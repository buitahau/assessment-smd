package net.bigpoint.assessment.gasstation.util;

public class ThreadUtil {

    public static void sleep(int times) {
        try {
            Thread.sleep((long) (times * 100));
        } catch (InterruptedException e) {
            // ignored
        }
    }
}
