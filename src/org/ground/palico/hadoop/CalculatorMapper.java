package org.ground.palico.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class CalculatorMapper extends
	Mapper<LongWritable, FloatWritable, LongWritable, FloatWritable> {
	
    private static final float SAMPLE_VALUE = 2.3f;
    private FloatWritable outValue = new FloatWritable();

    // map function implementing
    @Override
    public void map(LongWritable key, FloatWritable value, Context context) throws IOException, InterruptedException {

        // process calculating
        outValue.set(value.get() * SAMPLE_VALUE);

        // write key-value pair to the split
        context.write(key, outValue);
    }
}
