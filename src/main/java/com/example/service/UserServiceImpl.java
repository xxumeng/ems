package com.example.service;

import com.example.dao.UserDAO;
import com.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.Objects;

@Service
@Transactional
public class UserServiceImpl implements UserService{
    @Autowired
    private UserDAO userDAO;

    @Override
    public void register(User user) {
        //1.生成用户状态
        user.setStatus("已激活");
        //2.注册时间
        user.setRegisterTime(new Date());
        //3.调用DAO
        userDAO.save(user);

    }

    @Override
    public User login(User user) {

        User userDB = userDAO.findByUserName(user.getUsername());
        if (!ObjectUtils.isEmpty(userDB)) {
            //比较密码
            if (userDB.getPassword().equals(user.getPassword())) {
                return userDB;
            } else {
                throw new RuntimeException("密码输入错误");
            }
        }else {
            throw new RuntimeException("用户名输入错误");
        }
    }
}
