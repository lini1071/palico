package org.ground.palico.spark;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class FixedLengthBytesWritableReader
	extends RecordReader<LongWritable, FixedLengthBytesWritable> {

	private LongWritable key;
	private FixedLengthBytesWritable value;
	
	private long fpStart;
	private long fpPos;
	private long fpEnd;
	private FSDataInputStream iStream;
	
	private int blockSize;

	public FixedLengthBytesWritableReader(int blockSize)
	{
		this.blockSize = blockSize;
		
		this.key 	= new LongWritable();
		this.value 	= new FixedLengthBytesWritable(new byte[blockSize]);
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
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
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
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {

		// in.available() and (fpPos - fpEnd) may be different
		// for the file which size is larger than block size!
		/*
		System.out.println("in.available() : " + iStream.available() +
				", fpEnd - fpPos = " + String.valueOf(fpEnd - fpPos));
		*/
		
		if (fpPos < fpEnd)
		{
			int readSize;
			if ((this.fpPos + this.blockSize) <= fpEnd) {
				// we can read entire buffer size of data.
				fpPos += blockSize;
				readSize = blockSize;
			} else {
				// can over bound
				int partSize = (int) (fpEnd - fpPos);
				fpPos += partSize;
				
				this.value.setSize(partSize);
				readSize = partSize;
			}
			
			// block i/o processing :
			// read(ByteBuffer) & read(byte[]) performs loading pre-defined data size only.
			// so we must use readFully(byte[]) instead of read(ByteBuffer) & read(byte[]).
			iStream.readFully(this.value.getBytes(), 0, readSize);
			
			return true;
		}
		else return false;
	}
	
	
	@Override
	public LongWritable getCurrentKey() throws IOException, InterruptedException {
		return this.key;
	}

	@Override
	public FixedLengthBytesWritable getCurrentValue() throws IOException, InterruptedException {
		return this.value;
	}

	
	@Override
	public void close() throws IOException {
		this.iStream.close();
	}

}
