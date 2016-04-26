package org.ground.palico.spark;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class CalcMainClassForSpark {
	
	private static final float SAMPLE_VALUE = 0.999f;
	private static final float COMPLEXITY_CAL = 9;
	
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
			int numRecords, String inStr, String outStr) throws Exception {
    	
		long tStart = System.currentTimeMillis();
		JavaSparkContext context = new JavaSparkContext(cf);
		final int recordSize = Float.BYTES;	// 4bytes
		
		// FixedLengthBinaryInputFormat.RECORD_LENGTH_PROPERTY == 
		// "org.apache.spark.input.FixedLengthBinaryInputFormat.recordLength"
		Configuration hConf = context.hadoopConfiguration(); 
		hConf.setInt("CONF_RECORD_SIZE_BLOCK", recordSize);
		hConf.setInt("CONF_NUM_RECORDS_BLOCK", numRecords);

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
			LongWritable.class, FloatWritable.class, FixedLengthRecordOutputFormat.class);
		
		// Calculate performance time
		long tEnd = System.currentTimeMillis();
		long tPerf = tEnd - tStart;
		
		// Print result on Console
		System.out.println("Number of partitions : " + orgData.getNumPartitions());
		System.out.println("Perf. Time : " + tPerf + "ms");
		context.close();
    }
}
