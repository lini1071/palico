package org.ground.palico.lv2Generator;

import com.amd.aparapi.Kernel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CHLRawGenerator {
    Kernel.EXECUTION_MODE mode;
    String modeName;
    String srcFile;
    String defFile;
    private static final int MAXAVAILABLEMEM = 1024 * 1024 * 512;   // 512MB

    public CHLRawGenerator(String srcFile, String defFile, Kernel.EXECUTION_MODE mode) {
        this.srcFile = srcFile;
        this.defFile = defFile;
        this.mode = mode;
    }

    public String getModeName() {
        if (mode.equals(Kernel.EXECUTION_MODE.GPU)) {
            modeName = "GPU";
        } else if (mode.equals(Kernel.EXECUTION_MODE.JTP)) {
            modeName = "JTP";
        } else if (mode.equals(Kernel.EXECUTION_MODE.SEQ)) {
            modeName = "SEQ";
        }
        return modeName;
    }

    public void perform() throws IOException {
        AparapiMode am = new AparapiMode();
        SequentialMode sm = new SequentialMode();
        ByteBuffer readBuffer = ByteBuffer.allocate(MAXAVAILABLEMEM);
        try {
            FileChannel inputChannel = FileChannel.open(Paths.get(srcFile), StandardOpenOption.READ);
            FileChannel outputChannel = FileChannel.open(Paths.get(defFile), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            while (inputChannel.read(readBuffer) > 0) {
                //ByteBuffer -> byte[] -> floatBuffer -> float[]
                float[] band = new float[readBuffer.capacity() / Float.BYTES];
                int bandSize = band.length;

                //readBuffer(MAXAVAILABLEMEM 사이즈만큼 읽은 버퍼)를 배열화하여 array에 저장한다.
                byte[] array = readBuffer.array();

                //array를 floatBuffer화하여 floatBuffer에 저장한다.
                FloatBuffer inputBuffer = ByteBuffer.wrap(array).asFloatBuffer();

                //floatBuffer의 값을 band에 저장한다.
                inputBuffer.get(band);

                //알고리즘 수행 후 결과 값 output 배열에 저장
                float[] output;
                if (getModeName().equals("GPU") || getModeName().equals("JTP")) {
                    output = am.performByAparapi(band, bandSize, mode);
                } else {
                    output = sm.performBySequential(band, bandSize);
                }

                //float[] -> floatBuffer -> byte[] -> ByteBuffer
                //float[] -> flpatBuffer
                FloatBuffer floatBuffer = FloatBuffer.wrap(output);
                int obSize = floatBuffer.capacity();

                //floayBuffer -> byte[]
                ByteBuffer outputBuffer = ByteBuffer.allocate(obSize * Float.BYTES);
                outputBuffer.asFloatBuffer().put(floatBuffer);
                byte[] byteArray = outputBuffer.array();

                //byte[] -> byteBuffer
                ByteBuffer buf = ByteBuffer.wrap(byteArray);

                //write
                outputChannel.write(buf);
//                outputChannel.position(obSize);
                System.out.printf(buf.capacity() + " obSize : %d outputChannel: %d inputChannel: %d\n", obSize, outputChannel.position(), inputChannel.position());
                readBuffer.clear();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}