package com.heima.model.admin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 12141
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUserDto {
    private String name;
    private String password;
}
