package org.ground.palico.hdf;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;

class TestPlan {
    String modeName;
    Kernel.EXECUTION_MODE mode;

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


    public void perform(String srcFile, String destFile, String dataSet) throws Exception {
        HDFReader hdfReader = new HDFReader(srcFile);

        int[] band3 = hdfReader.getDataSet(3);
        int[] band4 = hdfReader.getDataSet(4);

        int size = hdfReader.getSize();
        float[] result = new float[size];


        for (int i = 0; i < result.length; i++) {
            result[i] = 0.0f;
        }
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

                if (band3[gid] == 0.0 || band4[gid] == 0.0) {
                    result[gid] = 0.0f;
                } else {
                    float r1 = log((float) band3[gid] / (float) band4[gid]) / log(10);
                    float r2 = r1 * r1;
                    float r3 = r2 * r1;
                    result[gid] = c0 + pow(10, c1 + c2 * r1 + c3 * r2 + c4 * r3);
                }
            }
        };
        Device device = Device.best();
        Range range = device.createRange(size);

        kernel.setExecutionMode(mode);
        kernel.execute(range);
        kernel.dispose();

        HDFCreator hdfCreator = new HDFCreator(destFile, dataSet, result);
        hdfCreator.create();
    }

}