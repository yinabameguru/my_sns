package com.jza.dao;

import com.jza.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {
    String TABLE_NAME = " user ";
    String SELECT_FIELDS = " id,name,password,salt,head_url,activation_code,activation_status ";
    String INSERT_FIELDS = " name,password,salt,head_url,activation_code,activation_status ";

    @Select({"select ",SELECT_FIELDS," from",TABLE_NAME,"where id = #{id}"})
    User selectUserById(int userId);

    @Select({"select ",SELECT_FIELDS," from",TABLE_NAME,"where name = #{name}"})
    User selectUserByName(User user);

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,") values(#{name},#{password},#{salt},#{headUrl},#{activationCode},#{activationStatus})"})
    Integer insertUser(User user);
}
