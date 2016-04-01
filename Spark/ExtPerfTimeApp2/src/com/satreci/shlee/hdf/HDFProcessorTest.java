package com.satreci.shlee.hdf;

import org.junit.Test;

import com.amd.aparapi.Kernel;
import com.satreci.shlee.hdf.HDFProcessor;

public class HDFProcessorTest {
	public void testPerformByOpr(HDFProcessor hdfProcessor) {
		try {
			long start = System.currentTimeMillis();
			hdfProcessor.perform();
			long end = System.currentTimeMillis();
			String mode = hdfProcessor.getModeName();

			double spendTime = (end - start) / 1000.0;
			System.out.println(mode + " spend time : " + spendTime);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPerformance() {
		testPerformByOpr(new HDFProcessor(Kernel.EXECUTION_MODE.GPU));
		testPerformByOpr(new HDFProcessor(Kernel.EXECUTION_MODE.JTP));
	}

	@Test
	public void testCal() throws Exception {
		HDFProcessor hdfProcessor = new HDFProcessor(Kernel.EXECUTION_MODE.GPU);
		hdfProcessor.perform();
		/*
		 * float[] result = modeSuite.getResult();
		 * 
		 * float expected = 1.1f; assertEquals(expected, result[221], 0);
		 */
	}
}
