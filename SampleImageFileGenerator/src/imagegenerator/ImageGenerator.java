package imagegenerator;

import java.io.IOException;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;

import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;

public class ImageGenerator
{
	public static void main(String[] args)
	{
		// args[0] : file path
		// args[1] : file size
		// args[2] : value
		try
		{
			long tStart, tEnd;
			
			tStart = System.currentTimeMillis();
			if (args.length == 3)
			{
				GenerateFile(args[0],
					Long.parseLong(args[1]), Float.parseFloat(args[2]));
			}
			else if (args.length == 4)
			{
				GenerateFile(args[0], Long.parseLong(args[1]),
					Long.parseLong(args[2]), Float.parseFloat(args[3]));
			};
			tEnd = System.currentTimeMillis();
			
			System.out.println("Generated file " + args[0] +
					"\nElapsed time : " + (tEnd - tStart) + "ms");
			
			/*
			 * Remove comment character if you want to validate written data
			 *  
			ValidateFile(args[0], 3 < args.length ?
				Float.parseFloat(args[3]) : Float.parseFloat(args[2]));
			*/
		}
		catch (NumberFormatException e)
		{
			System.err.println("Caught NumberFormatException");
		}
		catch (IOException e)
		{
			System.err.println("Caught IOException in creating file " + args[0]);
		}
		catch (Exception e)
		{
			System.err.println("This file has invalid data.");
		};
	};
	
	/**
	Generates a file filled by given value to specified path
	@param file - name of target file
	@param	size - size of target file
	@param	value - value you want to write
	@return 0 is returned when there is no error.
		<br>When an error is occurred, it returns 1.
	@throws file(I/O)-related exception
	*/
	private static void GenerateFile
		(String file, long size, float value) throws IOException
	{
		long i, r;
		
		FileOutputStream		fStream = null;
		BufferedOutputStream bStream = null;
		DataOutputStream		dStream = null;
		
		if (0 < size)
		{
			try
			{
				// Always create a new file, don't append to the target.
				fStream 	= new FileOutputStream(file, false);
				bStream 	= new BufferedOutputStream(fStream);
				dStream	= new DataOutputStream(bStream);
				
				r = size / ((long) Float.BYTES);
				for (i = 0 ; i < r ; i++) dStream.writeFloat(value);
				dStream.close();
			}
			catch (Exception e)
			{
				throw new IOException();
			};
		}
		else throw new IOException();		
	};
	
	
	private static void GenerateFile
		(String file, long row, long col, float value) throws IOException
	{
		long size = (row * col) * ((long) Float.BYTES);
		GenerateFile(file, size, value);
	};
	
	/**
	Validates speficied file
	@param file - name of target file
	@param	value - value you want to confirm
	@throws file(I/O)-related exception, non-info. exception 
	*/
	private static void ValidateFile
		(String file, float value) throws Exception, IOException
	{
		long numOfElements = 0;
		
		FileInputStream		fStream = null;
		BufferedInputStream	bStream = null;
		DataInputStream		dStream = null;
		
		try
		{
			fStream 	= new FileInputStream(file);
			bStream 	= new BufferedInputStream(fStream);
			dStream	= new DataInputStream(bStream);
				
			while (0 < dStream.available())
			{
				if (dStream.readFloat() != value)
				{
					dStream.close();
					throw new Exception();
				}
				else numOfElements += 1;
			};
			
			System.out.println("Value of all data is correct. "
				+ "Number of elements : " + numOfElements);
			dStream.close();
		}
		catch (Exception e)
		{
			throw new IOException();
		};
	};
}
