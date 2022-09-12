package com.heima.model.wemedia.dtos;

import lombok.Data;

/**
 * @author 12141
 */
@Data
public class NewsAuthDto {
    private Long id;
    private String msg;
    private String title;
    private Integer status;
    private Integer page;
    private Integer size;
}
