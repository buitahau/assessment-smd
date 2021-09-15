package net.bigpoint.assessment.gasstation.util;

public class ThreadUtil {

    public static void sleep(int time) {
        try {
            Thread.sleep((long) (time * 100));
        } catch (InterruptedException e) {
            // ignored
        }
    }
}
