package org.ground.palico.gpu.aparapi;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;

public abstract class AparapiBandOperator {
    /**
     * 밴드 연산 실행 부분
     * 템플릿 메소드
     */
    public void run(int size, float[] band1, float[] band2, float[] band3, float[] result, Kernel.EXECUTION_MODE mode){
        Kernel kernel = getKernel(band1, band2, band3, result);
        Device device = Device.firstGPU();
        Range range = device.createRange(size);

        kernel.setExecutionMode(mode);
        kernel.execute(range);
        kernel.dispose();
    }

    protected abstract Kernel getKernel(float[] band1, float[] band2, float[] band3, float[] result);
}
