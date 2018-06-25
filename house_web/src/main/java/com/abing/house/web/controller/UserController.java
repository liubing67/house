package com.abing.house.web.controller;

import com.abing.house.biz.service.AgencyService;
import com.abing.house.biz.service.UserService;
import com.abing.house.common.model.User;
import com.abing.house.common.result.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AgencyService agencyService;
    /**注册提交：1注册验证，2发送邮件 3 验证失败重定向要注册页面
     * @param user
     * @param modelMap
     * @return
     */
    @RequestMapping("accounts/register")
    public String accountRegister(User user, ModelMap modelMap){
        if (user==null||user.getName()==null){
            modelMap.put("agencyList",  agencyService.getAllAgency());
            return "/user/accounts/register";
        }
        //用户验证
        ResultMsg resultMsg=UserHelper.validate(user);
        if (resultMsg.isSuccess()&&userService.addAccount(user)){
            modelMap.put("email", user.getEmail());
            return "/user/accounts/registerSubmit";
        }else {
            return "redirect:/accounts/register?"+resultMsg.asUrlParams();
        }
    }

    /**
     * 邮箱验证
     * @param key
     * @return
     */
    @RequestMapping("accounts/verify")
    public String verify(String key){
        boolean result=userService.enable(key);
        if (result){
            return "redirect:/index?"+ResultMsg.successMsg("激活成功").asUrlParams();
        }else {
            return "redirect:/accounts/register?"+ResultMsg.errrorMsg("激活失败，请确认连接是否过期！");
        }

    }

    public String signIn(HttpServletRequest request){

        String userName=request.getParameter("userName");
        String passWord=request.getParameter("password");
        String target=request.getParameter("target");
        if (userName==null||passWord==null){
            request.setAttribute("target",target);
            return "/user/accounts/signin";
        }
        User user=userService.auth(userName,passWord);
        if (user==null){
            return "redirect:/account/signin?"+"target="+target+"&username="+
                    userName+"&"+ResultMsg.errrorMsg("用户名或密码错误").asUrlParams();
        }
        return "";
    }



}
