package ${package}.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisDistributedLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ThreadLocal<LockContext> local = new ThreadLocal<>();

    /**
     * 加锁，不论成功与否，立刻返回
     *
     * @param key      锁标志
     * @param lockTime 加锁时间
     * @param lockUnit 加锁时间单位
     * @return true-加锁成功，false-加锁失败
     */
    public boolean tryLock(String key, long lockTime, TimeUnit lockUnit) {
        return doLock(key, lockTime, lockUnit);
    }

    /**
     * 加锁，支持超时等待
     *
     * @param key      锁标志
     * @param waitTime 超时时间
     * @param waitUnit 超时时间单位
     * @param lockTime 加锁时间
     * @param lockUnit 加锁时间单位
     * @return true-加锁成功，false-加锁失败
     */
    public boolean tryLock(String key, long waitTime, TimeUnit waitUnit, long lockTime, TimeUnit lockUnit) {
        boolean success = false;
        try {
            success = doLock(key, lockTime, lockUnit);
        } catch (IllegalStateException e) {
            // ignore
        }
        if (success) {
            return true;
        }

        try {
            TimeUnit.MILLISECONDS.sleep(waitUnit.toMillis(waitTime));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        return doLock(key, lockTime, lockUnit);
    }

    private boolean doLock(String key, long lockTime, TimeUnit timeUnit) {
        LockContext lockContext = local.get();
        if (lockContext != null) {
            if (!Objects.equals(key, lockContext.lockKey)
                    && System.currentTimeMillis() <= lockContext.expireTimeInMillis) {
                // 避免锁覆盖，导致前面的锁无法被及时释放
                throw new IllegalStateException(String.format("锁%s未失效，不允许覆盖锁", lockContext.lockKey));
            } else {
                log.warn(String.format("锁%s已失效但未释放，即将被新锁%s覆盖", lockContext.lockKey, key));
            }
        }

        try {
            String lockValue = UUID.randomUUID().toString();
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, lockValue, lockTime, timeUnit);
            if (Boolean.TRUE.equals(result)) {
                local.set(new LockContext(key, lockValue,
                        System.currentTimeMillis() + timeUnit.toMillis(lockTime)));
                return true;
            }
        } catch (Exception e) {
            log.error("redis分布式锁-设置锁error", e);
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param key 锁标志
     * @return true-释放锁成功，false-释放锁失败
     */
    public boolean releaseLock(String key) {
        LockContext lockContext = local.get();
        if (lockContext == null) {
            return false;
        }
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (Objects.equals(value, lockContext.lockValue)) {
                Boolean result = redisTemplate.opsForValue().getOperations().delete(key);
                if (Boolean.TRUE.equals(result)) {
                    local.remove();
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("redis分布式锁-释放锁error", e);
        }
        return false;
    }

    private class LockContext {
        String lockKey;
        String lockValue;
        long expireTimeInMillis;

        public LockContext(String lockKey, String lockValue, long expireTimeInMillis) {
            this.lockKey = lockKey;
            this.lockValue = lockValue;
            this.expireTimeInMillis = expireTimeInMillis;
        }
    }
}