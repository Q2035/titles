package com.example.title.service;

import com.example.title.exception.UsernameDuplicationException;
import com.example.title.pojo.User;

import java.util.List;

/**
 * @Create: 26/04/2020 11:43
 * @Author: Q
 */
public interface UserService {

    User getUserByUsernameAndPassword(String username, String password);

    void setUserIntoDB(User user) throws UsernameDuplicationException;

    List<User> getAllUsers();

    void deleteUserById(Integer id);

    User getUserById(Integer id);
}
