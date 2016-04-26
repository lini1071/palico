package org.ground.palico.spark;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FixedLengthRecordBlockOutputFormat<K, V extends Writable> extends FileOutputFormat<K, V> {

	@Override
	public RecordWriter<K, V>
	getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		int recordSize = conf.getInt("CONF_RECORD_SIZE_BLOCK", 1);
		int numRecords = conf.getInt("CONF_NUM_RECORDS_BLOCK", 1);
		
		String extension = "";
		Path file = getDefaultWorkFile(context, extension);
		FileSystem fs = file.getFileSystem(conf);
		FSDataOutputStream fileOut = fs.create(file, false);

		return (RecordWriter<K, V>)
			new FixedLengthRecordBlockWriter<K, V>(fileOut, recordSize * numRecords);
	}

}
