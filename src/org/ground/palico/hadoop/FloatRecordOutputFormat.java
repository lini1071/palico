package org.ground.palico.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;

public class FloatRecordOutputFormat extends FileOutputFormat<LongWritable, FloatWritable> {

	@Override
	public RecordWriter<LongWritable, FloatWritable>
	getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		String extension = "";
		Path file = getDefaultWorkFile(context, extension);
		FileSystem fs = file.getFileSystem(conf);
		FSDataOutputStream fileOut = fs.create(file, false);
		return new FloatRecordWriter(fileOut);
	}
}
