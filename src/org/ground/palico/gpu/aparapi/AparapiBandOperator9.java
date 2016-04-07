package org.ground.palico.gpu.aparapi;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;

public class AparapiBandOperator9 extends AparapiBandOperator {
    @Override
    public Kernel getKernel(float[] band1, float[] band2, float[] band3, float[] result) {
        return new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
                result[gid] = band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid] /
                        band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid] /
                        band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid] /
                        band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid] /
                        band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid] /
                        band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid] /
                        band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid] /
                        band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid] /
                        band1[gid] / band2[gid] / band3[gid] / band1[gid] / band2[gid] / band3[gid];
            }
        };
    }
}
