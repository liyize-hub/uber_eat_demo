package com.example.uber_eat_demo.uber_eat_demo;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import com.kidd.uber_eat_demo.UberEatDemoApplication;

@SpringBootTest(classes = UberEatDemoApplication.class)
// @RunWith(SpringRunner.class)
public class SpringDataRedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 操作String类型数据
     */
    @Test
    public void testString() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // 存值
        valueOperations.set("city123", "beijing");

        // 取值
        String value = (String) valueOperations.get("city123");
        System.out.println(value);

        // 存值，同时设置过期时间
        valueOperations.set("key1", "value1", 10l, TimeUnit.SECONDS);

        // 存值，如果存在则不执行任何操作
        Boolean aBoolean = valueOperations.setIfAbsent("city123", "nanjing");

        System.out.println(aBoolean);
    }

    /**
     * 操作List类型的数据
     */
    @Test
    public void testList() {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        // 存值
        listOperations.leftPush("mylist", "a");
        listOperations.leftPushAll("mylist", "b", "c", "d");

        // 取值
        List<String> mylist = listOperations.range("mylist", 0, -1);
        for (String value : mylist) {
            System.out.println(value);
        }

        // 获得列表长度 llen
        Long size = listOperations.size("mylist");
        int lSize = size.intValue();
        for (int i = 0; i < lSize; i++) {
            // 出队列
            String element = (String) listOperations.rightPop("mylist");
            System.out.println(element);
        }
    }

    /**
     * 操作Set类型的数据
     */
    @Test
    public void testSet() {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();

        // 存值
        setOperations.add("myset", "a", "b", "c", "a");

        // 取值
        Set<String> myset = setOperations.members("myset");
        for (String o : myset) {
            System.out.println(o);
        }

        // 删除成员
        setOperations.remove("myset", "a", "b");

        // 取值
        myset = setOperations.members("myset");
        for (String o : myset) {
            System.out.println(o);
        }
    }

    /**
     * 操作ZSet类型的数据
     */
    @Test
    public void testZset() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        // 存值
        zSetOperations.add("myZset", "a", 10.0);
        zSetOperations.add("myZset", "b", 11.0);
        zSetOperations.add("myZset", "c", 12.0);
        zSetOperations.add("myZset", "a", 13.0);

        // 取值
        Set<String> myZset = zSetOperations.range("myZset", 0, -1);
        for (String s : myZset) {
            System.out.println(s);
        }

        // 修改分数
        zSetOperations.incrementScore("myZset", "b", 20.0);

        // 取值
        myZset = zSetOperations.range("myZset", 0, -1);
        for (String s : myZset) {
            System.out.println(s);
        }

        // 删除成员
        zSetOperations.remove("myZset", "a", "b");

        // 取值
        myZset = zSetOperations.range("myZset", 0, -1);
        for (String s : myZset) {
            System.out.println(s);
        }
    }

    /**
     * 通用操作，针对不同的数据类型都可以操作
     */
    @Test
    public void testCommon() {
        // 获取Redis中所有的key
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }

        // 判断某个key是否存在
        Boolean itcast = redisTemplate.hasKey("itcast");
        System.out.println(itcast);

        // 删除指定key
        redisTemplate.delete("myZset");

        // 获取指定key对应的value的数据类型
        DataType dataType = redisTemplate.type("myset");
        System.out.println(dataType.name());

    }

}