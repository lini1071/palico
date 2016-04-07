package org.ground.palico.base;

import java.util.HashMap;
import java.util.Map;

public class Operations {
    Map<Integer, SequentialBandOperator> map = new HashMap<>();

    public void initializeOperation() {
        map.put(0, new SequentialBandOperator0());
        map.put(1, new SequentialBandOperator1());
        map.put(2, new SequentialBandOperator2());
        map.put(3, new SequentialBandOperator3());
        map.put(4, new SequentialBandOperator4());
        map.put(5, new SequentialBandOperator5());
        map.put(6, new SequentialBandOperator6());
        map.put(7, new SequentialBandOperator7());
        map.put(8, new SequentialBandOperator8());
        map.put(9, new SequentialBandOperator9());
    }

    public SequentialBandOperator getOperation(int i) {
        return map.get(i);
    }
}
