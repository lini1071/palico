package org.ground.palico.hadoop;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class FloatRecordInputFormat extends FileInputFormat<LongWritable, FixedLengthFloatArrayWritable> {
	
	private int numRecords;
	
    @Override
    public RecordReader<LongWritable, FixedLengthFloatArrayWritable>
    	createRecordReader(InputSplit split, TaskAttemptContext context)
    		throws IOException, InterruptedException {
    	
    	this.numRecords = context.getConfiguration().getInt("CONF_NUM_RECORDS_BLOCK", 1);
    	
    	// the parameters(split, context) are passed one more at
    	// input.initialize() in MapTask.runNewMapper()
    	return new FloatRecordReader(this.numRecords);
    }

    // Minimum size of record is 4 (sizeof(float)).
    @Override
    public long getFormatMinSplitSize() {
        //return ((long) (Float.BYTES * this.numRecords));
    	return (long) Float.BYTES;
    }

    @Override
    public boolean isSplitable(JobContext context, Path file) {
        final CompressionCodec codec = new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
        if (null == codec) return true;
        else return (codec instanceof org.apache.hadoop.io.compress.SplittableCompressionCodec);
    }
}