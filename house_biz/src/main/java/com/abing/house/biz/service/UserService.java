package com.abing.house.biz.service;

import com.abing.house.biz.mapper.UserMapper;
import com.abing.house.common.model.User;
import com.abing.house.common.utils.BeanHelper;
import com.abing.house.common.utils.HashUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private FileService fileService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserMapper userMapper;

    @Value("${file.prefix}")
    private String imgPrefix;

    /**
     * 1.插入数据库，非激活;密码加盐md5;保存头像文件到本地 2.生成key，绑定email 3.发送邮件给用户
     *
     * @param user
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addAccount(User user) {
        user.setPasswd(HashUtils.encryPassword(user.getPasswd()));
        List<String> imgList=fileService.getImgPaths(Lists.newArrayList(user.getAvatarFile()));
        if (!imgList.isEmpty()){
            user.setAvatar(imgList.get(0));
        }
        BeanHelper.setDefaultProp(user,User.class);
        BeanHelper.onInsert(user);
        user.setEnable(0);
        userMapper.insert(user);
        mailService.registerNotify(user.getEmail());
        return true;
    }
    public boolean enable(String key){
        return mailService.enable(key);
    }

    /**
     * 用户名密码验证
     * @param userName
     * @param passWord
     * @return
     */
    public User auth(String userName,String passWord){
        User user=new User();
        user.setEmail(userName);
        user.setPasswd(HashUtils.encryPassword(passWord));
        user.setEnable(1);
        List<User> list=getUserByQuery(user);
        if (!list.isEmpty()){
            return list.get(0);
        }
        return null;

    }
    public List<User> getUserByQuery(User user){
        List<User> list=userMapper.selectUsersByQuery(user);
        list.forEach(u -> {
            u.setAvatar(imgPrefix+u.getAvatar());
        });
        return list;
    }
    public void updateUser(User updateUser,String email){
        updateUser.setEmail(email);
        BeanHelper.onUpdate(updateUser);
        userMapper.update(updateUser);
    }






}
