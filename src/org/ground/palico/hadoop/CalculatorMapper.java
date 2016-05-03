package org.ground.palico.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.ground.palico.spark.FixedLengthBytesWritable;

public class CalculatorMapper extends
	Mapper<LongWritable, FixedLengthBytesWritable, LongWritable, FixedLengthBytesWritable> {
	
	// map function implementing
	@Override
	public void map(LongWritable key, FixedLengthBytesWritable value, Context context) throws IOException, InterruptedException {
	
		// You must implement method getting byte array and proceeding 'run'. 
		byte[] b = value.getBytes();

		////// Do calling run method!
		
		// write key-value pair to the split
		context.write(key, new FixedLengthBytesWritable(b));
	}
}
