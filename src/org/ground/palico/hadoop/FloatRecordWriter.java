package org.ground.palico.hadoop;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

public class FloatRecordWriter extends RecordWriter<LongWritable, FloatWritable> {
	
	// It may be a network stream. So we can't use seek.
	private FSDataOutputStream oStream;
	
	public FloatRecordWriter(FSDataOutputStream stream) {
		oStream = stream;
	}
	
	@Override
	public synchronized void write(LongWritable key, FloatWritable value) throws IOException, InterruptedException {
//		oStream.writeFloat(value.get());
		oStream.writeFloat((float)(oStream.getPos()));
	}
	
	@Override
	public synchronized void close(TaskAttemptContext context) throws IOException, InterruptedException {
		oStream.close();
	}
}