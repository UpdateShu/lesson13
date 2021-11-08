package ru.geekbrains.lessons;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class Road extends Stage {

    private static final CyclicBarrier BARRIER = new CyclicBarrier(MainClass.CARS_COUNT);
    private boolean isSynchronized = false;
    Object lock = new Object();

    public Road(int length, boolean isSynchronized) {
        this.length = length;
        this.description = "Дорога " + length + " метров";
        this.isSynchronized = isSynchronized;
    }
    @Override
    public void go(Car c) {
        try {
            if (isSynchronized) {
                BARRIER.await();
            }
            System.out.println(c.getName() + " начал этап: " + description);
            Thread.sleep(length / c.getSpeed() * 1000);
            if (isSynchronized) {
                System.out.println(c.getName() + " закончил этап: " + description);
            }
            else {
                synchronized (lock) {
                    System.out.println(c.getName() + " закончил этап: " + description);
                    if (MainClass.finishedCount.getAndIncrement() == 0) {
                        System.out.println(c.getName() + " - WIN");
                    }
                    else {
                        System.out.println(c.getName() + " финиширует " + MainClass.finishedCount.get() + "-ым");
                    }
                }
                MainClass.cdl.countDown();
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
