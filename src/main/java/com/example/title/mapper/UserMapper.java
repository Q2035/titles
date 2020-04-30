package com.example.title.mapper;

import com.example.title.pojo.TitleDoneByUser;
import com.example.title.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Create: 24/04/2020 18:45
 * @Author: Q
 */
public interface UserMapper {

    @Select("select * from user where username=#{username} and password=#{password}")
    User getUserByUsernameAndPassword(String username, String password);

    @Insert("insert into user values(default,#{username},#{password},#{email},#{roles})")
    void setUserIntoDB(User user);

    @Select("select * from user where username=#{username}")
    User getUserByUsername(String username);

    @Select("select * from user")
    List<User> getAllUsers();

    @Delete("delete from user where id = #{id}")
    void deleteUserById(Integer id);

    @Select("select * from user where id = #{id}")
    User getUserById(Integer id);

//    -----------处理user_done表-------------

    @Insert("insert into user_done values(default,#{type},#{titleID},${isRight},#{date},#{userID})")
    void setUserDone(TitleDoneByUser titleDoneByUser);

    @Select("select * from user_done where user_id = #{userID} order by date limit 50")
    List<TitleDoneByUser> getUserDone(Integer userID);
}
