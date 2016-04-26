package org.ground.palico.spark;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.ground.palico.hadoop.FixedLengthFloatArrayWritable;
import org.ground.palico.hadoop.FloatRecordReader;

public class FloatRecordBlockInputFormat extends FixedLengthRecordBlockInputFormat<FloatWritable> {
    @Override
    public RecordReader<LongWritable, FixedLengthRecordBlock<FloatWritable>>
    	createRecordReader(InputSplit split, TaskAttemptContext context)
    		throws IOException, InterruptedException {
    	
    	this.numRecords = context.getConfiguration().getInt("CONF_NUM_RECORDS_BLOCK", 1);
    	
    	// the parameters(split, context) are passed one more at
    	// input.initialize() in MapTask.runNewMapper()
    	return new FixedLengthRecordBlockReader(this.numRecords);
    }
}
