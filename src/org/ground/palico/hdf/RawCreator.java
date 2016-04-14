package org.ground.palico.hdf;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class RawCreator {
    private String fileName;
    private float[] data;

    public RawCreator(String fileName, float[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    public void create() throws IOException {
        //버퍼 사이즈 8KB
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        for (int i = 0; i < data.length; i++) {
            dataOutputStream.writeFloat(data[i]);
        }
    }
}
