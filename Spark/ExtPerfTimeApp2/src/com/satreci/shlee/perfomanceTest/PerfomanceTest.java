package com.satreci.shlee.perfomanceTest;

import com.amd.aparapi.Kernel;
import com.opencsv.CSVWriter;
import com.satreci.shlee.binary.BinaryCal;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

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

				for (int i = 9; i < 10; i++) {
					suite.setComplexity(i);
					long start = System.currentTimeMillis();
					suite.perform();
					long end = System.currentTimeMillis();

					data[0] = suite.getModeName();
					data[1] = String.valueOf(mega + " mega");
					data[2] = String.valueOf(i);
					data[3] = String.valueOf((end - start) / 1000.0);
					writer.writeNext(data);
					// System.out.printf("No.%d %s [mega : %d] [복잡도 : %d] 실행 시간
					// : %f\n", cnt, suite.getModeName(), mega, i, (end - start)
					// / 1000.0);
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

	@Test
	public void testCalMode() {
		float expected = 0.000000444f;
		int size = 1 * 1024 * 1024;
		float[] band1 = new float[size];
		float[] band2 = new float[size];
		float[] band3 = new float[size];
		float[] result = new float[size];

		for (int i = 0; i < size; i++) {
			band1[i] = 3.2345F;
			band2[i] = 7.5678F;
			band3[i] = 2.8456F;
		}

		com.satreci.shlee.performanceMode.Complexity2 complexityMode = new com.satreci.shlee.performanceMode.Complexity2();
		complexityMode.run(1, band1, band2, band3, result, Kernel.EXECUTION_MODE.GPU);
		assertEquals(expected, result[0], 1e-7);
	}

	@Test
	public void testCalSEQ() {
		float expected = 0.000000444f; // (3.2345 / 7.5678 / 2.8456) =
										// 0.15019783
		int size = 1 * 1024 * 1024;
		float[] band1 = new float[size];
		float[] band2 = new float[size];
		float[] band3 = new float[size];
		float[] result = new float[size];

		for (int i = 0; i < size; i++) {
			band1[i] = 3.2345F;
			band2[i] = 7.5678F;
			band3[i] = 2.8456F;
		}

		com.satreci.shlee.perfomanceSEQ.Complexity2 complexitySEQ = new com.satreci.shlee.perfomanceSEQ.Complexity2();
		complexitySEQ.run(1, band1, band2, band3, result);
		assertEquals(expected, result[0], 1e-7);
	}
	@Test
	public void testBinary() throws IOException {
		BinaryCal br = new BinaryCal();
//        float[] result = br.read("/home/shlee/workspace/sparksample1/SampleImageFileGenerator/C1.img");
//
//        for(int i = 0; i < result.length; i++){
//            System.out.println(i + "," + result[i]);
//        }

	}
}