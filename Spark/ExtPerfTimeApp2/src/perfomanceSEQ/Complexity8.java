package perfomanceSEQ;

/**
 * Created by shlee on 16. 3. 30.
 */
public class Complexity8 implements Complexity {

    @Override
    public void run(int size, float[] band1, float[] band2, float[] band3, float[] result) {
        for (int i = 0; i < size; i++) {
            result[i] = band1[i] / band2[i] / band3[i] * band1[i] * band2[i] / band3[i] /
                    band1[i] / band2[i] / band3[i] * band1[i] * band2[i] / band3[i] /
                    band1[i] / band2[i] / band3[i] * band1[i] * band2[i] / band3[i] /
                    band1[i] / band2[i] / band3[i] * band1[i] * band2[i] / band3[i] /
                    band1[i] / band2[i] / band3[i] * band1[i] * band2[i] / band3[i] /
                    band1[i] / band2[i] / band3[i] * band1[i] * band2[i] / band3[i] /
                    band1[i] / band2[i] / band3[i] * band1[i] * band2[i] / band3[i] /
                    band1[i] / band2[i] / band3[i] * band1[i] * band2[i] / band3[i];
        }
    }
}
