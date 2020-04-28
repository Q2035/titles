package com.example.title.service.impl;

import com.example.title.exception.UsernameDuplicationException;
import com.example.title.mapper.UserMapper;
import com.example.title.pojo.TitleDoneByUser;
import com.example.title.pojo.User;
import com.example.title.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Create: 26/04/2020 11:44
 * @Author: Q
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUsernameAndPassword(String username, String password) {
        User user = userMapper.getUserByUsernameAndPassword(username, password);
        if (user!=null)
            user.setPassword("");
        return user;
    }

    /**
     * 保证username的唯一性,有重复的username则抛出异常
     * @param user
     * @throws UsernameDuplicationException
     */
    @Override
    public void setUserIntoDB(User user) throws UsernameDuplicationException {
        User tempUser = userMapper.getUserByUsername(user.getUsername());
        if (tempUser != null){
            throw new UsernameDuplicationException();
        }
        userMapper.setUserIntoDB(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    public void deleteUserById(Integer id) {
        userMapper.deleteUserById(id);
    }

    @Override
    public User getUserById(Integer id){
        return userMapper.getUserById(id);
    }

    @Override
    public void setUserDone(TitleDoneByUser titleDoneByUser) {
        userMapper.setUserDone(titleDoneByUser);
    }
}
