## Apache Hadoop 샘플 코드 설명 ##

1. Mapper
2. RecordReader
3. FileInputFormat
4. RecordWriter
5. FileOutputFormat
6. Configuration & misc.


#### 1. Mapper

사용자가 새로 정의하고자 하는 Mapper 또는 Reducer는 각각 org.apache.hadoop.mapreduce 안의 Mapper와 Reducer를 상속해야 한다. 이번 샘플 코드에서는 Mapper만을 이용하므로 Reducer는 제외한다. K1-V1으로 이루어진 데이터쌍을 *K2*-*V2*로 다시 매핑한다고 하면, 상속받는 새로운 Mapper class 자체와 내부 *map* 함수를 다음의 형태로 정의해두어야 한다.

```java
class ChildMapper extends Mapper<K1, V1, K2, V2>
{
	@Override
	public void map(K2 key, V2 value, Context context) throws IOException, InterruptedException
	{
		...
	}
}
```

일반적으로 Map output 값을 기록하기 위해서 map에서는 context.write(key, value)를 호출한다.
샘플 코드에서는 실수 형태의 값을 갖게 될 value에 다른 값을 곱한 뒤 이를 기록하였다.

MapTask.runNewMapper에서 MapTask 내부에 정의된 MapOutputBuffer 객체를
NewOutputCollector로서 생성한 뒤 이를 위 map의 context로 넘겨주게 된다.
위 map에서의 context.write는 MapOutputBuffer.collect를 호출하는 것과 같다.

Hadoop 소스 코드에서 Mapper 클래스의 run이 context.nextKeyValue() 값을 while로 확인해서
이가 true일 때마다 override된 Mapper.map을 호출하게 되는데, 그 부분은 아래와 같다.

```java
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
```

이 Mapper.run으로 전달되는 context는 MapContextImpl로서 생성된 RecordReader 객체이다.
따라서 RecordReader를 사용자가 임의로 정의할 때에 몇몇 함수들은 신경써서 작성해야 한다.


#### 2. RecordReader

새로 정의하는 RecordReader 역시 generic으로 *K2*, *V2*를 넘기는 RecordReader로서 정의해야 한다.
위에서 나온 3개의 함수를 포함하여 RecordReader는 다음의 함수들을 구현하여야 한다.

* public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException
* public boolean nextKeyValue() throws IOException, InterruptedException
* public K2 getCurrentKey() throws IOException, InterruptedException
* public V2 getCurrentValue() throws IOException, InterruptedException
* public void close() throws IOException

아래의 함수는 구현하여야 하나 작업의 진행도를 나타내는 것 외에는 실제로 중요한 기능을 하지는 않는다.

* public float getProgress() throws IOException, InterruptedException


Apache Hadoop fs 패키지에 정의된 FSDataInputStream을 하나 선언한 뒤 파일 읽기 작업을 수행할 수 있다.
MapTask.runNewMapper에서 map.run을 수행하기 전 이 RecordReader 객체의 initialize를 반드시 호출하므로
initialize 메소드를 통해 InputSplit과 TaskAttemptContext 두 인자를 넘겨주는 것이 좋으며,
InputFormat 클래스에서 K1-V1 데이터 쌍을 읽어오기 위해 위와 똑같은 두 인자를
createRecordReader를 호출하여 RecordReader 생성 시 넘겨주기도 하나 반드시 이 때에 전달하지 않아도 된다.

전달받는 InputSplit로는 getStart로 원본 파일의 Split 위치, getLength로 Split 크기를 알 수 있으므로
local stream의 seek와 read를 수행하는 것처럼 FSDataInputStream으로 HDFS의 파일에 대해 내용을 읽어올 수 있다.


#### 3. InputFormat

역시 기본 클래스 InputFormat을 상속하며, *K2*-*V2* generic으로 새로 작성한 InputFormat을 정의하여 준다.
우리가 FileInputFormat을 상속받는다는 클래스를 만든다고 하면 아래의 메소드는 반드시 정의하여야 한다.

* public RecordReader<K2, V2> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException

일반적으로 이는 RecordReader 클래스 객체를 반환한다.

```java
@Override
public RecordReader<K2, V2> createRecordReader(InputSplit split, TaskAttemptContext context)
	throws IOException, InterruptedException {
  ...
	return new UserSpecifiedRecordReader();
}
```

public long getFormatMinSplitSize()은 원본 파일에 대한 Split가 가져야 할 최소 크기를 정의한다.

```java
@Override
public long getFormatMinSplitSize() { return DESIRED_SIZE; }
```

#### 4. RecordWriter

사용자가 정의하는 RecordWriter는 보통 Mapper로 처리한 결과를 별도의 과정을 통해 최종 파일로 출력할 때 필요하다.
이 클래스를 Mapper.map을 수행한 직후의 중간 결과값을 저장하기 위해 사용하는 것은 부적합하다.
Mapper를 통해 나온 K2-V2 데이터가 *K3*-*V3*의 형태로 변환된다고 보면 RecordWriter 클래스를 다음과 같이 정의한다.

```java
public class RecordWriter extends RecordWriter<K3, V3> { ... }
```

RecordWriter를 정의하기 위해서는 아래의 두 메소드를 반드시 정의해주어야 한다.

* public void write(K3 key, V3 value) throws IOException, InterruptedException
* public void close(TaskAttemptContext context) throws IOException, InterruptedException


이 RecordWriter가 제대로 정의되었을 경우 ReduceTask.runNewReducer에서
NewTrackingRecordWriter.real을 OutputFormat.getRecordWriter로 설정하여
결과가 write 메소드를 통해 출력될 수 있도록 해준다.

Apache Hadoop fs 패키지에 정의된 FSDataOutputStream을 하나 선언한 뒤 파일 쓰기 작업을 수행할 수 있다.
작성하는 RecordWriter의 생성자를 통해 FSDataOutputStream을 전달받아 클래스 내부에 저장하고,
write에서 이 stream에 대한 쓰기 작업을 수행하는 방식 등을 취해 데이터를 기록할 수 있다.


#### 5. OutputFormat

기본 클래스 OutputFormat을 상속하며, *K3*-*V3* generic으로 새로 작성한 OutputFormat을 정의하여 준다.
FileOutputFormat을 상속받는다는 클래스를 만든다고 하면 아래의 메소드를 반드시 정의해야 한다.

```java
public RecordWriter<K3, V3> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException
```

평이한 다음의 코드를 사용할 수도 있다.

```java
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

Path에 대한 적당한 FSDataOutputStream을 생성하고 이를 RecordWriter를 생성함과 동시에 인자로 넘기어
RecordWriter에서 파일 쓰기 작업을 수행할 수 있게끔 하는 코드이다.

#### 6. Configuration & misc.

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
	최종 결과의 데이터 포맷 *K3*-*V3*과 중간 결과 포맷 K2-V2가 명확히 구별되어야 할 경우 호출될 수 있다.

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
