package com.abing.house.web.interceptor;

import com.abing.house.common.model.User;

public class UserContext {
    private static final ThreadLocal<User> USER_THREAD_LOCAL=new ThreadLocal<>();
    public static void setUser(User user){
        USER_THREAD_LOCAL.set(user);
    }
    public static void remove(){
        USER_THREAD_LOCAL.remove();
    }
    public static User getUser(){
        return USER_THREAD_LOCAL.get();
    }
}
