package performanceMode;

import com.amd.aparapi.Kernel;

/**
 * Created by shlee on 16. 3. 30.
 */
public interface Complexity {
    void run(int size, float[] band1, float[] band2, float[] band3, float[] result, Kernel.EXECUTION_MODE mode);
}
