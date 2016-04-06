package org.ground.palico.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;

public class ImageGeneratorForHadoop {
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("mapreduce.output.basename", "result");
		Job job = Job.getInstance(conf, "File Read & Calculation");
		
		// Set input & output path
		job.setJarByClass(MergeReducer.class);
		job.setMapperClass(CalculatorMapper.class);
		job.setReducerClass(MergeReducer.class);
		
		job.setInputFormatClass(FloatRecordInputFormat.class);
		job.setOutputFormatClass(FloatRecordOutputFormat.class);
		FloatRecordInputFormat.addInputPath(job, new Path(args[0]));
		FloatRecordOutputFormat.setOutputPath(job, new Path(args[1]));
		FloatRecordOutputFormat.setCompressOutput(job, false);
		
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(FloatWritable.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(FloatWritable.class);
		
		// Pin start time. Submit the job and wait for completion
		long tStart = System.nanoTime();
		job.waitForCompletion(true);
		
		// Calculate elapsed time, and print out
		long tEnd = System.nanoTime();
		long tPerf = tEnd - tStart;
		System.out.println("job.isSuccessful() == " + Boolean.toString(job.isSuccessful()));
		System.out.println("Elapsed time : " + tPerf + "ns");
	}
}
