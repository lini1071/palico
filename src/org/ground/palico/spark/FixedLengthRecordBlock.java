package org.ground.palico.spark;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Writable;

public class FixedLengthRecordBlock<T extends Writable> implements Writable {

	// We cannot use generic array... so changed to ArrayList.
	// But I'm worried about execution speed because
	// usage of this is more complex than case of traditional array.
	private ArrayList<T> buffer;
	
	/**
	 * 
	 * @param r
	 * @param n
	 */
	public FixedLengthRecordBlock(int n)
	{
		this.buffer = new ArrayList<T>(n);
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		for (int i = 0 ; i < buffer.size() ; i++) buffer.get(i).write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		for (int i = 0 ; i < buffer.size() ; i++) buffer.get(i).readFields(in);
	}

}
