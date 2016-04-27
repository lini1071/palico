package org.ground.palico.spark;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class FloatRecordBlockReader extends RecordReader<LongWritable, FloatWritable> {

	// File pointer position
	private long fpStart;
	private long fpPos;
	private long fpEnd;
	private FSDataInputStream iStream;
    
	private LongWritable key = new LongWritable();
	private FloatWritable value = new FloatWritable();
	private long keyCounter;
	
	// variables for file i/o operation
	private int recordSize;
	private int numRecords;
	private int size_buf;
	
	// class-primary buffer, and wrapper variable
	private byte[] buf;
	private ByteBuffer wrap_buf;
	
	@SuppressWarnings("unused")
	private InputSplit split;
	
	@SuppressWarnings("unused")
	private TaskAttemptContext context;
	
	public FloatRecordBlockReader(int rSize, int numRec)
	{
		this.recordSize = rSize;
		this.numRecords = numRec;
		this.size_buf = rSize * numRec;
	}
	
	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		this.split = split;
		this.context = context;
		
		FileSplit fSplit = (FileSplit) split;
		Configuration job = context.getConfiguration();
		long sLength = fSplit.getLength();
		
		// Split "S" is responsible for all records
		// starting from "start" and "end" positions
		fpStart = fSplit.getStart();
		fpEnd = fpStart + sLength;
		
		// Retrieve file containing Split "S"
		final Path file = fSplit.getPath();
		FileSystem fs = file.getFileSystem(job);
		iStream = fs.open(fSplit.getPath());
		
		// Move file pointer of the file we are accessing.
		fpPos = fpStart;
		if (0 < fpStart) iStream.seek(fpStart);
		
		// initialize inner variables
		this.buf 		= new byte[(int) this.size_buf];
		this.keyCounter = (this.fpStart / (long) this.recordSize);
		
		// initialize ByteBuffer variable
		this.wrap_buf = ByteBuffer.wrap(this.buf);
		this.wrap_buf.asFloatBuffer().clear();
		this.wrap_buf.position( this.wrap_buf.capacity() );
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
    	
		// in.available() and (fpPos - fpEnd) may be different
		// for the file which size is larger than block size!
		/*
		System.out.println("in.available() : " + iStream.available() +
				", fpEnd - fpPos = " + String.valueOf(fpEnd - fpPos));
		*/
		
		//if (0 < wrap_buf.remaining())
		if (wrap_buf.hasRemaining())
		{
			key.set(this.keyCounter++);
			value.set(wrap_buf.getFloat());
			return true;
		}
		else if (fpPos < fpEnd)
		{
			wrap_buf.clear();		// clearing (Byte)Buffer
									// The limit of flipped Buffer is defined on other objects.

			int readSize;
			if ((fpPos + size_buf) <= fpEnd) {
				// we can read entire buffer size of data.
				fpPos += size_buf;
				readSize = size_buf;
			} else {
				// can over bound
				int partSize = (int) (fpEnd - fpPos);
				fpPos += partSize;
				
				readSize = partSize;
				wrap_buf.limit(partSize);	// unit size is byte.
			}
			
			// block i/o processing :
			// read(ByteBuffer) & read(byte[]) performs loading pre-defined data size only.
			// so we must use readFully(byte[]) instead of read(ByteBuffer) & read(byte[]).
			iStream.readFully(buf, 0, readSize);
			
			return nextKeyValue(); 	// return true;
		}
		else return false;
	}

	@Override
	public LongWritable getCurrentKey() throws IOException, InterruptedException {
		return this.key;
	}

	@Override
	public FloatWritable getCurrentValue() throws IOException, InterruptedException {
		return this.value;
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
}
