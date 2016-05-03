package org.ground.palico.spark;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class FixedLengthBytesWritable implements Writable {

	private int size;
	private byte[] bytes;
  
	/**
	 * Create a zero-size sequence.
	 */
	//public PureBytesWritable() {this(EMPTY_BYTES);}
  
	/**
	 * Create a BytesWritable using the byte array as the initial value.
	 * @param bytes This array becomes the backing storage for the object.
	 */
	public FixedLengthBytesWritable(byte[] bytes) {
		this(bytes, bytes.length);
	}
	
	/**
	 * Create a BytesWritable using the byte array as the initial value
	 * and length as the length. Use this constructor if the array is larger
	 * than the value it represents.
	 * @param bytes This array becomes the backing storage for the object.
	 * @param length The number of bytes to use from array.
	 */
	public FixedLengthBytesWritable(byte[] bytes, int length) {
		this.bytes = bytes;
		this.size = length;
	}
	  
	/**
	 * Get a copy of the bytes that is exactly the length of the data.
	 * See {@link #getBytes()} for faster access to the underlying array.
	 */
	public byte[] copyBytes() {
		byte[] result = new byte[size];
		System.arraycopy(bytes, 0, result, 0, size);
		return result;
	}
	  
	/**
	 * Get the data backing the BytesWritable. Please use {@link #copyBytes()}
	 * if you need the returned array to be precisely the length of the data.
	 * @return The data is only valid between 0 and getLength() - 1.
	 */
	public byte[] getBytes() {
		return bytes;
	}
	
	/**
	 * Get the data from the BytesWritable.
	 * @deprecated Use {@link #getBytes()} instead.
	 */
	@Deprecated
	public byte[] get() {
		return getBytes();
	}
	
	/**
	 * Get the current size of the buffer.
	 */
	public int getLength() {
		return size;
	}
	
	/**
	 * Get the current size of the buffer.
	 * @deprecated Use {@link #getLength()} instead.
	 */
	@Deprecated
	public int getSize() {
		return getLength();
	}
	  
	/**
	 * Change the size of the buffer. The values in the old range are preserved
	 * and any new values are undefined. The capacity is changed if it is 
	 * necessary.
	 * @param size The new number of bytes
	 */
	public void setSize(int size) {
		if (size > getCapacity()) {
			setCapacity(size * 3 / 2);
		}
		this.size = size;
	}
	  
	/**
	 * Get the capacity, which is the maximum size that could handled without
	 * resizing the backing storage.
	 * @return The number of bytes
	 */
	public int getCapacity() {
		return bytes.length;
	}
		  
	/**
	 * Change the capacity of the backing storage.
	 * The data is preserved.
	 * @param new_cap The new capacity in bytes.
	 */
	public void setCapacity(int new_cap) {
		if (new_cap != getCapacity()) {
			byte[] new_data = new byte[new_cap];
			if (new_cap < size) {
				size = new_cap;
			}
			if (size != 0) {
				System.arraycopy(bytes, 0, new_data, 0, size);
			}
			bytes = new_data;
		}
	}
	
	/**
	* Set the BytesWritable to the contents of the given newData.
	* @param newData the value to set this BytesWritable to.
	*/
	public void set(FixedLengthBytesWritable newData) {
		set(newData.bytes, 0, newData.size);
	}

	/**
	 * Set the value to a copy of the given byte range
	 * @param newData the new values to copy in
	 * @param offset the offset in newData to start at
	 * @param length the number of bytes to copy
	 */
	public void set(byte[] newData, int offset, int length) {
		setSize(0);
		setSize(length);
		System.arraycopy(newData, offset, bytes, 0, size);
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		//setSize(0); // clear the old data
		//setSize( );
		in.readFully(bytes, 0, size);
	}
	  
	@Override
	public void write(DataOutput out) throws IOException {
		out.write(bytes, 0, size);
	}
		  
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/**
	* Are the two byte sequences equal?
	*/
	@Override
	public boolean equals(Object right_obj) {
		if (right_obj instanceof FixedLengthBytesWritable)
			return super.equals(right_obj);
		return false;
	}
}
