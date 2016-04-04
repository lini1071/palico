package temp_packname;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MainClass
{
	public static void main(String[] args) throws Exception
	{
		// time-storing variables
		long tStart, tEnd, tPerf;
		
		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf, "File Read & Calculation");

		// Set input & output path
		job.setJarByClass(temp_packname.HadoopApplication.class);
		job.setMapperClass(temp_packname.HadoopApplication.CalculatorMapper.class);
		job.setReducerClass(temp_packname.HadoopApplication.MergeReducer.class);

		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(FloatWritable.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(FloatWritable.class);

		job.setInputFormatClass(temp_packname.HadoopApplication.FloatRecordInputFormat.class);
		job.setOutputFormatClass(temp_packname.HadoopApplication.FloatRecordOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		// Pin start time. Submit the job and wait for completion
		tStart = System.nanoTime();
		job.waitForCompletion(true);
		
		// Calculate elapsed time, and print out
		tEnd = System.nanoTime();
		tPerf = tEnd - tStart;
		System.out.println("job.isSuccessful() == " + Boolean.toString(job.isSuccessful()));
		System.out.println("Elapsed time : " + tPerf + "ns");
		
		// end of main function
	};
}
