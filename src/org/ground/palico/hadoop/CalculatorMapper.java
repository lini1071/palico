package org.ground.palico.hadoop;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.map.WrappedMapper;

import java.io.IOException;

public class CalculatorMapper extends
	WrappedMapper<LongWritable, FloatWritable, LongWritable, FloatWritable> {
    private static final float SAMPLE_VALUE = 2.3f;
    private FloatRecordReader reader = new FloatRecordReader();

    private LongWritable outKey = new LongWritable();
    private FloatWritable outValue = new FloatWritable();

    // map function implementing
    public void map(LongWritable key, FloatWritable value, Context context) throws IOException, InterruptedException {
    	float res;
        // If mapper read each data value successfully,
        // record processed output data value.
        while (reader.nextKeyValue()) {
            // process calculating
            res = value.get();
            res /= SAMPLE_VALUE;

            outKey.set(key.get());
            outValue.set(res);

            // write key-value pair to the split
            context.write(outKey, outValue);
        }
    }
}
