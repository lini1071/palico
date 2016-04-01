package sparksample1;

import java.util.Arrays;
import java.lang.Math;

// dependency ; org.apache.spark, spark-core_2.10
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

//import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class Program
{
	//public static int iTimes = 25;		// value of expecting iteration count
	
	public static void main(String[] args)
	{
		// try-catch for printing error-check message
		try
		{
			// Declare basic objects ; SparkConf, JavaSparkContext
			SparkConf 			sConf;
			JavaSparkContext 	sCont = null;
			
			// Specify target file to read contexts and test the performance
			String filePath = args[0];
			
			// Initialize SparkConf object, and set application name
			(sConf = new SparkConf()).setAppName("ExtPerfTimeApp2");

			PerformSparkAction(sConf, sCont, filePath);
			
			// Release SparkConf object literally
			sConf = null;
		}
		catch (Exception e)
		{
			System.out.println("Error occurred : " + e.getMessage());
		};
	};
	// end of main function
	
	private static void PerformSparkAction
		(SparkConf cf, JavaSparkContext ct, String fPath) throws Exception
	{
		// Declare int-type variables ; temporary variable
		int i, j;
		
		// Declare long-type variables ; for calculating perf. time
		long tStart, tEnd, tPerf;
		
		/*
		for (i = 1 ; i <= 10 ; i++)	// Specifying value of spark.cores.max.
										// We can't get the number of available
										// maximum cores if we don't use JSON.
		{
			// Modify spark.cores.max condition of SparkConf
			sConf.set("spark.cores.max", String.valueOf(i));
			System.out.println("Core number : " + String.valueOf(i));
			*/
			
			//for (j = 0 ; j <= 8 ; j++)	// Specifying number of partitions
			//{
				// Initialize JavaSparkContext object, and pin start time
				ct = new JavaSparkContext(cf);
				tStart = System.nanoTime();
				
				///// This line indicates start point of task for measuring the time
				///// In this code we use one big text file as data. 
				JavaRDD<String> data =
					ct.textFile( fPath, 10/*((int) Math.pow(2, 8 - j))*/ );
				
				JavaRDD<String> words = data.flatMap(new FlatMapFunction<String, String>()
					{
						public Iterable<String> call(String s) { return Arrays.asList(s.split(" ")); }
					});
				JavaPairRDD<String, Integer> pairs =
					words.mapToPair(new PairFunction<String, String, Integer>()
					{
						public Tuple2<String, Integer> call(String s) { return new Tuple2<String, Integer>(s, 1); }
					});
				JavaPairRDD<String, Integer> counts =
					pairs.reduceByKey(new Function2<Integer, Integer, Integer>()
					{
						public Integer call(Integer a, Integer b) { return a + b; }
					});
				
				// In this case we write the result as files.
				
				counts.saveAsTextFile("hdfs://192.168.100.6:9000/result/");

				// Calculate performance time
				tEnd = System.nanoTime();
				if (tEnd < tStart)
				{
					ct.close();
					ct = null;
					throw new Exception();	// unexpected error
				}
				else tPerf = tEnd - tStart;
				
				// Print result on Console
				System.out.println("Number of partitions : " + String.valueOf(data.getNumPartitions())
					+ ",\tPerf. Time : " + String.valueOf(tPerf) + "ns");
				
				// Close JavaSparkContext and
				// release JavaSparkContext object literally
				ct.close();
				ct = null;
			//};
			
			System.out.println();	// Line spacing
		//};
	};
};