package com.heima.freemarker.entity;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author 12141
 */
@Data
public class Student {
    private String name;
    private int age;
    private LocalDate birthday;
    private Float money;
}
