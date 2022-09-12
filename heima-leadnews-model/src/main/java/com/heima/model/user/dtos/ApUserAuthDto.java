package com.heima.model.user.dtos;


import lombok.Data;

@Data
public class ApUserAuthDto {
    private Long id;
    private String msg;
    private Integer page;
    private Integer size;
    private Integer status;
}
