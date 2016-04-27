package org.ground.palico.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class CalculatorMapper extends
	Mapper<LongWritable, FloatWritable, LongWritable, FloatWritable> {
	
	private static final float SAMPLE_VALUE = 0.999f;
	private static final float COMPLEXITY_CAL = 9;
	
	// map function implementing
	@Override
	public void map(LongWritable key, FloatWritable value, Context context) throws IOException, InterruptedException {
	
		float f = value.get();

		// do calculating
		for (int j = 0 ; j < COMPLEXITY_CAL ; j++) f /= SAMPLE_VALUE;
		
		// write key-value pair to the split
		context.write(key, new FloatWritable(f));
	}
}
