package education.bert;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CacheConcurrentTest {
//    private Cache<Integer, String> cache;
//    private final int iterations = 100_000;
//
//    private void printProgress(int progress) {
//        System.out.printf("%3d%%", progress);
//    }
//
//    private void clearProgress() {
//        System.out.print("\b\b\b\b");
//    }
//
//    @Test
//    public void putGetCollisionTest() throws InterruptedException {
//        Map<Integer, String> expectedMap = new HashMap<>();
//        expectedMap.put(0, "Value0");
//        expectedMap.put(1, "Value1");
//
//        System.out.print(new Throwable().getStackTrace()[0].getMethodName() + ": ");
//        printProgress(0);
//        for (int i = 0; i < 100_000; i++) {
//            cache = new CacheLruImpl<>(3);
//            Thread threadOne = new Thread(() -> {
//                cache.put(0, "Value0");
//                assertEquals("Value0", cache.get(0));
//            });
//            Thread threadTwo = new Thread(() -> {
//                cache.put(1, "Value1");
//                assertEquals("Value1", cache.get(1));
//            });
//            threadOne.start();
//            threadTwo.start();
//            threadOne.join();
//            threadTwo.join();
//
//            assertEquals(expectedMap, cache.getMap());
//            assertEquals(2, cache.getKeys().size());
//
//            clearProgress();
//            printProgress(100 * (i + 1) / iterations);
//        }
//        System.out.println();
//    }
//
//    @Test
//    public void updateDeleteCollisionTest() throws InterruptedException {
//        System.out.print(new Throwable().getStackTrace()[0].getMethodName() + ": ");
//        printProgress(0);
//        for (int i = 0; i < 100_000; i++) {
//            cache = new CacheLruImpl<>(3);
//            cache.put(0, "Value");
//            Thread threadOne = new Thread(() -> cache.put(0, "New Value"));
//            Thread threadTwo = new Thread(() -> cache.remove(0));
//            threadOne.start();
//            threadTwo.start();
//            threadOne.join();
//            threadTwo.join();
//
//            assertEquals(cache.getMap().size(), cache.getKeys().size());
//
//            clearProgress();
//            printProgress(100 * (i + 1) / iterations);
//        }
//        System.out.println();
//    }
//
//    @Test
//    public void breakingLimitCollisionTest() throws InterruptedException {
//        System.out.print(new Throwable().getStackTrace()[0].getMethodName() + ": ");
//        printProgress(0);
//        for (int i = 0; i < 100_000; i++) {
//            cache = new CacheLruImpl<>(3);
//            cache.put(0, "Value0");
//            cache.put(1, "Value1");
//            Thread threadOne = new Thread(() -> cache.put(2, "Value2"));
//            Thread threadTwo = new Thread(() -> cache.put(3, "Value3"));
//            threadOne.start();
//            threadTwo.start();
//            threadOne.join();
//            threadTwo.join();
//
//            assertEquals(3, cache.getMap().size());
//            assertEquals(3, cache.getKeys().size());
//            assertNull(cache.get(0));
//
//            clearProgress();
//            printProgress(100 * (i + 1) / iterations);
//        }
//        System.out.println();
//    }
}
