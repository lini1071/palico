package org.ground.palico.hadoop;

import org.ground.palico.spark.FixedLengthBytesWritable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;

import org.mortbay.log.Log;

public class CalcMainClassForHadoop {
	public static void main(String[] args) throws Exception {
		int preferSize = Integer.parseInt(args[0]);
		
		Configuration conf = new Configuration();

		// get file block size for adjust by accesing file system
		Path path = new Path(args[1]);
		FileSystem fs = path.getFileSystem(conf);
		long blockSize = fs.getFileStatus(path).getBlockSize(); 
		
		conf.setInt("CONF_RECORD_SIZE", Float.BYTES);
		if (blockSize < (long) preferSize)
		{
			Log.warn("Block size error : Block size " + blockSize + " is smaller than "
				+ preferSize + ". Setting block size to " + blockSize + "...");
			conf.setInt("CONF_BLOCK_SIZE", (int) blockSize);
		}
		else conf.setInt("CONF_BLOCK_SIZE", preferSize);
		
		Job job = Job.getInstance(conf, "File Block Read & Calculation");
		
		// Set input & output path
		job.setJarByClass(CalcMainClassForHadoop.class);
		job.setMapperClass(CalculatorMapper.class);
		job.setNumReduceTasks(0);
		
		/*
		job.setInputFormatClass(FloatRecordInputFormat.class);
		job.setOutputFormatClass(FloatRecordOutputFormat.class);
		FloatRecordInputFormat.addInputPath(job, new Path(args[1]));
		FloatRecordOutputFormat.setOutputPath(job, new Path(args[2]));
		FloatRecordOutputFormat.setCompressOutput(job, false);
		*/
		job.setInputFormatClass(org.ground.palico.spark.FixedLengthBytesWritableInputFormat.class);
		job.setOutputFormatClass(org.ground.palico.spark.FixedLengthRecordBlockOutputFormat.class);
		org.ground.palico.spark.FixedLengthBytesWritableInputFormat.addInputPath(job, new Path(args[1]));
		org.ground.palico.spark.FixedLengthRecordBlockOutputFormat.setOutputPath(job, new Path(args[2]));
		
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(FixedLengthBytesWritable.class);
		
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
