package org.ground.palico.spark;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class FixedLengthBytesWritableInputFormat extends
	FileInputFormat<LongWritable, FixedLengthBytesWritable> {

	private int blockSize;
	private int recordSize;
	
    @Override
    public RecordReader<LongWritable, FixedLengthBytesWritable>
    	createRecordReader(InputSplit split, TaskAttemptContext context)
    		throws IOException, InterruptedException {
    	
    	this.blockSize = context.getConfiguration().getInt("CONF_BLOCK_SIZE", 1);
    	this.recordSize = context.getConfiguration().getInt("CONF_RECORD_SIZE", 1);
    	
    	// the parameters(split, context) are passed one more at
    	// input.initialize() in MapTask.runNewMapper()
    	return new FixedLengthBytesWritableReader(blockSize);
    }

    // Minimum size of record is 4 (sizeof(float)).
    @Override
    public long getFormatMinSplitSize() {
        //return ((long) (Float.BYTES * this.numRecords));
    	return ((long) this.recordSize);
    }

    @Override
    public boolean isSplitable(JobContext context, Path file) {
        final CompressionCodec codec = new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
        if (null == codec) return true;
        else return (codec instanceof org.apache.hadoop.io.compress.SplittableCompressionCodec);
    }
}
