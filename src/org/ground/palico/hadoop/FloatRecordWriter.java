package org.ground.palico.hadoop;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FloatRecordWriter extends RecordWriter<LongWritable, FixedLengthFloatArrayWritable> {
	
	// It may be a network stream. So we can't use seek.
	private FSDataOutputStream oStream;
	
	private byte[] block_buf;
	private ByteBuffer buf_wrap;

	public FloatRecordWriter(FSDataOutputStream stream, int length) {
		oStream = stream;
		this.block_buf = new byte[length * Float.BYTES];
		this.buf_wrap = ByteBuffer.wrap(block_buf);
	}
	
	@Override
	public synchronized void write(LongWritable key, FixedLengthFloatArrayWritable value) throws IOException, InterruptedException {

		buf_wrap.clear();

		int valueLength = value.getLength();
		for (int i = 0 ; i < valueLength ; i++)
		{
			// get each float value and write to buffer.
			buf_wrap.putFloat(value.get(i).get());
		}
		
		// write buffer to stream
		oStream.write(block_buf, 0, valueLength * Float.BYTES);
	}

	@Override
	public synchronized void close(TaskAttemptContext context) throws IOException, InterruptedException {

		buf_wrap = null;
		block_buf = null;
		
		oStream.close();
	}
}