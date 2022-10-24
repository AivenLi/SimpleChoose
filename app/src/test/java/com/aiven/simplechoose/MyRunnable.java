package com.aiven.simplechoose;

import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class MyRunnable implements Runnable {

    private ConcurrentSkipListSet<String> concurrentSkipListSet;
    private Set<String> failedSet;
    private String value;

    public MyRunnable(ConcurrentSkipListSet<String> concurrentSkipListSet, Set<String> failedSet, String value) {
        this.concurrentSkipListSet = concurrentSkipListSet;
        this.failedSet = failedSet;
        this.value = value;
    }

    @Override
    public void run() {
        Log.d(MyRunnable.class.getSimpleName(), "---------线程[" + value + "]开始执行------------");
        Random random = new Random();
        int v = random.nextInt(10);
        Log.d(MyRunnable.class.getSimpleName(), "随机数：" + v);
        if (v == 5) {
            failedSet.add(value);
        }
        concurrentSkipListSet.remove(value);
        Log.d(MyRunnable.class.getSimpleName(), "---------线程[" + value + "]结束------------");
    }

    public String getTaskValue() {
        return value;
    }
}
