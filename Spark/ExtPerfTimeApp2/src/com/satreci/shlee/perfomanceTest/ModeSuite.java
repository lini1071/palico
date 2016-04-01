package com.satreci.shlee.perfomanceTest;

import com.amd.aparapi.Kernel;
import com.satreci.shlee.perfomanceSEQ.OperationSEQ;
import com.satreci.shlee.performanceMode.OperationMode;

class ModeSuite {
	int size = 0;
	int complexity = 0;
	String modeName;
	Kernel.EXECUTION_MODE mode;

	public ModeSuite(Kernel.EXECUTION_MODE mode) {
		this.mode = mode;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public String getModeName() {
		if (mode.equals(Kernel.EXECUTION_MODE.GPU)) {
			modeName = "GPU";
		} else if (mode.equals(Kernel.EXECUTION_MODE.JTP)) {
			modeName = "JTP";
		} else if (mode.equals(Kernel.EXECUTION_MODE.SEQ)) {
			modeName = "SEQ";
		}
		return modeName;
	}

	public void perform() {
		final float[] band1 = new float[size];
		final float[] band2 = new float[size];
		final float[] band3 = new float[size];
		final float[] result = new float[size];

		for (int i = 0; i < size; i++) {
			band1[i] = 3.2345F;
			band2[i] = 7.5678F;
			band3[i] = 2.8456F;
		}

		if (mode.equals(Kernel.EXECUTION_MODE.GPU) || mode.equals(Kernel.EXECUTION_MODE.JTP)) {
			OperationMode operationMode = new OperationMode();
			operationMode.initializeOperation();
			operationMode.getOperation(complexity).run(size, band1, band2, band3, result, mode);
		} else {
			OperationSEQ operationSeq = new OperationSEQ();
			operationSeq.initializeOperation();
			operationSeq.getOperation(complexity).run(size, band1, band2, band3, result);
		}
	}
}
