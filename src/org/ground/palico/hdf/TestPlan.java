package org.ground.palico.hdf;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestPlan {
    private String modeName;
    private Kernel.EXECUTION_MODE mode;
    private static final int MAXAVAILABLEMEM = 89448448;
    private static final int DATASETSIZE = 126593580;
    private int numOfSplit = 0;

    public TestPlan(Kernel.EXECUTION_MODE mode) {
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


    public float[] perform(String srcFile, String destFile, String dataSet) throws Exception {
        HDFReader hdfReader = new HDFReader(srcFile);

        int[] band3 = hdfReader.getDataSet(3);
        int[] band4 = hdfReader.getDataSet(4);

        int bandSize = hdfReader.getSize();
        float[] result = new float[bandSize];
        for (int i = 0; i < bandSize; i++) {
            result[i] = 0.0f;
        }

        if (modeName == "GPU" || modeName == "JTP") {
            result = performByAparapi(band3, band4, bandSize, result);
        } else {
            result = performBySequential(band3, band4, bandSize, result);
        }

        RawCreator rawCreator = new RawCreator("data/CHL.bin", result);
        rawCreator.create();

        return result;
    }

    private float[] performBySequential(int[] band3, int[] band4, int bandSize, float[] result) throws IOException {
        //CHL OC2 알고리즘
        float c0 = -0.0929f;
        float c1 = 0.2974f;
        float c2 = -2.2429f;
        float c3 = 0.8358f;
        float c4 = -0.0077f;

        for (int i = 0; i < bandSize; i++) {
            if (band3[i] == 0.0 || band4[i] == 0.0) {
                result[i] = 0.0f;
            } else {
                float r1 = (float) Math.log10((float) band3[i] / (float) band4[i]);
                float r2 = r1 * r1;
                float r3 = r2 * r1;
                result[i] = (float) (c0 + Math.pow(10, c1 + c2 * r1 + c3 * r2 + c4 * r3));
            }
        }
        return result;
    }

    private float[] performByAparapi(int[] band3, int[] band4, int bandSize, float[] result) throws IOException {
        //할당 할 갯수 생성
        numOfSplit = bandSize / MAXAVAILABLEMEM;
        if (bandSize % MAXAVAILABLEMEM != 0) {
            numOfSplit++;
        }

        //band 배열을 분할하여 map에 저장
        Map band3Map = splitArray(band3, bandSize);
        Map band4Map = splitArray(band4, bandSize);
        Map resultMap = new HashMap<>();

        //알고리즘 수행
        for (int j = 0; j < numOfSplit; j++) {
            int[] splitBand3 = (int[]) band3Map.get(j);
            int[] splitBand4 = (int[]) band4Map.get(j);
            float[] splitResult = new float[MAXAVAILABLEMEM];

            parallelCalculate(splitResult.length, splitBand3, splitBand4, splitResult);
            resultMap.put(j, splitResult);
        }

        //분할된 배열을 하나의 배열로 병합
        System.arraycopy(resultMap.get(0), 0, result, 0, MAXAVAILABLEMEM);
        System.arraycopy(resultMap.get(1), 0, result, MAXAVAILABLEMEM, bandSize - MAXAVAILABLEMEM);

        return result;
    }

    private void parallelCalculate(int splitSize, final int[] splitBand3, final int[] splitBand4, final float[] splitResult) throws IOException {
        //CHL OC2 알고리즘
        float c0 = -0.0929f;
        float c1 = 0.2974f;
        float c2 = -2.2429f;
        float c3 = 0.8358f;
        float c4 = -0.0077f;

        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();

                if (splitBand3[gid] == 0.0 || splitBand4[gid] == 0.0) {
                    splitResult[gid] = 0.0f;
                } else {
                    float r1 = log((float) splitBand3[gid] / (float) splitBand4[gid]) / log(10);
                    float r2 = r1 * r1;
                    float r3 = r2 * r1;
                    splitResult[gid] = c0 + pow(10, c1 + c2 * r1 + c3 * r2 + c4 * r3);
                }
            }
        };
        Device device = Device.best();
        Range range = device.createRange(splitSize);

        kernel.setExecutionMode(mode);
        kernel.execute(range);
        kernel.dispose();
    }

    private Map splitArray(int[] band, int bandSize) {
        Map map = new HashMap<>();

        int totalIndex = 0;
        for (int i = 0; i < numOfSplit; i++) {
            map.put(i, new int[MAXAVAILABLEMEM]);
            for (int j = 0; j < MAXAVAILABLEMEM; j++) {
                if (totalIndex >= bandSize) {
                    ((int[]) map.get(i))[j] = 0;
                } else {
                    ((int[]) map.get(i))[j] = band[totalIndex];
                }
                totalIndex++;
            }
        }
        return map;
    }
}
