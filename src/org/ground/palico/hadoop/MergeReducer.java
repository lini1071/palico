package org.ground.palico.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.reduce.WrappedReducer;

class MergeReducer extends
	WrappedReducer<LongWritable, FloatWritable, LongWritable, FloatWritable> {

	public void reduce(LongWritable key, Iterable<FloatWritable> values, Context context)
		throws IOException, InterruptedException {
		
		for (FloatWritable value : values) {
			context.write(key, value);
		}
	}
}