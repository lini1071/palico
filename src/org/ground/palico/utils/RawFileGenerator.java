package org.ground.palico.utils;

import java.io.*;

/**
 * 파일 위치, 크기, 값을 입력 받아 로우(Raw) 형식 파일을 생성
 * 인자는 "위치, 크기" 또는 "위치, 크기, 값" 형태로 주어질 수 있다.
 */
public class RawFileGenerator {
    public static void main(String[] args) {
        // args[0] : file path
        // args[1] : file size
        // args[2] : value
        try {
            long tStart, tEnd;
            tStart = System.currentTimeMillis();
            /*
            if (args.length == 3) {
                GenerateFile(args[0], Long.parseLong(args[1]), Float.parseFloat(args[2]));
            } else if (args.length == 4) {
                GenerateFile(args[0], Long.parseLong(args[1]), Long.parseLong(args[2]), Float.parseFloat(args[3]));
            }
            */
            ValidateFile(args[0]);
            
            tEnd = System.currentTimeMillis();
            System.out.println("Generated file " + args[0] + "\nElapsed time : " + (tEnd - tStart) + "ms");
        } catch (NumberFormatException e) {
            System.err.println("Caught NumberFormatException");
        } catch (IOException e) {
            System.err.println("Caught IOException in creating file " + args[0]);
        } catch (Exception e) {
            System.err.println("This file has invalid data.");
        }
    }

    /**
     * Generates a file filled by given value to specified path
     *
     * @param file - name of target file
     * @return 0 is returned when there is no error.
     * <br>When an error is occurred, it returns 1.
     * @param    size - size of target file
     * @param    value - value you want to write
     */
    private static void GenerateFile(String file, long size, float value) throws IOException {
        if(size <= 0)
            return;

        // Always create a new file, don't append to the target.
        FileOutputStream fStream = new FileOutputStream(file, false);
        BufferedOutputStream bStream = new BufferedOutputStream(fStream);
        DataOutputStream dStream = new DataOutputStream(bStream);

        long r = size / ((long) Float.BYTES);
        for (int i = 0; i < r; i++) dStream.writeFloat(value);
        dStream.close();
    }

    private static void GenerateFile(String file, long row, long col, float value) throws IOException {
        long size = (row * col) * ((long) Float.BYTES);
        GenerateFile(file, size, value);
    }

    /**
     * Validates specified file
     *
     * @param file - name of target file
     * @param    value - value you want to confirm
     */
    private static void ValidateFile(String file) throws IOException {
        FileInputStream fStream = new FileInputStream(file);
        BufferedInputStream bStream = new BufferedInputStream(fStream);
        DataInputStream dStream = new DataInputStream(bStream);

        long numOfElements = 0;
        float fl;
        while (0 < dStream.available()) {
        	/*
            if (dStream.readFloat() != value) {
                break;
            } else {
                numOfElements += 1;
            }
            */
        	fl = numOfElements;
        	for (int i = 0 ; i < 9 ; i++) fl /= 0.999f;
        	System.out.printf("%ld %f %f\n", numOfElements, dStream.readFloat(), fl);
        	
        	numOfElements += 1;
        }
        System.out.println("Value of all data is correct. Number of elements : " + numOfElements);
        dStream.close();
    }
}
