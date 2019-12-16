package com.foxiaotao.springbootredis.myredis.listopt.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderModel implements Serializable {
    private int age;
    private String name;
    private String income;
}
