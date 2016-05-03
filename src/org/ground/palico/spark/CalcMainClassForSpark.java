package org.ground.palico.spark;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class CalcMainClassForSpark {

    public static void main(String[] args) {
        try {
            SparkConf sConf = new SparkConf();
            sConf.setAppName("ExtPerfTimeAppSpark");
            calcFloat(sConf, Integer.parseInt(args[0]), args[1], args[2]);
        } catch (Exception e) {
            System.out.println("Error occurred : " + e.getMessage());
        }
    }
    
    @SuppressWarnings("serial")
	private static void calcFloat(SparkConf cf,
			int blockSize, String inStr, String outStr) throws Exception {
    	
		long tStart = System.currentTimeMillis();
		JavaSparkContext context = new JavaSparkContext(cf);
		
		// FixedLengthBinaryInputFormat.RECORD_LENGTH_PROPERTY == 
		// "org.apache.spark.input.FixedLengthBinaryInputFormat.recordLength"
		Configuration hConf = context.hadoopConfiguration(); 
		hConf.setInt("CONF_BLOCK_SIZE", blockSize);
		hConf.setInt("CONF_RECORD_SIZE", Float.BYTES);

		/*
		JavaPairRDD<LongWritable, FloatWritable> orgData = 
			context.newAPIHadoopFile(inStr, FloatRecordBlockInputFormat.class,
			LongWritable.class, FloatWritable.class, hConf);

		JavaPairRDD<LongWritable, FloatWritable> flData =
				orgData.mapValues(new Function<FloatWritable, FloatWritable>()
				{
					public FloatWritable call(FloatWritable fw)
					{
						// Includes calculating sequence. It may be very costful.
						float res = fw.get();
						for (int i = 0 ; i < COMPLEXITY_CAL ; i++) res /= SAMPLE_VALUE;
						return new FloatWritable(res);
					}
				});
		
		flData.saveAsNewAPIHadoopFile(outStr,
				LongWritable.class, FloatWritable.class, FixedLengthRecordBlockOutputFormat.class);
		*/
		
		JavaPairRDD<LongWritable, FixedLengthBytesWritable> orgData = 
				context.newAPIHadoopFile(inStr, FixedLengthBytesWritableInputFormat.class,
				LongWritable.class, FixedLengthBytesWritable.class, hConf);

		JavaPairRDD<LongWritable, FixedLengthBytesWritable> flData =
				orgData.mapValues(new Function<FixedLengthBytesWritable, FixedLengthBytesWritable>()
				{
					public FixedLengthBytesWritable call(FixedLengthBytesWritable pb)
					{
						byte[] resBytes = pb.getBytes();
						
						////// You must call aparapi run method in this point.
						
						return new FixedLengthBytesWritable(resBytes);
					}
				});
		
		flData.saveAsNewAPIHadoopFile(outStr,
			LongWritable.class, FixedLengthBytesWritable.class, FixedLengthRecordBlockOutputFormat.class);
		
		// Calculate performance time
		long tEnd = System.currentTimeMillis();
		long tPerf = tEnd - tStart;
		
		// Print result on Console
		System.out.println("Number of partitions : " + orgData.getNumPartitions());
		System.out.println("Perf. Time : " + tPerf + "ms");
		context.close();
    }
}
