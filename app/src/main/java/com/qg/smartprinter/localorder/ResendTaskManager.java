package com.qg.smartprinter.localorder;

import com.qg.smartprinter.localorder.messages.BOrder;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 任务循环机
 */
public class ResendTaskManager {
    private static final int DELAY_INCREMENT_MIN = 2 * 1000; // 2s

    private Timer mTimer;
    private Map<Long, TaskRecycler> mTaskMap;

    public ResendTaskManager() {
        mTimer = new Timer();
        mTaskMap = new HashMap<>();
    }

    public static abstract class Task {
        private long id;
        private int delay;
        private int delayIncrement;

        protected Task(BOrder order) {
            int delay = (int) Math.ceil((float) order.getBytesLength() / 1000); // Unit : second.
            delay = delay <= 0 ? 1 : delay; // Guard negative number.
            delay += 5; // +5s
            delay *= 1000; // Second to millisecond.
            this.id = order.getOrderNumber();
            this.delay = delay;
            this.delayIncrement = DELAY_INCREMENT_MIN;
        }

        private int delayIncrement() {
            return delayIncrement += 2 * 1000;// +2s
        }

        /**
         * 下一次延时时间
         */
        int nextDelay() {
            return 30 * 1000; // 固定30s
//            // 每一次延时的时间都会增加
//            int temp = delay;
//            delay = temp + delayIncrement(); // Next delay time.
//            Log.d(TAG, timeMsg);
//            Log.d(TAG, delayMsg);
//            return temp;
        }

        public long getId() {
            return id;
        }

        protected abstract void run();
    }

    /**
     * 开始任务, 自动循环
     */
    public void startTask(Task task) {
        mTaskMap.put(task.id, scheduleTask(task));
        task.run();
    }

    private TaskRecycler scheduleTask(final Task task) {
        TaskRecycler t = new TaskRecycler(task);
        mTimer.schedule(t, task.nextDelay());
        return t;
    }

    public void clearTasks() {
        for (TaskRecycler taskRecycler : mTaskMap.values()) {
            taskRecycler.cancel();
        }

        mTaskMap.clear();
    }

    /**
     * 完成任务
     *
     * @param id 任务id
     */
    public boolean finishTask(long id) {
        TaskRecycler remove = mTaskMap.remove(id);
        if (remove != null) {
            remove.cancel();
            return true;
        }
        return false;
    }

    /**
     * 是否还有循环中的任务
     */
    public boolean hasTask() {
        return mTaskMap.size() > 0;
    }

    /**
     * 任务执行器, 若未完成则继续循环
     */
    private class TaskRecycler extends TimerTask {
        private Task task;

        private TaskRecycler(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            if (mTaskMap.containsKey(task.id)) {
                // 重启任务
                startTask(task);
            }
        }
    }
}
