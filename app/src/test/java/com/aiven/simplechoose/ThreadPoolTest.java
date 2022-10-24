package com.aiven.simplechoose;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolTest {

    @Test
    public void threadPoolTest() {

        String TAG = ThreadPoolTest.class.getSimpleName();
        Set<String> failedList = new ConcurrentSkipListSet<>();
        ConcurrentSkipListSet<String> taskList = new ConcurrentSkipListSet<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 4000; ++i) {
            taskList.add("" + i);
        }
        for (int i = 0; i < 4000; ++i) {
            executorService.execute(new MyRunnable(taskList, failedList, "" + i));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            if (failedList.size() >= 10) {
                List<Runnable> runnableList = executorService.shutdownNow();
                Log.d(TAG, "任务执行失败，还有" + runnableList.size() + "个任务未执行");
                for (Runnable r: runnableList) {
                    Log.d(TAG, "任务：[" + ((MyRunnable)r).getTaskValue() + "]未执行");
                }
                break;
            }
        }
        Log.d(ThreadPoolTest.class.getSimpleName(), "所有线程池执行完毕");
        Iterator<String> iterator = taskList.iterator();
        while (iterator.hasNext()) {
            Log.d(ThreadPoolTest.class.getSimpleName(), "剩余任务---->" + iterator.next());
        }
        iterator = failedList.iterator();
        while (iterator.hasNext()) {
            Log.d(TAG, "失败任务--->" + iterator.next());
        }
    }
}
