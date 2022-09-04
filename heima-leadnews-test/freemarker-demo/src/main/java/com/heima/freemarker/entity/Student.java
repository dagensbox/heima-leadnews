package com.heima.freemarker.entity;


import lombok.Data;

import java.util.Date;

/**
 * @author 12141
 */
@Data
public class Student {
    private String name;
    private int age;
    private Date birthday;
    private Float money;
}
