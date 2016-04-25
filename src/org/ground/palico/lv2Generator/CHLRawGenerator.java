package org.ground.palico.lv2Generator;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CHLRawGenerator {
    Kernel.EXECUTION_MODE mode;
    String modeName;
    String srcFile;
    String defFile;
    private static final int MAXAVAILABLEMEM = 1024 * 1024 * 128;   // 128MB

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
        ByteBuffer readBuffer = ByteBuffer.allocate(MAXAVAILABLEMEM);
        try {
            FileChannel inputChannel = FileChannel.open(Paths.get(srcFile), StandardOpenOption.READ);
            FileChannel outputChannel = FileChannel.open(Paths.get(defFile), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            //inputChannel을 MAXAVAILABLEMEM 사이즈의 버퍼만큼 읽음.
            while (inputChannel.read(readBuffer) > 0) {
                //
                float[] band = new float[readBuffer.capacity() / Float.BYTES];
                int bandSize = band.length;
                //readBuffer(MAXAVAILABLEMEM 사이즈만큼 읽은 버퍼)를 배열화하여 array에 저장한다.
                byte[] array = readBuffer.array();
                //array를 floatBuffer화하여 floatBuffer에 저장한다.
                FloatBuffer floatBuffer = ByteBuffer.wrap(array).asFloatBuffer();
                //floatBuffer의 값을 band에 저장한다.(ByteBuffer -> byte[] -> floatBuffer -> float[])
                floatBuffer.get(band);
                //알고리즘 수행 후 결과 값 output 배열에 저
                float[] output;
                if (getModeName().equals("GPU") || getModeName().equals("JTP")) {
                    output = performByAparapi(band);
                } else {
                    output = performBySequential(band, bandSize);
                }
                //float[] -> FloatBuffer -> ByteBuffer)
                //output 배열을 floatBuffer화하여 outputBuffer에 저장
                FloatBuffer outputBuffer = FloatBuffer.wrap(output);
                int obSize = outputBuffer.capacity();
                //byteBuffer 생성
                ByteBuffer byteBuffer = ByteBuffer.allocate(obSize * Float.BYTES);
                byteBuffer.order(ByteOrder.nativeOrder());
                byteBuffer.putFloat(outputBuffer.get());
//                byteBuffer.asFloatBuffer().put(outputBuffer);
                outputChannel.write(byteBuffer);
                outputChannel.position(obSize);
                System.out.printf("obSize : %d outputChannel: %d inputChannel: %d\n", obSize, outputChannel.position(), inputChannel.position());
                readBuffer.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public float[] perform(float[] band) {
        return band;
    }

    public float[] performBySequential(float[] band, int bandSize) {
        float c0 = -0.0929f;
        float c1 = 0.2974f;
        float c2 = -2.2429f;
        float c3 = 0.8358f;
        float c4 = -0.0077f;

        float[] result = new float[bandSize];
        for (int i = 0; i < bandSize; i++) {
            if (band[i] == 0.0f) {
                result[i] = 0.0f;
            } else {
                float r1 = (float) Math.log10(band[i] / band[i]);
                float r2 = r1 * r1;
                float r3 = r2 * r1;
                result[i] = (float) (c0 + Math.pow(10, c1 + c2 * r1 + c3 * r2 + c4 * r3));
            }
        }
        return result;
    }

    public float[] performByAparapi(float[] band) {
        float c0 = -0.0929f;
        float c1 = 0.2974f;
        float c2 = -2.2429f;
        float c3 = 0.8358f;
        float c4 = -0.0077f;
        float[] result = new float[MAXAVAILABLEMEM];
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();

                if (band[gid] == 0.0 || band[gid] == 0.0) {
                    result[gid] = 0.0f;
                } else {
                    float r1 = log(band[gid] / band[gid]) / log(10);
                    float r2 = r1 * r1;
                    float r3 = r2 * r1;
                    result[gid] = c0 + pow(10, c1 + c2 * r1 + c3 * r2 + c4 * r3);
                }
            }
        };
        Device device = Device.firstGPU();
        Range range = device.createRange(MAXAVAILABLEMEM);

        kernel.setExecutionMode(mode);
        kernel.execute(range);
        kernel.dispose();

        return result;
    }
}