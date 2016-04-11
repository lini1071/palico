package org.ground.palico.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;

public class CalcMainClassForHadoop {
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.setInt("CONF_NUM_RECORDS_BLOCK", Integer.parseInt(args[0]));
		
		Job job = Job.getInstance(conf, "File Read & Calculation");
		
		// Set input & output path
		job.setJarByClass(CalcMainClassForHadoop.class);
		job.setMapperClass(CalculatorMapper.class);
		job.setNumReduceTasks(0);
		
		job.setInputFormatClass(FloatRecordInputFormat.class);
		job.setOutputFormatClass(FloatRecordOutputFormat.class);
		FloatRecordInputFormat.addInputPath(job, new Path(args[1]));
		FloatRecordOutputFormat.setOutputPath(job, new Path(args[2]));
		FloatRecordOutputFormat.setCompressOutput(job, false);
		
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(FixedLengthFloatArrayWritable.class);
		
		// Pin start time. Submit the job and wait for completion
		long tStart = System.currentTimeMillis();
		job.waitForCompletion(true);
		
		// Calculate elapsed time, and print out
		long tEnd = System.currentTimeMillis();
		long tPerf = tEnd - tStart;
		System.out.println("job.isSuccessful() == " + Boolean.toString(job.isSuccessful()));
		System.out.println("Elapsed time : " + tPerf + "ms");
	}
}
