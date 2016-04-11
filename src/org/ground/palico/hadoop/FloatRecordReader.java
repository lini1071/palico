package org.ground.palico.hadoop;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.mortbay.log.Log;

import java.io.IOException;

public class FloatRecordReader extends RecordReader<LongWritable, FloatWritable> {
	
    // File pointer position
    private long fpStart;
    private long fpPos;
    private long fpEnd;
    private InputSplit split;
    private TaskAttemptContext context;
    
    protected LongWritable key = new LongWritable();
    protected FloatWritable value = new FloatWritable();
    private FSDataInputStream iStream;

<<<<<<< Updated upstream
    public FloatRecordReader() {

    }
=======
	// variables for file i/o operation
	private int numRecords;
	private int size_buf;
	
	private byte[] buf;
	private ByteBuffer wrap_buf;
	
	public FloatRecordReader(int length) {
		this.numRecords = length;
		this.size_buf = Float.BYTES * numRecords;
	}
>>>>>>> Stashed changes

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
        
        
        
		// check block size and initialize inner variables
		if (sLength < this.size_buf)
		{
			Log.warn("Block size error : File Split size " + sLength + " is smaller than "
				+ size_buf + ". Setting block size to " + sLength + "...");
			this.size_buf = (int) sLength;
			this.numRecords = ((int) (this.size_buf / 4));
		}
		
		this.buf 		= new byte[(int) this.size_buf];
		this.wrap_buf = ByteBuffer.wrap(this.buf);
		this.wrap_buf.asFloatBuffer();
		this.values = new FixedLengthFloatArrayWritable(this.numRecords);
    }

    @Override
    public synchronized boolean nextKeyValue() throws IOException, InterruptedException {
		
<<<<<<< Updated upstream
        if (fpPos < fpEnd) {
            key.set(fpPos);
            value.set(iStream.readFloat());
            fpPos += ((long) Float.BYTES);
            return true;
        } else return false;
=======
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
           wrap_buf.clear();

    		if ((fpPos + size_buf) <= fpEnd) {
    			// we can read entire buffer size of data.
    			ptrBuffer 	= this.buf;
    			bb 			= this.wrap_buf;
    			
    			bufferLength = values.getLength();
    			
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
    		
    		iStream.readFully(ptrBuffer);
			for (int i = 0 ; i < bufferLength ; i++)
			{
				// set inner ArrayWritable values
				values.get(i).set(bb.getFloat());
			}
    		
    		return true;
    	}
    	else return false;
>>>>>>> Stashed changes
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {
        return key;
    }
    
    @Override
    public FloatWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
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
    
    /*
    public InputSplit getInputSplit() {
    	return split;
    }
    
    public TaskAttemptContext getTaskAttemptContext() {
    	return context;
    }
    */

}