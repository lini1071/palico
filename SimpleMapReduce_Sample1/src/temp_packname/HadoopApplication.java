package temp_packname;

import java.io.IOException;
import java.util.List;

//This dependency is became comment but we may need this in future.
/*
	import org.apache.commons.logging.Log;
	import org.apache.commons.logging.LogFactory;
*/

// dependency ; org.apache.hadoop, hadoop-common
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

// dependency ; org.apache.hadoop.mapreduce, hadoop-mapreduce-client-core
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ReflectionUtils;


class HadoopApplication
{
	// FloatRecordInputFormat(InputFormat) class
	public static class FloatRecordInputFormat
		extends FileInputFormat<LongWritable, FloatWritable>
	{
		@Override
		public RecordReader<LongWritable, FloatWritable>
			createRecordReader(InputSplit split, TaskAttemptContext context)
		{
			return new FloatRecordReader();
		};
		
		// Minimum size of record is 4 (sizeof(float)).
		@Override
		protected long getFormatMinSplitSize()
		{
		    return ((long) Float.BYTES);
		};
		
		@Override
		protected boolean isSplitable(JobContext context, Path file)
		{
			final CompressionCodec codec =
				new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
			if (null == codec) return true;
			else return (codec instanceof
				org.apache.hadoop.io.compress.SplittableCompressionCodec);
		};
	}
	
	// FloatRecordReader(RecordReader) class
	public static class FloatRecordReader
		extends RecordReader<LongWritable, FloatWritable>
	{
		// File pointer position
		private long fpStart;
		private long fpPos;
		private long fpEnd;
		
		private LongWritable  key 	= new LongWritable();
		private FloatWritable value = new FloatWritable();
		private FSDataInputStream iStream;
		
		public FloatRecordReader()
		{
			
		};
		
		@Override
		public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException
		{
			FileSplit fSplit = (FileSplit) split;
			Configuration job = context.getConfiguration();
			
			// Split "S" is responsible for all records
			// starting from "start" and "end" positions
			fpStart = fSplit.getStart();
			fpEnd 	 = fpStart + fSplit.getLength();
			
			// Retrieve file containing Split "S"
			final Path file 	= fSplit.getPath();
			FileSystem fs 	= file.getFileSystem(job);
			iStream 		= fs.open(fSplit.getPath());
			
			// Move file pointer of the file we are accessing.
			fpPos = fpStart;
			if (0 < fpStart) iStream.seek(fpStart);
		};

		@Override
		public boolean nextKeyValue()
			throws IOException, InterruptedException
		{
			if (fpPos < fpEnd)
			{
				key.set(fpPos);
				value.set(iStream.readFloat());
				fpPos += ((long) Float.BYTES);
				return true;
			}
			else return false;
		};

		@Override
		public LongWritable getCurrentKey()
			throws	IOException, InterruptedException
		{
			return key;
		};

		@Override
		public FloatWritable getCurrentValue()
			throws IOException, InterruptedException
		{
			return value;
		};

		@Override
		public float getProgress()
			throws IOException, InterruptedException
		{
			// t : total data size, c : currently progressed size 
			float t = (float) (fpEnd - fpStart);
			float c = (float) (fpPos - fpStart);
			
			// avoiding DivideByZero
			return (t != 0.0f ? (c / t) : 0.0f);
		};

		@Override
		public void close() throws IOException
		{
			iStream.close();
		};
	}
	
	// FloatRecordOutputFormat(OutputFormat) class
	public static class FloatRecordOutputFormat
		extends FileOutputFormat<LongWritable, FloatWritable>
	{
		@Override
		public RecordWriter<LongWritable, FloatWritable>
			getRecordWriter(TaskAttemptContext job) throws IOException, InterruptedException
		{
		    Configuration conf = job.getConfiguration();
		    boolean isCompressed = getCompressOutput(job);

		    CompressionCodec codec = null;
		    String extension = "";
		    
		    if (isCompressed)
		    {
				Class<? extends CompressionCodec> codecClass = 
					getOutputCompressorClass(job, GzipCodec.class);
				codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, conf);
				extension = codec.getDefaultExtension();
		    }
		    Path file = getDefaultWorkFile(job, extension);
		    FileSystem fs = file.getFileSystem(conf);
		    
		    if (!isCompressed)
		    {
				FSDataOutputStream fileOut = fs.create(file, false);
				return new FloatRecordWriter(fileOut);
		    }
			else
			{
				FSDataOutputStream fileOut = fs.create(file, false);
				return new FloatRecordWriter
					(new FSDataOutputStream(codec.createOutputStream(fileOut)));
			}
		};
	}
	
	// FloatRecordWriter(RecordWriter) class
	public static class FloatRecordWriter
		extends RecordWriter<LongWritable, FloatWritable>
	{
		private FSDataOutputStream oStream;
		
		public FloatRecordWriter(FSDataOutputStream stream)
		{
			oStream = stream;
		};

		@Override
		public void write(LongWritable key, FloatWritable val)
			throws IOException, InterruptedException
		{
			oStream.writeFloat(val.get());
		};
		
		@Override
		public void close(TaskAttemptContext context)
			throws IOException, InterruptedException
		{
			oStream.close();
		};
	}
	
	// Mapper class
	public static class CalculatorMapper
		extends Mapper<LongWritable, FloatWritable, LongWritable, FloatWritable>
	{
		private static final float SAMPLE_VALUE = 2.3f;
		private FloatRecordReader reader = new FloatRecordReader();
		
		private LongWritable  outKey 	= new LongWritable();
		private FloatWritable outValue 	= new FloatWritable();
		
		// map function implementing
		@Override
		public void map(LongWritable key, FloatWritable value, Context context)
			throws IOException, InterruptedException
		{
			float res;
			
			// If mapper read each data value successfully,
			// record processed output data value.
			while (reader.nextKeyValue())
			{
				// process calculating
				res = value.get();
				res /= SAMPLE_VALUE;
				
				outKey.set(key.get());
				outValue.set(res);
				
				// write key-value pair to the split
				context.write(outKey, outValue);
			};
		};
	}
	
	// Reducer class
	public static class MergeReducer
		extends Reducer<LongWritable, FloatWritable, LongWritable, FloatWritable>
	{
		@Override
		public void reduce(LongWritable key, Iterable<FloatWritable> values, Context context)
			throws IOException, InterruptedException
		{
			for (FloatWritable value : values)
			{
				context.write(key, value);
			};
		};
	}
}