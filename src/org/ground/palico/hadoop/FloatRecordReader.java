package org.ground.palico.hadoop;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class FloatRecordReader extends RecordReader<LongWritable, FloatWritable> {
	
    // File pointer position
    private long fpStart;
    private long fpPos;
    private long fpEnd;
    private InputSplit split;
    private TaskAttemptContext context;
    
    protected LongWritable key = new LongWritable();
    protected FloatWritable value = new FloatWritable();
    private FSDataInputStream iStream;

    public FloatRecordReader() {

    }

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        this.split = split;
        this.context = context;
        
        FileSplit fSplit = (FileSplit) split;
        Configuration job = context.getConfiguration();
        
        // Split "S" is responsible for all records
        // starting from "start" and "end" positions
        fpStart = fSplit.getStart();
        fpEnd = fpStart + fSplit.getLength();

        // Retrieve file containing Split "S"
        final Path file = fSplit.getPath();
        FileSystem fs = file.getFileSystem(job);
        iStream = fs.open(fSplit.getPath());

        // Move file pointer of the file we are accessing.
        fpPos = fpStart;
        if (0 < fpStart) iStream.seek(fpStart);
    }

    @Override
    public synchronized boolean nextKeyValue() throws IOException, InterruptedException {
		
        if (fpPos < fpEnd) {
            key.set(fpPos);
            value.set(iStream.readFloat());
            fpPos += ((long) Float.BYTES);
            return true;
        } else return false;
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {
        return key;
    }
    
    @Override
    public FloatWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        // t : total data size, c : currently progressed size
        float t = (float) (fpEnd - fpStart);
        float c = (float) (fpPos - fpStart);

        // avoiding DivideByZero
        return (t != 0.0f ? (c / t) : 0.0f);
    }
    
    @Override
    public synchronized void close() throws IOException {
        iStream.close();
    }
    
    /*
    public InputSplit getInputSplit() {
    	return split;
    }
    
    public TaskAttemptContext getTaskAttemptContext() {
    	return context;
    }
    */

}