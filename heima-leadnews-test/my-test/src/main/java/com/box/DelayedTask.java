package com.box;

import java.util.Calendar;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 测试jdk自带的延迟队列
 *
 * @author 12141
 */
public class DelayedTask implements Delayed {


    private int executedTime = 0;

    public DelayedTask(int delay) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, delay);
        this.executedTime = (int) (cal.getTimeInMillis() / 1000);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        Calendar calendar = Calendar.getInstance();
        return executedTime - calendar.getTimeInMillis() / 1000;
    }

    @Override
    public int compareTo(Delayed o) {
        long val = this.getDelay(TimeUnit.SECONDS) - o.getDelay(TimeUnit.SECONDS);
        return val == 0 ? 0 : (val < 0 ? -1 : 1);
    }

    public static void main(String[] args) {

        DelayQueue<DelayedTask> queue = new DelayQueue<>();

        queue.add(new DelayedTask(5));
        queue.add(new DelayedTask(10));
        queue.add(new DelayedTask(15));

        System.out.println(System.currentTimeMillis() / 1000 + "start consume");
        while (queue.size() != 0) {
            DelayedTask delayedTask = queue.poll();
            if (delayedTask != null){
                System.out.println(System.currentTimeMillis()/1000+" consume task");
            }

            //每隔一秒消费一次
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

}
