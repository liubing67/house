package com.abing.house.biz.service;

import com.abing.house.biz.mapper.UserMapper;
import com.abing.house.common.model.User;
import com.abing.house.common.utils.BeanHelper;
import com.abing.house.common.utils.HashUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
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
}
