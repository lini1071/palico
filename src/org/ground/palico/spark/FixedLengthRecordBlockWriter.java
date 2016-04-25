package org.ground.palico.spark;

import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class FixedLengthRecordBlockWriter<K, V extends Writable> extends RecordWriter<K, V> {

	private FSDataOutputStream oStream;
	
	public FixedLengthRecordBlockWriter(FSDataOutputStream stream)
	{
		oStream = stream;
	}
	
	@Override
	public synchronized void write(K key, V value) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		value.write(oStream);
	}
	
	@Override
	public synchronized void close(TaskAttemptContext context) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		oStream.close();
	}
	
}
