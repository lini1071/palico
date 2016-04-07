package org.ground.palico.hadoop;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configuration.IntegerRanges;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.ReduceContext;

import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.io.RawComparator;

class MergeReducer extends
	Reducer<LongWritable, FloatWritable, NullWritable, FloatWritable> {

    /**
     * The <code>Context</code> passed on to the {@link Mapper} implementations.
     */
	/*
   public Reducer<LongWritable, FloatWritable, LongWritable, FloatWritable>.Context 
    getReducerContext(ReduceContext<LongWritable, FloatWritable, LongWritable, FloatWritable> reduceContext) {
      return new Context(reduceContext);
    }
	
	public class Context 
		extends Reducer<LongWritable, FloatWritable, LongWritable, FloatWritable>.Context {

	    protected ReduceContext<LongWritable, FloatWritable, LongWritable, FloatWritable> reduceContext;
	
	    public Context(ReduceContext<LongWritable, FloatWritable, LongWritable, FloatWritable> reduceContext)
	    {
	      this.reduceContext = reduceContext; 
	    }

		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			
			return ;
		}

		@Override
		public LongWritable getCurrentKey() throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FloatWritable getCurrentValue() throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void write(LongWritable key, FloatWritable value) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public OutputCommitter getOutputCommitter() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TaskAttemptID getTaskAttemptID() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setStatus(String msg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getStatus() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public float getProgress() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Counter getCounter(Enum<?> counterName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Counter getCounter(String groupName, String counterName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Configuration getConfiguration() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Credentials getCredentials() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public JobID getJobID() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getNumReduceTasks() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Path getWorkingDirectory() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<?> getOutputKeyClass() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<?> getOutputValueClass() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<?> getMapOutputKeyClass() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<?> getMapOutputValueClass() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getJobName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends InputFormat<?, ?>> getInputFormatClass() throws ClassNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends Mapper<?, ?, ?, ?>> getMapperClass() throws ClassNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends Reducer<?, ?, ?, ?>> getCombinerClass() throws ClassNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends Reducer<?, ?, ?, ?>> getReducerClass() throws ClassNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends OutputFormat<?, ?>> getOutputFormatClass() throws ClassNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends Partitioner<?, ?>> getPartitionerClass() throws ClassNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RawComparator<?> getSortComparator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getJar() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RawComparator<?> getCombinerKeyGroupingComparator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RawComparator<?> getGroupingComparator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getJobSetupCleanupNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean getTaskCleanupNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean getProfileEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getProfileParams() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IntegerRanges getProfileTaskRange(boolean isMap) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getUser() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getSymlink() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Path[] getArchiveClassPaths() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URI[] getCacheArchives() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URI[] getCacheFiles() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Path[] getLocalCacheArchives() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Path[] getLocalCacheFiles() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Path[] getFileClassPaths() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getArchiveTimestamps() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getFileTimestamps() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getMaxMapAttempts() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getMaxReduceAttempts() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void progress() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean nextKey() throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterable<FloatWritable> getValues() throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			return null;
		}
        
    }
	*/
	
	@Override
	public void reduce(LongWritable key, Iterable<FloatWritable> values, Context context)
		throws IOException, InterruptedException {
		
		for (FloatWritable value : values) {
			context.write(NullWritable.get(), value);
		}
	}
}