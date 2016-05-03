package org.ground.palico.spark;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class FixedLengthRecordBlockWriter<K, V extends Writable> extends RecordWriter<K, V> {

	private FSDataOutputStream fsOutStream;		// actual stream
	private BufferedOutputStream buffOutStream;	// for making buffer
	private DataOutputStream dataOutStream;		// for wrapping method write()
	private OutputStream oStream;	// for using close()
	
	public FixedLengthRecordBlockWriter(FSDataOutputStream stream, int bufSize)
	{
		this.fsOutStream = stream;
		this.buffOutStream = new BufferedOutputStream(stream, bufSize);
		this.dataOutStream = new DataOutputStream(buffOutStream);
		
		this.oStream = this.dataOutStream;
	}
	
	@Override
	public synchronized void write(K key, V value) throws IOException, InterruptedException {
		// wrapped BufferedOutputStream..
		value.write(dataOutStream);
	}
	
	@Override
	public synchronized void close(TaskAttemptContext context) throws IOException, InterruptedException {
		oStream.close();
	}
	
}
