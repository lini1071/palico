package com.satreci.palico.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ImageGeneratorForHadoop {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "File Read & Calculation");

        // Set input & output path
        job.setJarByClass(MergeReducer.class);
        job.setMapperClass(CalculatorMapper.class);
        job.setReducerClass(MergeReducer.class);

        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(FloatWritable.class);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(FloatWritable.class);

        job.setInputFormatClass(FloatRecordInputFormat.class);
        job.setOutputFormatClass(FloatRecordOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

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
