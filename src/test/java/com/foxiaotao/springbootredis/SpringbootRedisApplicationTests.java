package com.foxiaotao.springbootredis;

import com.foxiaotao.springbootredis.myredis.listopt.model.OrderModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringbootRedisApplicationTests {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, OrderModel> redisTemplate;

    private final static String listKey = "LIST.KEY";

    @Test
    void addList2Redis() {
        List<OrderModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            OrderModel orderModel = new OrderModel();
            orderModel.setAge(i);
            orderModel.setName("suntao"+i);
            orderModel.setIncome(10000 + i + "");
            list.add(orderModel);
        }
        redisTemplate.opsForList().leftPushAll(listKey, list);
    }


    @Test
    void getList2Redis() {

        ListOperations<String, OrderModel> operations = redisTemplate.opsForList();

        OrderModel orderModel = operations.rightPop(listKey);
        while (orderModel != null) {
            System.out.println(orderModel);
            orderModel = operations.rightPop(listKey);
        }

    }




}
