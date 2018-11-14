import db.RedisUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by pandechuan on 2018/11/07.
 */
public class RedisDistrLookTest {

    //  private int counter = 0;
    private static int counter = 0;

    private static String lockKey = "lock";

    private static Jedis jedis = RedisUtil.getJedis();

//    @Test
//    public void test() throws InterruptedException {
//        ExecutorService executorService = Executors.newFixedThreadPool(50);
//        IntStream.iterate(0, e -> e + 1)
//                .limit(2000)
//                .parallel()
//                .forEach(e -> executorService.submit(() -> {
//                    //String requestId = UUID.randomUUID().toString();
//                    // String requestId = String.valueOf(e);
//                    //RedisUtil.tryGetDistributedLock(jedis, lockKey, requestId, 300);
//                    counter++;
//                    // RedisUtil.releaseDistributedLock(jedis, lockKey, requestId);
//                }));
//        executorService.shutdown();
//        executorService.awaitTermination(1, TimeUnit.MINUTES);
//        System.out.println(counter);
//
//    }

    @Test
    public void test2(){
        System.out.println(222);
    }

//    public static void main(String[] args) throws Exception {
//        int counter = 0;
//
//        String lockKey = "lock";
//
//        Jedis jedis = RedisUtil.getJedis();
//
//        ExecutorService executorService = Executors.newFixedThreadPool(50);
//        IntStream.iterate(0, e -> e + 1)
//                .limit(2000)
//                .parallel()
//                .forEach(e -> executorService.submit(() -> {
//                    String requestId = UUID.randomUUID().toString();
//                    // String requestId = String.valueOf(e);
//                    RedisUtil.tryGetDistributedLock(jedis, lockKey, requestId, 300);
//                   // counter++;
//                    RedisUtil.releaseDistributedLock(jedis, lockKey, requestId);
//                }));
//        executorService.shutdown();
//        executorService.awaitTermination(1, TimeUnit.SECONDS);
//        System.out.println(counter);
//    }

}
