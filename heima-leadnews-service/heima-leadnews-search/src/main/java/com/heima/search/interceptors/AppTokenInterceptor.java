package com.heima.search.interceptors;

import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import com.heima.utils.thread.WmThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author 12141
 */
@Slf4j
public class AppTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //得到header中的信息
        String userId = request.getHeader("userId");
        Optional<String> optional = Optional.ofNullable(userId);
        if (optional.isPresent()){
            //将用户id存入threadLocal
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            AppThreadLocalUtil.setUser(apUser);
            log.info("AppTokenInterceptor设置用户信息到threadLocal中...");
        }

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocalUtil.clear();
        log.info("AppTokenInterceptor清除threadLocal中的用户信息...");
    }
}
