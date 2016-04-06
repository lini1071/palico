package org.ground.palico.hadoop;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

public class FloatRecordWriter extends RecordWriter<LongWritable, FloatWritable> {
	private FSDataOutputStream oStream;
	
	public FloatRecordWriter(FSDataOutputStream stream) {
		oStream = stream;
	}
	
	public void write(LongWritable key, FloatWritable value) throws IOException, InterruptedException {
		oStream.writeFloat(value.get());
	}
	
	public void close(TaskAttemptContext context) throws IOException, InterruptedException {
		oStream.close();
	}
}