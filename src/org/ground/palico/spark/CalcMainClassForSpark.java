package org.ground.palico.spark;

import java.nio.ByteBuffer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.input.FixedLengthBinaryInputFormat;

import scala.Tuple2;
import scala.collection.Iterator;

public class CalcMainClassForSpark {
	
	private static final float SAMPLE_VALUE = 0.999f;
	private static final float COMPLEXITY_CAL = 9;
	
    public static void main(String[] args) {
        try {
            SparkConf sConf = new SparkConf();
            sConf.setAppName("ExtPerfTimeAppSpark");
            calcFloat(sConf, args[0], args[1]);
        } catch (Exception e) {
            System.out.println("Error occurred : " + e.getMessage());
        }
    }
    
    @SuppressWarnings("serial")
	private static void calcFloat(SparkConf cf, String inStr, String outStr) throws Exception {
        long tStart = System.currentTimeMillis();
        JavaSparkContext context = new JavaSparkContext(cf);
        final int recordLength = Float.BYTES;	// 4bytes
        
        // FixedLengthBinaryInputFormat.RECORD_LENGTH_PROPERTY == 
        // "org.apache.spark.input.FixedLengthBinaryInputFormat.recordLength"
        Configuration hConf = context.hadoopConfiguration(); 
        hConf.setInt("org.apache.spark.input.FixedLengthBinaryInputFormat.recordLength", recordLength);
        JavaPairRDD<LongWritable, BytesWritable> orgData = 
        		context.newAPIHadoopFile(inStr, FixedLengthBinaryInputFormat.class,
        		LongWritable.class, BytesWritable.class, hConf);

		JavaPairRDD<LongWritable, FloatWritable> flData =
        		orgData.mapValues(new Function<BytesWritable, FloatWritable>()
        		{
        			public FloatWritable call(BytesWritable b)
        			{
						// Includes calculating sequence. It may be very costful.
						float res = ByteBuffer.wrap(b.getBytes()).asFloatBuffer().get();
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
