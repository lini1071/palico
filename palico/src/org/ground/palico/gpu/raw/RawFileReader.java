package org.ground.palico.gpu.raw;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class RawFileReader {
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
