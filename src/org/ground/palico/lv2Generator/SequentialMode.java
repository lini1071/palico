package org.ground.palico.lv2Generator;

public class SequentialMode {
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
}
