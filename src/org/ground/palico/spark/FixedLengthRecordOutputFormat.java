package org.ground.palico.spark;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FixedLengthRecordOutputFormat<K, V extends Writable> extends FileOutputFormat<K, V> {

	@Override
	public RecordWriter<K, V>
		getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		String extension = "";
		Path file = getDefaultWorkFile(context, extension);
		FileSystem fs = file.getFileSystem(conf);
		FSDataOutputStream fileOut = fs.create(file, false);
		
		return (RecordWriter<K, V>) new FixedLengthRecordWriter<K, V>(fileOut);
	}
}