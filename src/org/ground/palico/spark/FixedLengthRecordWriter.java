package org.ground.palico.spark;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.mortbay.log.Log;

import java.io.IOException;

public class FixedLengthRecordWriter<K, V extends Writable> extends RecordWriter<K, V> {
	
	// It may be a network stream. So we can't use seek.
	private FSDataOutputStream oStream;
	
	public FixedLengthRecordWriter(FSDataOutputStream stream) {
		oStream = stream;
	}
	
	@Override
	public synchronized void write(K key, V value) throws IOException, InterruptedException {
		// write float value to stream
		value.write(oStream);
	}

	@Override
	public synchronized void close(TaskAttemptContext context) throws IOException, InterruptedException {
		oStream.close();
	}
}