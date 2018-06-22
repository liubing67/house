package com.abing.house.web.controller;

import com.abing.house.common.model.User;
import com.abing.house.common.result.ResultMsg;
import org.apache.commons.lang3.StringUtils;

public class UserHelper {

    public static ResultMsg validate(User user){
        if (StringUtils.isBlank(user.getEmail())){
            return ResultMsg.errrorMsg("Email 有误");
        }
        if (StringUtils.isBlank(user.getName())){
            return ResultMsg.errrorMsg("名词有误");
        }
        if (StringUtils.isBlank(user.getConfirmPasswd())||StringUtils.isBlank(user.getPasswd())||!user.getPasswd().equals(user.getConfirmPasswd())){
            return ResultMsg.errrorMsg("密码有误");
        }
        if (user.getPasswd().length()<6){
            return ResultMsg.errrorMsg("密码大于6位");
        }
        return ResultMsg.successMsg("");
    }
}
