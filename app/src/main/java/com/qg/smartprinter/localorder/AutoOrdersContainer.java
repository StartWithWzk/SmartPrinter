package com.qg.smartprinter.localorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AutoOrdersContainer {
    private LinkedList<AutoOrder> mAutoOrderList;

    public AutoOrdersContainer() {
        mAutoOrderList = new LinkedList<>();
    }

    public void reset() {
        mAutoOrderList.clear();
    }

    public void addAutoOther(AutoOrder autoOrder) {
        mAutoOrderList.add(autoOrder);
    }

    public void addAutoOther(int bytes, int times) {
        addAutoOther(new AutoOrder(bytes, times));
    }

    public void remove() {
        if (!mAutoOrderList.isEmpty()) {
            mAutoOrderList.removeLast();
        }
    }

    public List<Integer> getOrders(boolean shuffle) {
        List<Integer> result = new ArrayList<>();
        for (AutoOrder autoOrder : mAutoOrderList) {
            for (int i = 0; i < autoOrder.times; i++) {
                result.add(autoOrder.bytes);
            }
        }
        if (shuffle) {
            Collections.shuffle(result);
        }
        return result;
    }

    public LinkedList<AutoOrder> getAutoOrderList() {
        return mAutoOrderList;
    }

    public static class AutoOrder {
        int bytes;
        int times;

        public AutoOrder(int bytes, int times) {
            this.bytes = bytes;
            this.times = times;
        }

        @Override
        public String toString() {
            return bytes + "*" + times;
        }
    }
}
