package com.abing.house.web.interceptor;

import com.abing.house.common.constants.CommonConstants;
import com.abing.house.common.model.User;
import com.google.common.base.Joiner;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    //在controller执行之前执行的
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String,String[]> map= request.getParameterMap();
        map.forEach((k,v)->{
            if (k.equals("errorMsg")||k.equals("successMsg")||k.equals("target")){
                request.setAttribute(k, Joiner.on(",").join(v));
            }
        });
        String reqUri=request.getRequestURI();
        if (reqUri.startsWith("/static")||reqUri.startsWith("/error")){
            return true;
        }
        HttpSession session=request.getSession(true);
       User user = (User) session.getAttribute(CommonConstants.USER_ATTRIBUTE);
        if (user!=null){
            UserContext.setUser(user);
        }
        return true;
    }

    //在controller执行完之后执行的
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    //在页面渲染完之后执行的
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.remove();
    }
}
