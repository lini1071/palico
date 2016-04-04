package com.satreci.shlee.binary;

import java.io.*;

public class BinaryReader {

    public void read(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DataInputStream dis = new DataInputStream(bis);

        while (dis.available() > 0) {
            System.out.println(dis.readFloat());
        }
        dis.close();
    }
}
