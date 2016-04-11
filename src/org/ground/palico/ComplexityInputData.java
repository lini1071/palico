package org.ground.palico;

public class ComplexityInputData {
    private static final int DATA_UNIT = 1024*1024;     // MEGA BYTES
    int pixelCount = 0;
    int dataSize = 0;
    float[] band1;
    float[] band2;
    float[] band3;

    public ComplexityInputData(int pixelCount){
        this.pixelCount = pixelCount;
        this.dataSize = pixelCount * DATA_UNIT;
        initialize();
    }

    public void initialize() {
        band1 = new float[dataSize];
        band2 = new float[dataSize];
        band3 = new float[dataSize];

        for (int i = 0; i < dataSize; i++) {
            band1[i] = 3.2345F;
            band2[i] = 7.5678F;
            band3[i] = 2.8456F;
        }
    }

    public float[] getBand1(){
        return band1;
    }

    public float[] getBand2(){
        return band2;
    }

    public float[] getBand3(){
        return band3;
    }

    public int getDataSize() {
        return dataSize;
    }
}
