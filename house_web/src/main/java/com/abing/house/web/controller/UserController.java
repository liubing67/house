package com.abing.house.web.controller;

import com.abing.house.biz.service.AgencyService;
import com.abing.house.biz.service.UserService;
import com.abing.house.common.constants.CommonConstants;
import com.abing.house.common.model.User;
import com.abing.house.common.result.ResultMsg;
import com.abing.house.common.utils.HashUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
     * 注册，邮箱验证
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

    /**
     * 登录接口
     * @param request
     * @return
     */
    @RequestMapping("/accounts/signin")
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
        }else {
            HttpSession httpSession=request.getSession(true);
            httpSession.setAttribute(CommonConstants.USER_ATTRIBUTE,user);
            return StringUtils.isNoneBlank(target)?"redirect:"+target:"redirect:/index";
        }
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @RequestMapping("accounts/logout")
    public String logout(HttpServletRequest request){
        HttpSession session=request.getSession(true);
        session.invalidate();
        return "redirect:/index;";
    }

    /**
     * 修改密码操作
     * @param email
     * @param password
     * @param newPassword
     * @return
     */
    @RequestMapping("accounts/changePassword")
    public String changePassword(String email,String password,String newPassword,String confirmPassword,ModelMap modelMap){
        User user=userService.auth(email,password);
        if (user==null||!confirmPassword.equals(newPassword)){
            return "redirct:/accounts/profile?"+ResultMsg.errrorMsg("密码不一致").asUrlParams();
        }
        User updateUser=new User();
        updateUser.setPasswd(HashUtils.encryPassword(newPassword));
        userService.updateUser(updateUser,email);
        return "redirect:/accounts/profile?"+ResultMsg.successMsg("更新成功").asUrlParams();
    }

    /**
     * 忘记密码,填写邮箱，发送验证
     * @param email
     * @param modelMap
     * @return
     */
    @RequestMapping("accounts/remember")
    public String remember(String email,ModelMap modelMap){
        if (StringUtils.isBlank(email)){
            return "redirect:/accounts/signin?"+ResultMsg.errrorMsg("邮箱不能为空").asUrlParams();
        }
        userService.resetNotify(email);
        modelMap.put("email",email);
        return "/user/accounts/remember";
    }

    /**
     * 重置密码，邮箱验证
     * @param key
     * @param modelMap
     * @return
     */
    @RequestMapping("accounts/reset")
    public String reset(String key,ModelMap modelMap){
        String email=userService.getResetEmail(key);
        if (StringUtils.isBlank(email)){
            return "redirect:/accounts/signin?"+ResultMsg.errrorMsg("重置连接已过期").asUrlParams();
        }
        modelMap.put("email",email);
        modelMap.put("success_key",key);
        return "/user/accounts/reset";
    }

    /**
     * 重置密码，提交操作
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(value = "accounts/resetSubmit")
    public String resetSubmit(HttpServletRequest request,User user){
        ResultMsg resultMsg=UserHelper.validateResetPassword(user.getKey(),user.getPasswd(),user.getConfirmPasswd());
        if (!resultMsg.isSuccess()){
            String suffix="";
            if (StringUtils.isNoneBlank(user.getKey())){
                suffix="email="+userService.getResetEmail(user.getKey())+"&key="+user.getKey()+"&";
            }
            return "redirect:/accounts/reset?"+suffix+resultMsg.asUrlParams();

        }
        User updatedUser=userService.reset(user.getKey(),user.getPasswd());
        request.getSession(true).setAttribute(CommonConstants.USER_ATTRIBUTE,updatedUser);
        return "redirect:/index?"+resultMsg.asUrlParams();
    }

}
