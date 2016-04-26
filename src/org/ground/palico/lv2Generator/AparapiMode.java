package org.ground.palico.lv2Generator;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;

public class AparapiMode {
    public float[] performByAparapi(float[] band, int bandSize, Kernel.EXECUTION_MODE mode) {
        float c0 = -0.0929f;
        float c1 = 0.2974f;
        float c2 = -2.2429f;
        float c3 = 0.8358f;
        float c4 = -0.0077f;
        float[] result = new float[bandSize];
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
        Range range = device.createRange(bandSize);

        kernel.setExecutionMode(mode);
        kernel.execute(range);
        kernel.dispose();

        return result;
    }
}
