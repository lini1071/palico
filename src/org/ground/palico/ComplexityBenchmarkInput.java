package org.ground.palico;

public class ComplexityBenchmarkInput {
    private ComplexityInputData data;
    private int complexity = 0;

    public ComplexityBenchmarkInput(ComplexityInputData data, int complexity){
        this.data = data;
        this.complexity = complexity;
    }

    public int getComplexity(){
        return complexity;
    }

    public ComplexityInputData getData(){
        return data;
    }
}
