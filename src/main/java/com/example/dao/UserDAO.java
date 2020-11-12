package com.example.dao;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper //用来创建DAO对象
public interface UserDAO {
    void save(User user);
    User findByUserName(String username);
}
