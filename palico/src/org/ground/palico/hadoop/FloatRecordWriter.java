package org.ground.palico.hadoop;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

public class FloatRecordWriter extends RecordWriter<LongWritable, FloatWritable> {
    private FSDataOutputStream oStream;

    public FloatRecordWriter(FSDataOutputStream stream) {
        oStream = stream;
    }

    @Override
    public void write(LongWritable key, FloatWritable val) throws IOException, InterruptedException {
        oStream.writeFloat(val.get());
    }

    @Override
    public void close(TaskAttemptContext context) throws IOException, InterruptedException {
        oStream.close();
    }
}