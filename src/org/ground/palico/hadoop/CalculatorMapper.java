package org.ground.palico.hadoop;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.ground.palico.lv2Generator.SequentialMode;
import org.ground.palico.spark.FixedLengthBytesWritable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class CalculatorMapper extends
		Mapper<LongWritable, FixedLengthBytesWritable, LongWritable, FixedLengthBytesWritable> {
	private static final int BLOCK_SIZE = 1024 * 1024 * 32;   // 32MB
	SequentialMode sm = new SequentialMode();
	byte[] result;

	@Override
	public void map(LongWritable key, FixedLengthBytesWritable value, Context context) throws IOException, InterruptedException {
		byte[] input = value.getBytes();

		//byte[] > flotBuffer > float[]
		float[] band = new float[BLOCK_SIZE / Float.BYTES];
		int bandSize = band.length;
		FloatBuffer inputBuffer = ByteBuffer.wrap(input).asFloatBuffer();
		inputBuffer.get(band);

		float[] output = sm.performBySequential(band, bandSize);

		//float[] > floatBuffer > byte[]
		FloatBuffer floatBuffer = FloatBuffer.wrap(output);
		int obSize = floatBuffer.capacity();
		ByteBuffer outputBuffer = ByteBuffer.allocate(obSize * Float.BYTES);
		outputBuffer.asFloatBuffer().put(floatBuffer);
		result = outputBuffer.array();

		context.write(key, new FixedLengthBytesWritable(result));
	}
}
