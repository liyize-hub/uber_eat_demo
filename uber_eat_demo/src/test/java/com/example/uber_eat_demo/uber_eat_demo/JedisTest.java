package com.example.uber_eat_demo.uber_eat_demo;

import java.util.Set;

import org.junit.jupiter.api.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * 使用Jedis操作Redis
 */
public class JedisTest {
    @Test
    public void testRedis() {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("127.0.0.1", 6379);
        // 设置密码
        // jedisShardInfo.setPassword("123456");

        // 1 获取连接
        Jedis jedis = new Jedis(jedisShardInfo);

        // 2 执行具体的操作
        jedis.set("school", "itcast");
        // 从缓存中获得数据
        String value = jedis.get("school");
        System.out.println(value);

        // hash
        jedis.hset("myhash", "addr", "Beijing");
        String hVaule = jedis.hget("myhash", "addr");
        System.out.println(hVaule);

        // 显示所有keys
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }

        jedis.del("school");
        jedis.hdel("myhash", "addr");
        // 3 关闭连接
        jedis.close();
    }
}
