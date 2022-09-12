package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApUserAuthDto;
import com.heima.user.service.ApUserRealNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 12141
 */
@RestController
@RequestMapping("/api/v1/auth")
public class ApUserAuthController {

    @Autowired
    private ApUserRealNameService apUserRealNameService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody ApUserAuthDto dto) {
        return apUserRealNameService.listByPage(dto);
    }


    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody ApUserAuthDto dto) {
        return apUserRealNameService.authFail(dto);
    }

    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody ApUserAuthDto dto) {
        return apUserRealNameService.authPass(dto);
    }

}
