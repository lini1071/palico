package org.ground.palico;

import com.amd.aparapi.Kernel;
import org.ground.palico.base.SeqOperations;
import org.ground.palico.base.SequentialBandOperator;
import org.ground.palico.gpu.aparapi.AparapiBandOperator;
import org.ground.palico.gpu.aparapi.AparapiOpertions;

public class ExecutionMode {
    private Kernel.EXECUTION_MODE internalMode;
    private String name;
    private boolean useAparapi;

    public interface IBenchmarkRunner {
        float[] run(ComplexityBenchmarkInput input);
    }

    public static final ExecutionMode GPU = new ExecutionMode("GPU", Kernel.EXECUTION_MODE.GPU, true);
    public static final ExecutionMode JTP = new ExecutionMode("JTP", Kernel.EXECUTION_MODE.JTP, true);
    public static final ExecutionMode SEQ = new ExecutionMode("SEQ", Kernel.EXECUTION_MODE.SEQ);

    private ExecutionMode(String name, Kernel.EXECUTION_MODE mode, boolean useAparapi){
        this.name = name;
        this.internalMode = mode;
        this.useAparapi = useAparapi;
    }

    private ExecutionMode(String name, Kernel.EXECUTION_MODE mode){
        this(name, mode, false);
    }

    public String getName(){
        return name;
    }

    public IBenchmarkRunner getRunner(){
        return useAparapi ? (input) -> {
            final AparapiBandOperator operation = AparapiOpertions.getOperation(input.getComplexity());
            return operation.run(input, internalMode);
        }
                : (input) -> {
            final SequentialBandOperator operation = SeqOperations.getOperation(input.getComplexity());
            final ComplexityInputData data = input.getData();
            final int size = data.getDataSize();
            float[] result = new float[size];
            operation.run(size, data.getBand1(), data.getBand2(), data.getBand3(), result);
            return result;
        };
    }

    public float[] run(ComplexityBenchmarkInput input){
        return getRunner().run(input);
    }

    @Override
    public String toString() {
        return name;
    }
}
