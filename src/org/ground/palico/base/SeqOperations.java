package org.ground.palico.base;

import java.util.HashMap;
import java.util.Map;

public class SeqOperations {
    private static SequentialBandOperator[] operators = {
            new SequentialBandOperator0(),
            new SequentialBandOperator1(),
            new SequentialBandOperator2(),
            new SequentialBandOperator3(),
            new SequentialBandOperator4(),
            new SequentialBandOperator5(),
            new SequentialBandOperator6(),
            new SequentialBandOperator7(),
            new SequentialBandOperator8(),
            new SequentialBandOperator9(),
    };

    public static SequentialBandOperator getOperation(int i) {
        return operators[i];
    }
}
