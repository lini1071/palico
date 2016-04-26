package org.ground.palico.spark;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class FixedLengthRecordBlockInputFormat<T extends Writable>
	extends FileInputFormat<LongWritable, FixedLengthRecordBlock<T>> {

	private int recordSize;
	private int numRecords;
	
	@Override
	public RecordReader<LongWritable, FixedLengthRecordBlock<T>> createRecordReader
		(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		this.recordSize = context.getConfiguration().getInt("CONF_RECORD_SIZE_BLOCK", 1);
    	this.numRecords = context.getConfiguration().getInt("CONF_NUM_RECORDS_BLOCK", 1);
    	return new FixedLengthRecordBlockReader<T>(this.numRecords);
	}

	// Minimum size of record is ... flexible.
	@Override
	public long getFormatMinSplitSize() {
		return (long) (this.recordSize * this.numRecords);
	}

    @Override
    public boolean isSplitable(JobContext context, Path file) {
        final CompressionCodec codec = new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
        if (null == codec) return true;
        else return (codec instanceof org.apache.hadoop.io.compress.SplittableCompressionCodec);
    }
}
