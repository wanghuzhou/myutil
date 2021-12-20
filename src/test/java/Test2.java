import org.junit.Test;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Test2 {

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 100, 10,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));


    public void test1() {
        for (int i = 0; i < 100; i++) {
            executor.execute(Test2::test2);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            executor.execute(Test2::test2);
        }
    }

    public static void test2() {
        for (int i = 0; i < 100000; i++) {
           /* try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            System.out.println(Thread.currentThread().getName() + " " + i);
        }
    }
}
