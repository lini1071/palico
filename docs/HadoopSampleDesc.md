## Apache Hadoop 샘플 코드 설명 ##

### Contents ###

1. 개요
2. Mapper
3. RecordReader
4. FileInputFormat
5. RecordWriter
6. FileOutputFormat
7. Configuration & misc.

#### 1. 개요 ####

이 문서는 MapReduce 샘플 코드를 작성하면서 Apache Hadoop MapReduce 2.x 버전에서 Mapper의 구동을 위해 조사하고 작성했던 것들을 요약한 내용이다. 이 글은 내용들을 정리하고 기억하여 차후에 응용 수준을 높일 수 있게끔 하고자 하는 것을 주 목적으로 작성되었다. 한편으로는 MapReduce의 구성 요소의 관계에 대해 조금이나마 이해를 돕기 위한 보조 자료의 측면도 가질 수 있게끔 하려 했다.

![Hadoop MapReduce Concept Image](http://xiaochongzhang.me/blog/wp-content/uploads/2013/05/MapReduce_Work_Structure.png)


본 개요 부분 이후 Job Configuration과 기타를 제외하고 샘플 코드에서 구현한 사항은 클래스 각각에 대한 내용을 설명하고 있다. 데이터를 텍스트로 저장하고자 하는 경우는 대부분 Hadoop에서 미리 정의된 클래스들을 이용하여 처리가 가능하나, 이진 바이너리에 대한 처리를 사용자가 별도의 구성을 통해 구현하고자 하는 경우 Input/OutputFormat과 RecordReader/Writer 등을 정의해줄 필요가 있다. 본 문서에서는 수동으로 정의한 항목의 설명 대신 상위 클래스가 요구하는 기본 사항을 주로 기술하였다. 이번 샘플 코드에서는 Mapper만을 이용하므로 Reducer는 제외한다.


#### 2. Mapper ####

사용자가 새로 정의하고자 하는 Mapper 또는 Reducer는 각각 org.apache.hadoop.mapreduce 안의 Mapper와 Reducer를 상속해야 한다. K1-V1으로 이루어진 데이터쌍을 *K2*-*V2*로 다시 매핑한다고 하면, 상속받는 새로운 Mapper class 자체와 내부 *map* 함수 정의를 아래의 예와 같이 하여야 한다.

```java
import org.apache.hadoop.mapreduce.Mapper;

class ChildMapper extends Mapper<K1, V1, K2, V2>
{
	@Override
	public void map(K1 key, V1 value, Context context) throws IOException, InterruptedException
	{
		context.write((K2) key, (V2) value);
	}
}
```

위의 코드는 map 함수가 K1 타입의 key를 K2 타입으로, V1 타입의 value를 V2 타입으로 변환한다는 내용을 담았다. 
위 함수는 실제로 Hadoop MapReduce client core의 Mapper 클래스 Mapper.java에 기재된 소스 코드에서
사용되는 generic 식별자만 달리 취한 것이며 내용은 똑같다. 
context.write(key, value)를 호출하여 Map output 값을 다른 곳에 기록하게끔 하는데, 
샘플 코드에서는 실수 형태의 값을 갖게 될 value에 다른 값을 곱한 뒤 이를 기록하게 하였다.


MapTask.runNewMapper에서 MapTask 내부에 정의된 MapOutputBuffer 객체를 
NewOutputCollector로서 생성한 뒤 이를 위 map의 context로 넘겨주게 된다.
위 map에서의 context.write는 MapOutputBuffer.collect를 호출하는 것과 같다.
```java
// package org.apache.hadoop.mapred;
// MapTask.java

	@SuppressWarnings("unchecked")
	private <INKEY,INVALUE,OUTKEY,OUTVALUE>
	void runNewMapper(final JobConf job,
		final TaskSplitIndex splitIndex,
		final TaskUmbilicalProtocol umbilical,
		TaskReporter reporter
		) throws IOException, ClassNotFoundException,
			InterruptedException {

		......

		job.setBoolean(JobContext.SKIP_RECORDS, isSkipping());
		org.apache.hadoop.mapreduce.RecordWriter output = null;

		// get an output object

		if (job.getNumReduceTasks() == 0) {
			output = 
				new NewDirectOutputCollector(taskContext, job, umbilical, reporter);
		} else {
			output = new NewOutputCollector(taskContext, job, umbilical, reporter);
		}

		org.apache.hadoop.mapreduce.MapContext<INKEY, INVALUE, OUTKEY, OUTVALUE> 
			mapContext = 
			new MapContextImpl<INKEY, INVALUE, OUTKEY, OUTVALUE>(job, getTaskID(), 
				input, output, 
				committer, 
				reporter, split);

		...

	}
```


Hadoop 소스 코드에서 Mapper 클래스의 Mapper.run이 context.nextKeyValue() 값을 while로 확인해서
이가 true일 때마다 override된 Mapper.map을 호출하게 되는데, 그 부분은 아래와 같다.

```java
// package org.apache.hadoop.mapreduce;
// Mapper.java
public class Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {

	...

	/**
	 * Expert users can override this method for more complete control over the
	 * execution of the Mapper.
	 * @param context
	 * @throws IOException
	 */
	public void run(Context context) throws IOException, InterruptedException {
		setup(context);
		try {
			while (context.nextKeyValue()) {
				map(context.getCurrentKey(), context.getCurrentValue(), context);
			}
		} finally {
			cleanup(context);
		}
	}
}
```


이 Mapper.run으로 전달되는 context는 MapContextImpl로서 생성된 RecordReader 객체이다.
따라서 RecordReader를 사용자가 임의로 정의할 때에 몇몇 함수들은 신경써서 작성해야 한다.
해당하는 부분은 MapTask.runNewMapper 코드 부분을 봄으로써 알 수 있다.

```java
// package org.apache.hadoop.mapred;
// MapTask.java
	@SuppressWarnings("unchecked")
	private <INKEY,INVALUE,OUTKEY,OUTVALUE>
	void runNewMapper(final JobConf job,
			final TaskSplitIndex splitIndex,
			final TaskUmbilicalProtocol umbilical,
			TaskReporter reporter
			) throws IOException, ClassNotFoundException,
				InterruptedException {

		org.apache.hadoop.mapreduce.MapContext<INKEY, INVALUE, OUTKEY, OUTVALUE> 
		mapContext = 
			new MapContextImpl<INKEY, INVALUE, OUTKEY, OUTVALUE>(job, getTaskID(), 
				input, output, 
				committer, 
				reporter, split);

		org.apache.hadoop.mapreduce.Mapper<INKEY,INVALUE,OUTKEY,OUTVALUE>.Context 
		mapperContext = 
			new WrappedMapper<INKEY, INVALUE, OUTKEY, OUTVALUE>().getMapContext(
				mapContext);

		try {
			input.initialize(split, mapperContext);
			mapper.run(mapperContext);

			...
		}
	...
	}
```


#### 3. RecordReader ####

새로 정의하는 RecordReader 역시 generic으로 *K2*, *V2*를 넘기는 RecordReader로서 정의해야 한다.
위에서 나온 3개의 함수를 포함하여 RecordReader는 다음의 함수들을 구현하여야 한다.

* public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException
* public boolean nextKeyValue() throws IOException, InterruptedException
* public K2 getCurrentKey() throws IOException, InterruptedException
* public V2 getCurrentValue() throws IOException, InterruptedException
* public void close() throws IOException

다음 함수는 구현하여야 하나 작업의 진행도를 나타내는 것 외에는 실제로 중요한 기능을 하지는 않는다.

* public float getProgress() throws IOException, InterruptedException


아래의 코드는 Hadoop에서 기본적으로 정의해둔, 텍스트 줄을 읽을 때 사용하는 LineRecordReader 클래스 내용 중 일부이다. 
RecordReader는 아래와 같이 Apache Hadoop fs 패키지에 정의된 FSDataInputStream을 하나 선언한 뒤 파일 읽기 작업에 이용할 수 있다. 

```java
// package org.apache.hadoop.mapreduce.lib.input;
// LineRecordReader.java
...

public class LineRecordReader extends RecordReader<LongWritable, Text> {

	...

	public static final String MAX_LINE_LENGTH = 
		"mapreduce.input.linerecordreader.line.maxlength";
	private long start;
	private long pos;
	private long end;

	private SplitLineReader in;
	private FSDataInputStream fileIn;

	public LineRecordReader() {
	}

	public void initialize(InputSplit genericSplit,
		TaskAttemptContext context) throws IOException {

		FileSplit split = (FileSplit) genericSplit;
		Configuration job = context.getConfiguration();
		this.maxLineLength = job.getInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
		start = split.getStart();
		end = start + split.getLength();
		final Path file = split.getPath();

		// open the file and seek to the start of the split
		final FileSystem fs = file.getFileSystem(job);
		fileIn = fs.open(file);

		...
	}
	
	...

}
```

MapTask.runNewMapper에서 map.run을 수행하기 전 이 RecordReader 객체의 initialize를 반드시 호출하므로
initialize 메소드를 통해 InputSplit과 TaskAttemptContext 두 인자를 넘겨주는 것이 좋으며,
InputFormat 클래스에서 K1-V1 데이터 쌍을 읽어오기 위해 위와 똑같은 두 인자를
createRecordReader를 호출하여 RecordReader 생성 시 넘겨주기도 하나 반드시 이 때에 전달하지 않아도 된다.
```java
// package org.apache.hadoop.mapred;
// MapTask.java
	@SuppressWarnings("unchecked")
	private <INKEY,INVALUE,OUTKEY,OUTVALUE>
	void runNewMapper(final JobConf job,
			final TaskSplitIndex splitIndex,
			final TaskUmbilicalProtocol umbilical,
			TaskReporter reporter
			) throws IOException, ClassNotFoundException,
				InterruptedException {
		......


		try {
			input.initialize(split, mapperContext);
			mapper.run(mapperContext);

			...
		}
	...
	}
```

전달받는 InputSplit로는 getStart로 원본 파일의 Split 위치, getLength로 Split 크기를 알 수 있으므로
local stream의 seek와 read를 수행하는 것처럼 FSDataInputStream으로 HDFS의 파일에 대해 내용을 읽어올 수 있다.

	
#### 4. InputFormat ####

역시 기본 클래스 InputFormat을 상속하며, *K2*-*V2* generic으로 새로 작성한 InputFormat을 정의하여 준다.
우리가 FileInputFormat을 상속받는다는 클래스를 만든다고 하면 아래의 메소드는 반드시 정의하여야 한다.

* public RecordReader<K2, V2> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException

```java
@Override
public RecordReader<K2, V2> createRecordReader(InputSplit split, TaskAttemptContext context)
	throws IOException, InterruptedException {
  ...
	return new UserSpecifiedRecordReader();
}
```

위는 단순히 RecordReader 클래스 객체를 반환하게끔 하는 코드이다. Hadoop에서 사용하는 TextInputFormat.java를 살펴보더라도
createRecordReader는 다른 여러 코드에 비해 비교적 단순한 구조를 취하고 있음을 알 수 있다.

```java
// package org.apache.hadoop.mapreduce.lib.input;
// TextInputFormat.java
public class TextInputFormat extends FileInputFormat<LongWritable, Text> {
	
	@Override
	public RecordReader<LongWritable, Text> 
		createRecordReader(InputSplit split,
				TaskAttemptContext context) {
		String delimiter = context.getConfiguration().get(
			"textinputformat.record.delimiter");
		byte[] recordDelimiterBytes = null;
		if (null != delimiter)
			recordDelimiterBytes = delimiter.getBytes(Charsets.UTF_8);
		return new LineRecordReader(recordDelimiterBytes);
	}

	...

}
```


public long getFormatMinSplitSize()은 원본 파일에 대한 Split가 가져야 할 최소 크기를 정의한다.

```java
@Override
public long getFormatMinSplitSize() { return DESIRED_SIZE; }
```

#### 5. RecordWriter ####

사용자가 정의하는 RecordWriter는 보통 Mapper로 처리한 결과를 Reducer를 거친 후 출력할 때 필요하다.
Mapper를 통해 나온 K2-V2 데이터가 가상의 Reducer를 통해 *K3*-*V3*의 형태로 변환된다고 보면 RecordWriter 클래스를 다음과 같이 정의한다.

```java
public class CustomRecordWriter extends RecordWriter<K3, V3> { ... }
```

RecordWriter를 정의하기 위해서는 아래의 두 메소드를 반드시 정의해주어야 한다.

* public void write(K3 key, V3 value) throws IOException, InterruptedException
* public void close(TaskAttemptContext context) throws IOException, InterruptedException


이 RecordWriter가 제대로 정의되었을 경우 ReduceTask.runNewReducer에서
NewTrackingRecordWriter.real을 OutputFormat.getRecordWriter로 설정하여
결과가 write 메소드를 통해 출력될 수 있도록 해준다.

만일 프로젝트가 Reducer를 쓰지 않는다면, 즉 setNumReduceTasks(0)을 호출했다면 
Mapper.map에서의 context가 OutputFormat에서 얻는 RecordWriter와 직접 연결되므로 
Mapper에서의 출력을 RecordWriter로 바로 내보낼 수 있다.
MapTask.runNewMapper에서 Reducer가 있을 경우 NewOutputCollector를 생성하는 반면 
Reducer가 없을 경우 NewDirectOutputCollector을 생성하는데, 다시 NewOutputCollector는 
내부에서 버퍼로 사용될 collector를 만들어 이를 Mapper에 이어주나 NewDirectOutputCollector는
전달받은 OutputFormat에서 getRecordWriter를 호출하여 Writer를 반환받고 이를 출력으로 연결시킨다.
```java
// package org.apache.hadoop.mapred;
// MapTask.java
	@SuppressWarnings("unchecked")
	private <INKEY,INVALUE,OUTKEY,OUTVALUE>
		void runNewMapper(final JobConf job,
			final TaskSplitIndex splitIndex,
			final TaskUmbilicalProtocol umbilical,
			TaskReporter reporter
			) throws IOException, ClassNotFoundException,
				InterruptedException {

		...

		org.apache.hadoop.mapreduce.RecordReader<INKEY,INVALUE> input =
			new NewTrackingRecordReader<INKEY,INVALUE>
			(split, inputFormat, reporter, taskContext);

		job.setBoolean(JobContext.SKIP_RECORDS, isSkipping());
		org.apache.hadoop.mapreduce.RecordWriter output = null;

		// get an output object
		if (job.getNumReduceTasks() == 0) {
			output = 
				new NewDirectOutputCollector(taskContext, job, umbilical, reporter);
		} else {
			output = new NewOutputCollector(taskContext, job, umbilical, reporter);
		}

		...
	}
```


Apache Hadoop fs 패키지에 정의된 FSDataOutputStream을 하나 선언한 뒤 파일 쓰기 작업을 수행할 수 있다. 
작성하는 RecordWriter의 생성자를 통해 FSDataOutputStream을 전달받아 클래스 내부에 저장하고, 
write에서 이 stream에 대한 쓰기 작업을 수행하는 방식 등을 취해 데이터를 기록할 수 있다. 아래 코드는 샘플 코드에 사용된
실제 코드의 일부이다.


```java
...
import org.apache.hadoop.mapreduce.RecordWriter;

public class FloatRecordWriter extends RecordWriter<LongWritable, FixedLengthFloatArrayWritable> {
	
	private FSDataOutputStream oStream;
	
	public FloatRecordWriter(FSDataOutputStream stream) {
		oStream = stream;
	}

	...
}
```

#### 6. OutputFormat ####

기본 클래스 OutputFormat을 상속하며, *K3*-*V3* generic으로 새로 작성한 OutputFormat을 정의하여 준다.
FileOutputFormat을 상속받는다는 클래스를 만든다고 하면 아래의 메소드를 반드시 정의해야 한다.

```java
public RecordWriter<K3, V3> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException
```

아래의 코드는 TextOutputFormat.java의 내용 중 일부를 참조해 작성되었으며 본 샘플에서 사용하였다. Path에 대한 적당한 FSDataOutputStream을 생성하고 이를 RecordWriter를 생성함과 동시에 인자로 넘기어 RecordWriter에서 차후 파일 쓰기 작업을 수행할 수 있게끔 하는 코드이다.

```java
// referenced TextOutputFormat.java
// 	: package org.apache.hadoop.mapreduce.output;
	@Override
	public RecordWriter<K3, V3>
		getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {

		Configuration conf = context.getConfiguration();
		String extension = "";
		Path file = getDefaultWorkFile(context, extension);
		FileSystem fs = file.getFileSystem(conf);
		FSDataOutputStream fileOut = fs.create(file, false);
		return new UserSpecifiedRecordWriter(fileOut);
	}
```



#### 7. Configuration & misc. ####

RunJar를 통해 실행될 클래스의 main 메소드는 Job을 submit하기 전에
Job에 대한 Configuration 객체의 내용을 설정해주어야 한다.
아래와 같이 Job 객체를 생성한 뒤에 Job 객체에 대한 몇몇 메소드를 호출한다.

```java
Configuration conf = new Configuration();
Job job = Job.getInstance(conf, __Job name in String__);
```

* Job.setJarByClass(__User-specified Main Class(.class)__)
* Job.setMapperClass(__User-specified Mapper Class(.class)__)
* Job.setReducerClass(__User-specified Reducer Class(.class)__)
	Main 메소드를 포함한 클래스, Mapper 클래스, Reducer 클래스를 지정한다.
	Reducer나 Combiner, Partitioner 등은 필요가 없을 경우 설정하지 않아도 무방하다.
	Reducer가 없을 경우 Job.setNumReduceTasks(0)을 추가로 호출하여 준다.
		
* Job.setInputFormatClass(__User-specified InputFormat Class(.class)__)
* Job.setOutputFormatClass(__User-specified OutputFormat Class(.class)__)
	사용자 정의된 InputFormat과 OutputFormat을 이용하겠음을 전달한다.
	LongWritable과 같은 Hadoop에 미리 정의된 Format을 지정할 수도 있다.

* Job.setMapOutputKeyClass(*K2*.class)
* Job.setMapOutputValueClass(*V2*.class)
	Map 결과의 데이터 포맷을 설정한다.

* Job.setOutputKeyClass(__K3(.class)__)
* Job.setOutputValueClass(__V3(.class)__)
	최종 결과의 데이터 포맷을 설정한다.
		
* __FileInputFormat__.addInputPath(job, new Path(__...__))
* __FileOutputFormat__.setOutputPath(job, new Path(__...__))
* __FileOutputFormat__.setCompressOutput(job, false)
	FileInput의 경로와 FileOutput의 경로를 설정하고 Output을 압축할 것인지를 설정한다.

* Job.waitForCompletion(__boolean__)
	설정이 끝난 Job을 수행시켜달라 요청하는 메소드.
	waitForCompletion은 작업이 끝날 때까지 기다린다(Thread.sleep).
	__boolean__ 값이 true일 경우 작업 처리에 관한 상세 내용이 출력된다.
	작업이 완료되기를 기다리지 않고자 하면 waitForCompletion 대신 submit을 호출한다.
