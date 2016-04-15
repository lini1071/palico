package org.ground.palico.gpu.aparapi;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.amd.aparapi.device.Device;
import org.ground.palico.ComplexityBenchmarkInput;
import org.ground.palico.ComplexityInputData;

public abstract class AparapiBandOperator {
    /**
     * 밴드 연산 실행 부분
     * 템플릿 메소드
     */
    public float[] run(ComplexityBenchmarkInput input, Kernel.EXECUTION_MODE mode){
        final ComplexityInputData data = input.getData();
        final int size = data.getDataSize();
        final float[] result = new float[size];
        Kernel kernel = getKernel(data.getBand1(), data.getBand2(), data.getBand3(), result);
        Device device = Device.best();
        Range range = device.createRange(size);

        kernel.setExecutionMode(mode);
        kernel.execute(range);
        kernel.dispose();
        return result;
    }

    protected abstract Kernel getKernel(float[] band1, float[] band2, float[] band3, float[] result);
}
