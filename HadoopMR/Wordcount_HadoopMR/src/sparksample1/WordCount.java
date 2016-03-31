package sparksample1;

import java.io.IOException;
import java.util.StringTokenizer;

// dependency ; org.apache.hadoop, hadoop-common
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

// dependency ; org.apache.hadoop, hadoop-mapreduce-client-core
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount
{
	public static class TokenizerMapper
		extends Mapper<Object, Text, Text, IntWritable>
	{
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException
		{
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens())
			{
				word.set(itr.nextToken());
				context.write(word, one);
			};
		};
	}

	public static class IntSumReducer
		extends Reducer<Text, IntWritable, Text, IntWritable>
	{
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException
		{
			int sum = 0;
			for (IntWritable val : values)
			{
				sum += val.get();
			};

			result.set(sum);
			context.write(key, result);
		};
	}

	public static void main(String[] args) throws Exception
	{
		// time-storing variables
		long tStart, tEnd, tPerf;
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");

		job.setJarByClass(sparksample1.WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// Set input & output path
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		// Pin start time. Submit the job and wait for completion
		tStart = System.nanoTime();
		job.waitForCompletion(true);
		
		// Calculate elapsed time, and print out
		tEnd = System.nanoTime();
		tPerf = tEnd - tStart;
		System.out.println("Elapsed time : " + tPerf + "ns");
		
		// end of main function
	};
}