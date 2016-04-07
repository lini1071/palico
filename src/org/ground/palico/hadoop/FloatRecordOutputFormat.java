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

public class FloatRecordOutputFormat extends FileOutputFormat<NullWritable, FloatWritable> {

    @Override
    public RecordWriter<NullWritable, FloatWritable>
    	getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        boolean isCompressed = getCompressOutput(context);

        CompressionCodec codec = null;
        String extension = "";

        if (isCompressed) {
            Class<? extends CompressionCodec> codecClass
            	= getOutputCompressorClass(context, GzipCodec.class);
            codec = ReflectionUtils.newInstance(codecClass, conf);
            extension = codec.getDefaultExtension();
        }
        Path file = getDefaultWorkFile(context, extension);
        FileSystem fs = file.getFileSystem(conf);

        if (!isCompressed) {
            FSDataOutputStream fileOut = fs.create(file, false);
            return new FloatRecordWriter(fileOut);
        } else {
            FSDataOutputStream fileOut = fs.create(file, false);
            return new FloatRecordWriter
                    (new FSDataOutputStream(codec.createOutputStream(fileOut)));
        }
    }
}
