import com.amd.aparapi.Kernel;
import com.opencsv.CSVWriter;
import org.junit.Test;

import java.io.FileWriter;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class PerfomanceTest {

    public void testPerformByMode(ModeSuite suite) {
        int mega = 1;
        int cnt = 1;
        String fileName = "/home/shlee/csv/perfomance_result5.csv";
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(fileName, true));
            while (mega < 65) {
                String[] data = new String[4];
                suite.setSize(mega * 1024 * 1024);

                for (int i = 0; i < 10; i++) {
                    suite.setComplexity(i);
                    long start = System.currentTimeMillis();
                    suite.perform();
                    long end = System.currentTimeMillis();

                    data[0] = suite.getModeName();
                    data[1] = String.valueOf(mega + " mega");
                    data[2] = String.valueOf(i);
                    data[3] = String.valueOf((end - start) / 1000.0);
                    writer.writeNext(data);
                    System.out.printf("No.%d %s [mega : %d] [복잡도 : %d] 실행 시간 : %f\n", cnt, suite.getModeName(), mega, i, (end - start) / 1000.0);
                    cnt++;
                }
                mega += 7;
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGPU() {

        testPerformByMode(new ModeSuite(Kernel.EXECUTION_MODE.GPU));
    }

    @Test
    public void testJTP() {
        testPerformByMode(new ModeSuite(Kernel.EXECUTION_MODE.JTP));
    }

    @Test
    public void testSEQ() {
        testPerformByMode(new ModeSuite(Kernel.EXECUTION_MODE.SEQ));
    }

    @Test
    public void testAll() {
        testPerformByMode(new ModeSuite(Kernel.EXECUTION_MODE.GPU));
        testPerformByMode(new ModeSuite(Kernel.EXECUTION_MODE.JTP));
        testPerformByMode(new ModeSuite(Kernel.EXECUTION_MODE.SEQ));
    }


    public void testCal(ModeSuite suite) {
        HashMap<Integer, Float> expected = new HashMap<>();
        expected.put(0, 3.2345f);
        expected.put(1, 1.292012179f);
        expected.put(2, 0.159558117f);
        expected.put(3, 0.019704762f);
        expected.put(4, 0.002433456f);
        expected.put(5, 0.000300522f);
        expected.put(6, 0.000037113f);
        expected.put(7, 0.000004583f);
        expected.put(8, 0.000000566f);
        expected.put(9, 0.00000007f);

        int mega = 1;
        while (mega < 65) {
            int size = mega * 1 * 1;
            for (int i = 0; i < 10; i++) {
                suite.setSize(size);
                suite.setComplexity(i);
                for (int j = 0; j < size; j++) {
                    System.out.println(mega + ", " + i + ", " + j + ", " + suite.perform()[j]);
                    assertEquals(expected.get(i), suite.perform()[j], 1e-4);
                }
            }
            mega += 20;
        }
    }

    @Test
    public void testCalGPU() {
        testCal(new ModeSuite(Kernel.EXECUTION_MODE.GPU));
        testCal(new ModeSuite(Kernel.EXECUTION_MODE.JTP));
        testCal(new ModeSuite(Kernel.EXECUTION_MODE.SEQ));
    }

}