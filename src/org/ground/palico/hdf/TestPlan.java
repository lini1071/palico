package org.ground.palico.hdf;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class TestPlan {
    private String modeName;
    private Kernel.EXECUTION_MODE mode;
    private static final int MAXAVAILABLEMEM = 89448448;
    private int cnt = 0;

    public TestPlan(Kernel.EXECUTION_MODE mode) {
        this.mode = mode;
    }

    public String getModeName() {
        if (mode.equals(Kernel.EXECUTION_MODE.CPU)) {
            modeName = "CPU";
        } else if (mode.equals(Kernel.EXECUTION_MODE.GPU)) {
            modeName = "GPU";
        } else if (mode.equals(Kernel.EXECUTION_MODE.JTP)) {
            modeName = "JTP";
        }
        return modeName;
    }


    public float[] perform(String srcFile, String destFile, String dataSet) throws Exception {
        HDFReader hdfReader = new HDFReader(srcFile);

        int[] band3 = hdfReader.getDataSet(3);
        int[] band4 = hdfReader.getDataSet(4);
        int bandSize = hdfReader.getSize();

        cnt = bandSize / MAXAVAILABLEMEM;
        if (bandSize % MAXAVAILABLEMEM != 0) {
            cnt++;
        }

        //band 배열을 분할하여 map에 저장
        Map map1 = splitArray(band3);
        Map map2 = splitArray(band4);

        float[] result = new float[bandSize];
        for (int i = 0; i < result.length; i++) {
            result[i] = 0.0f;
        }
        //CHL OC2 알고리즘
        float c0 = -0.0929f;
        float c1 = 0.2974f;
        float c2 = -2.2429f;
        float c3 = 0.8358f;
        float c4 = -0.0077f;
        Map resultMap = new HashMap<>();

        for (int j = 0; j < cnt; j++) {
            int[] splitBand1 = (int[]) map1.get(j);
            int[] splitBand2 = (int[]) map2.get(j);
            float[] splitResult = new float[MAXAVAILABLEMEM];

            parallelPerform(bandSize, result, c0, c1, c2, c3, c4, splitBand1, splitBand2, splitResult);

            resultMap.put(j, splitResult);
        }
        System.arraycopy(resultMap.get(0), 0, result, 0, MAXAVAILABLEMEM);
        System.arraycopy(resultMap.get(1), 0, result, 0, bandSize - MAXAVAILABLEMEM);

        return result;
    }

    private void parallelPerform(int bandSize, float[] result, final float c0, final float c1, final float c2, final float c3, final float c4, final int[] spliteBand1, final int[] spliteBand2, final float[] spliteResult) throws IOException {
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();

                if (spliteBand1[gid] == 0.0 || spliteBand2[gid] == 0.0) {
                    spliteResult[gid] = 0.0f;
                } else {
                    float r1 = log((float) spliteBand1[gid] / (float) spliteBand2[gid]) / log(10);
                    float r2 = r1 * r1;
                    float r3 = r2 * r1;
                    spliteResult[gid] = c0 + pow(10, c1 + c2 * r1 + c3 * r2 + c4 * r3);
                }
            }
        };
        Device device = Device.best();
        Range range = device.createRange(bandSize);

        kernel.setExecutionMode(mode);
        kernel.execute(range);
        kernel.dispose();

        RawCreator rawCreator = new RawCreator("data/CHL.bin", result);
        rawCreator.create();

//        HDFCreator hdfCreator = new HDFCreator(destFile, dataSet, result);
//        hdfCreator.create();
    }

    private Map splitArray(int[] band) {
        Map map = new HashMap<>();

        int totalIndex = 0;
        for (int i = 0; i < cnt; i++) {
            map.put(i, new int[MAXAVAILABLEMEM]);
            for (int j = 0; j < MAXAVAILABLEMEM; j++) {
                if (totalIndex >= 126593580) {
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
