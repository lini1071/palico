package org.ground.palico.gpu.aparapi;

public class Operations {
    private static AparapiBandOperator operators[] = {
            new AparapiBandOperator0(),
            new AparapiBandOperator1(),
            new AparapiBandOperator2(),
            new AparapiBandOperator3(),
            new AparapiBandOperator4(),
            new AparapiBandOperator5(),
            new AparapiBandOperator6(),
            new AparapiBandOperator7(),
            new AparapiBandOperator8(),
            new AparapiBandOperator9()
    };

    public static AparapiBandOperator getOperation(int i) {
        return operators[i];
    }
}
