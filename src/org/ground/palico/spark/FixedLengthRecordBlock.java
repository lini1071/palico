package org.ground.palico.spark;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class FixedLengthRecordBlock implements Writable {

	private byte[] buffer;
	
	public FixedLengthRecordBlock()
	{
		
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.write(buffer);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
