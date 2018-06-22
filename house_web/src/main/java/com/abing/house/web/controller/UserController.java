package com.abing.house.web.controller;

import com.abing.house.biz.service.UserService;
import com.abing.house.common.model.User;
import com.abing.house.common.result.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**注册提交：1注册验证，2发送邮件 3 验证失败重定向要注册页面
     * @param user
     * @param modelMap
     * @return
     */
    @RequestMapping("accounts/register")
    public String accountRegister(User user, ModelMap modelMap){
        if (user==null||user.getName()==null){
            return "/user/accounts/register";
        }
        //用户验证
        ResultMsg resultMsg=UserHelper.validate(user);
        if (resultMsg.isSuccess()&&userService.addAccount(user)){
            return "/user/accounts/registerSubmit";
        }else {
            return "redirect:/accounts/register?"+resultMsg.asUrlParams();
        }
    }



}
