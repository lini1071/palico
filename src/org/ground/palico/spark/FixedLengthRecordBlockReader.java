package org.ground.palico.spark;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class FixedLengthRecordBlockReader<T extends Writable>
	extends RecordReader<LongWritable, FixedLengthRecordBlock<T>> {

	// File pointer position
	private long fpStart;
	private long fpPos;
	private long fpEnd;
	private FSDataInputStream iStream;
    
	// Actual key & value object :
	// 		values are fixed(don't call more new method)
	// Referenced from org.apache.hadoop.mapreduce.lib.LineRecordReader;
	private LongWritable key = new LongWritable();
	private FixedLengthRecordBlock<T> values;
	private Class<T> classType;
	
	// variables for file i/o operation
	private int recordSize;
	private int numRecords;
	private int size_buf;
	
	// buf : main buffer, wrap_buf : wrapper
	private byte[] buf;
	private ByteBuffer wrap_buf;
	
	private InputSplit split;
	private TaskAttemptContext context;
	
	public FixedLengthRecordBlockReader(int numRec)
	{
		this.numRecords = numRec;
		
		this.classType = //(ParameterizedType) getClass().getGenericSuperclass()
				(Class<T>) ((ParameterizedType) getClass()
                        .getGenericSuperclass()).getActualTypeArguments()[0];
		this.size_buf = rSize * numRec;
	}
	
	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
		throws IOException, InterruptedException {
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
		this.wrap_buf = ByteBuffer.wrap(this.buf);
		try
		{
			this.values = new FixedLengthRecordBlock<T>(this.classType, this.numRecords);
		}
		catch (Exception e)
		{
			throw new IOException();
		}
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
    	
		// in.available() and (fpPos - fpEnd) may be different
		// for the file which size is larger than block size!
		/*
		System.out.println("in.available() : " + iStream.available() +
				", fpEnd - fpPos = " + String.valueOf(fpEnd - fpPos));
		*/
		
		if (fpPos < fpEnd) {
			
			byte[] ptrBuffer = null;
			ByteBuffer bb;
			int bufferLength;
			
			// set position, clear buffer and read
			key.set(fpPos);
		
			if ((fpPos + size_buf) <= fpEnd) {
				// we can read entire buffer size of data.
				ptrBuffer 	= this.buf;
				bb 			= this.wrap_buf;
				
				bufferLength = values.getBufferLength();
					
				fpPos += size_buf;
			} else {
				// can over bound
				int partSize = (int) (fpEnd - fpPos);
				byte[] tmpBuffer = new byte[partSize];
				
				ptrBuffer = tmpBuffer;
				bb = ByteBuffer.wrap(ptrBuffer);
				
				int bLen = partSize / Float.BYTES;
				values.setPartLength(bLen);
				bufferLength = bLen;
				
				fpPos += partSize;
			}
			wrap_buf.clear();
			iStream.readFully(ptrBuffer);
			for (int i = 0 ; i < bufferLength ; i++)
			{
				// set inner ArrayWritable values
				//values.get(i).set(bb.getFloat());
			}
			
			/*
			// set position, clear buffer and read
			key.set(fpPos);
		
			if ((fpPos + size_buf) <= fpEnd) {
				// we can read entire buffer size of data.
				fpPos += size_buf;
			} else {
				// can over bound
				int partSize = (int) (fpEnd - fpPos);
				int bLen = partSize / Float.BYTES;
				values.setPartLength(bLen);
				
				fpPos += partSize;
			}
			*/
			
			values.readFields(iStream);
			
			return true;
		}
		else return false;
	}

	@Override
	public LongWritable getCurrentKey() throws IOException, InterruptedException {
		return this.key;
	}

	@Override
	public FixedLengthRecordBlock<T> getCurrentValue() throws IOException, InterruptedException {
		return this.values;
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
