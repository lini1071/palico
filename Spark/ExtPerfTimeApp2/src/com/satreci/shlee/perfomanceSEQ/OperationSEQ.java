package com.satreci.shlee.perfomanceSEQ;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shlee on 16. 3. 30.
 */
public class OperationSEQ {
    Map<Integer, Complexity> Operation = new HashMap<>();

    public void initializeOperation() {
        Operation.put(0, new Complexity0());
        Operation.put(1, new Complexity1());
        Operation.put(2, new Complexity2());
        Operation.put(3, new Complexity3());
        Operation.put(4, new Complexity4());
        Operation.put(5, new Complexity5());
        Operation.put(6, new Complexity6());
        Operation.put(7, new Complexity7());
        Operation.put(8, new Complexity8());
        Operation.put(9, new Complexity9());
    }

    public Complexity getOperation(int i) {
        return Operation.get(i);
    }
}
