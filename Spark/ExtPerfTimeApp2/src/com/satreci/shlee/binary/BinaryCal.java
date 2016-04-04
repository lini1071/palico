package com.satreci.shlee.binary;

import java.io.*;

public class BinaryCal {
    public float[] read(String fileName, int mega) throws IOException {

        DataInputStream dis = getDis(fileName);
        int cnt = 0;
        while (dis.available() > 0) {
            dis.readFloat();
            cnt++;
        }
        dis.close();

        int size = (int) Math.ceil(((double)cnt / (double)mega));

        System.out.println("size = " + size + ", " + cnt + ", " + mega);

        DataInputStream dis2 = getDis(fileName);
        for(int j = 0; j < size; j++){
            float result;
        }
        float[] result = new float[cnt];
        for (int i = 0; i < cnt; i++) {
            result[i] = dis2.readFloat();
        }
        dis2.close();

        return result;
    }

    public DataInputStream getDis(String fileName) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DataInputStream dis = new DataInputStream(bis);
        return dis;
    }
}
