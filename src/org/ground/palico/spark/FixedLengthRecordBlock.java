package org.ground.palico.spark;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.apache.hadoop.io.Writable;
import org.mortbay.log.Log;

public class FixedLengthRecordBlock<T extends Writable> implements Writable {

	// We cannot use generic array... so changed to ArrayList.
	// But I'm worried about execution speed because
	// usage of this is more complex than case of traditional array.
	//private ArrayList<T> buffer;
	private T[] buffer;
	
	private boolean isBlockBroken = false;
	private int 	blockLength;
	private int 	partLength;
	
	/**
	 * 
	 * @param componentType type of basic record class
	 * @param n number of records
	 */
	@SuppressWarnings("unchecked")
	public FixedLengthRecordBlock(Class<T> componentType, int n)
			throws InstantiationException, IllegalAccessException
	{
		this.blockLength = n;
		this.buffer = (T[]) Array.newInstance(componentType, n);//new ArrayList<T>(n);
		
		// initialize each Writable instance
		for (int i = 0 ; i < buffer.length ; i++)
			this.buffer[i] = componentType.newInstance();
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		//for (int i = 0 ; i < buffer.size() ; i++) buffer.get(i).write(out);
		int bufLen = this.getBufferLength(); 
		for (int i = 0 ; i < bufLen ; i++) buffer[i].write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		//for (int i = 0 ; i < buffer.size() ; i++) buffer.get(i).readFields(in);
		int bufLen = this.getBufferLength(); 
		for (int i = 0 ; i < bufLen ; i++) buffer[i].readFields(in);
	}

	public int getBufferLength() {
		if (isBlockBroken) 	return this.partLength;
		else 					return this.blockLength;
	}
	public void setPartLength(int length) {
		this.isBlockBroken = true;
		this.partLength = length;
	}
	public boolean checkBroken() {
		return this.isBlockBroken;
	}
	
}
