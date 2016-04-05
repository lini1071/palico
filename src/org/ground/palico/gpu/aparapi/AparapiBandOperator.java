package org.ground.palico.gpu.aparapi;

import com.amd.aparapi.Kernel;

public interface AparapiBandOperator {
    void run(int size, float[] band1, float[] band2, float[] band3, float[] result, Kernel.EXECUTION_MODE mode);
}
