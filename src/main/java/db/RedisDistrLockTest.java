package db;

import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by pandechuan on 2018/11/07.
 */
public class RedisDistrLockTest {

    private int counter = 0;

    private String lockKey = "lock";


    private void test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        IntStream.iterate(0, e -> e + 1)
                .limit(2000)
                .parallel()
                .forEach(e -> executorService.submit(() -> {
                    Jedis jedis = RedisUtil.getJedis();
                    try {
                        String requestId = UUID.randomUUID().toString();
                        boolean locked = RedisUtil.getDistributedLockByBlocking2(jedis, lockKey, requestId, 300, 1000L);
                        if (locked) {
                            counter++;
                            RedisUtil.releaseDistributedLock(jedis, lockKey, requestId);
                        } else {
                            System.out.println("unlocked");
                        }
                    } finally {
                        RedisUtil.close(jedis);
                    }

                }));
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println(counter);
    }

    public static void main(String[] args) throws InterruptedException {
        RedisDistrLockTest redisDistrLockTest = new RedisDistrLockTest();
        redisDistrLockTest.test();
//        Jedis jedis = RedisUtil.getJedis();
//        jedis.set("ooo","5555" );

    }

}


