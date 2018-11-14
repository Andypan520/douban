package db;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Created by pandechuan on 2018/09/22.
 */
public class RedisUtil {
    private static final String addr;
    private static final int port;
    private static final String passwd;

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final long SLEEP_TIME = 10L;


    // 加载配置文件
    private static ResourceBundle rb = ResourceBundle.getBundle("redis-config");

    // 初始化连接
    static {
        addr = rb.getString("jedis.addr");
        port = Integer.parseInt(rb.getString("jedis.port"));
        passwd = rb.getString("jedis.passwd");

        try {
            // 先进行redis数据的参数配置
            JedisPoolConfig config = new JedisPoolConfig();
            // 链接耗尽时是否阻塞，false时抛出异常，默认是true，阻塞超时之后抛出异常
            config.setBlockWhenExhausted(true);
            // 逐出策略类名，当连接超过最大空闲时间或最大空闲数抛出异常
            config.setEvictionPolicyClassName("org.apache.commons.pool2." +
                    "impl.DefaultEvictionPolicy");
            // 是否启用pool的jmx管理功能，默认是true
            config.setJmxEnabled(true);
            // 最大空闲数，默认为8，一个pool最多有多少空闲的Jedis实例
            config.setMaxIdle(60);
            // 最大连接数
            config.setMaxTotal(100);
            // 当引入一个Jedis实例时，最大的等待时间，如果超过等待时间，抛出异常
            config.setMaxWaitMillis(1000 * 10);
            // 获得一个jedis实例的时候是否检查连接可用性（ping()）
            config.setTestOnBorrow(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取Jedis实例
    public  static Jedis getJedis() {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(addr, port);
        // 权限认证
        // jedis.auth(passwd);
        return jedis;
    }

    // 释放Jedis资源
    public static void close(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }





    /**
     * 阻塞式获取分布式锁
     *
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */


    /**
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     * @param timeout
     * @return
     */
    public static boolean getDistributedLockByBlocking(Jedis jedis, String lockKey, String requestId, int expireTime, long timeout) {

        long start = System.currentTimeMillis();

        try {
            while (System.currentTimeMillis() - start < timeout) {
                String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
                Thread.sleep(SLEEP_TIME);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean getDistributedLockByBlocking2(Jedis jedis, String lockKey, String requestId, int expireTime, long timeout) {

        long start = System.currentTimeMillis();
        boolean locked = false;

        try {
            while (true) {
                String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

                if (LOCK_SUCCESS.equals(result)) {
                    locked = true;
                    break;
                }
                Thread.sleep(SLEEP_TIME);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return locked;
    }


    /**
     * 尝试获取分布式锁
     *
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {

        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

    /**
     * 释放分布式锁
     *
     * @param jedis     Redis客户端
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

}
