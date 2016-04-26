package org.ground.palico.hadoop;

/*
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
*/
import org.apache.hadoop.io.FloatWritable;

// referenced class org.apache.hadoop.io.ArrayWritable
public class FixedLengthFloatArrayWritable {
	
	// no byte-order consideration
	private FloatWritable[] values;
	
	private boolean isBlockBroken = false;
	private int 	blockLength;
	private int 	partLength;
	
	public FixedLengthFloatArrayWritable(int length) {

		this.blockLength = length;
		this.values = new FloatWritable[blockLength];  // construct values
		
		for (int i = 0 ; i < length ; i++)
		{
			this.values[i] = new FloatWritable();
		}
	}
	
	public int getLength() {
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
	

	public FloatWritable get(int index) { return this.values[index]; }
	public void set(FloatWritable[] values) { this.values = values; }
	//public float get(int index) { return this.values[index].get(); }
	//public void set(int index, float value) { this.values[index].set(value); }
}