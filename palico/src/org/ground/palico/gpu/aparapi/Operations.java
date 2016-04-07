package org.ground.palico.gpu.aparapi;

import java.util.HashMap;
import java.util.Map;

public class Operations {
    Map<Integer, AparapiBandOperator> map = new HashMap<>();

    public void initializeOperation() {
        map.put(0, new AparapiBandOperator0());
        map.put(1, new AparapiBandOperator1());
        map.put(2, new AparapiBandOperator2());
        map.put(3, new AparapiBandOperator3());
        map.put(4, new AparapiBandOperator4());
        map.put(5, new AparapiBandOperator5());
        map.put(6, new AparapiBandOperator6());
        map.put(7, new AparapiBandOperator7());
        map.put(8, new AparapiBandOperator8());
        map.put(9, new AparapiBandOperator9());
    }

    public AparapiBandOperator getOperation(int i) {
        return map.get(i);
    }
}
