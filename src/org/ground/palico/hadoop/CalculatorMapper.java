package org.ground.palico.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class CalculatorMapper extends
	Mapper<LongWritable, FixedLengthFloatArrayWritable, LongWritable, FixedLengthFloatArrayWritable> {
	
	private static final float SAMPLE_VALUE = 0.999f;
	private static final float COMPLEXITY_CAL = 9;
	
	// map function implementing
	@Override
	public void map(LongWritable key, FixedLengthFloatArrayWritable value, Context context) throws IOException, InterruptedException {
	
		FloatWritable fw;
		float f;
		
		for (int i = 0 ; i < value.getLength() ; i++)
		{
			fw = value.get(i);
			f = fw.get();

			// do calculating
			for (int j = 0 ; j < COMPLEXITY_CAL ; j++) f /= SAMPLE_VALUE;
			fw.set(f);
		}
		
		// write key-value pair to the split
		context.write(key, value);
	}
}
