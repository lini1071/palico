package org.ground.palico.hadoop;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

class MergeReducer extends Reducer<LongWritable, FloatWritable, LongWritable, FloatWritable> {
    @Override
    public void reduce(LongWritable key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
        for (FloatWritable value : values) {
            context.write(key, value);
        }
    }
}