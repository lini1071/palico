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
<<<<<<< Updated upstream
	public synchronized void write(LongWritable key, FloatWritable value) throws IOException, InterruptedException {
//		oStream.writeFloat(value.get());
		oStream.writeFloat((float)(oStream.getPos()));
=======
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
>>>>>>> Stashed changes
	}
	
	@Override
	public synchronized void close(TaskAttemptContext context) throws IOException, InterruptedException {
		oStream.close();
	}
}