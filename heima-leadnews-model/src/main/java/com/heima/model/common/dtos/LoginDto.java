package com.heima.model.common.dtos;

import lombok.Data;

/**
 * @author 12141
 */
@Data
public class LoginDto {
    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;
}
