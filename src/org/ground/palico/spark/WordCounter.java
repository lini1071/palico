package org.ground.palico.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;

public class WordCounter {
    public static void main(String[] args) {
        try {
            String filePath = args[0];
            SparkConf sConf = new SparkConf();
            sConf.setAppName("ExtPerfTimeApp2");
            countWord(sConf, filePath);
        } catch (Exception e) {
            System.out.println("Error occurred : " + e.getMessage());
        }
    }

    private static void countWord(SparkConf cf, String fPath) throws Exception {
        long tStart = System.nanoTime();
        JavaSparkContext context = new JavaSparkContext(cf);
        final int minPartitions = 10;
        JavaRDD<String> data = context.textFile(fPath, minPartitions);
        JavaRDD<String> words = data.flatMap((FlatMapFunction<String, String>) s -> Arrays.asList(s.split(" ")));
        JavaPairRDD<String, Integer> pairs = words.mapToPair((PairFunction<String, String, Integer>) s -> new Tuple2<>(s, 1));
        JavaPairRDD<String, Integer> counts = pairs.reduceByKey((Function2<Integer, Integer, Integer>) (a, b) -> a + b);

        // In this case we write the result as files.
        counts.saveAsTextFile("hdfs://192.168.100.6:9000/result/");

        // Calculate performance time
        long tEnd = System.nanoTime();
        long tPerf = tEnd - tStart;
        // Print result on Console
        System.out.println("Number of partitions : " + data.getNumPartitions());
        System.out.println("Perf. Time : " + tPerf + "ns");
        context.close();
    }
}