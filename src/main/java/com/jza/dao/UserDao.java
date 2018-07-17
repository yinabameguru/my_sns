package com.jza.dao;

import com.jza.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {
    String TABLE_NAME = " user ";
    String SELECT_FIELDS = " id,name,password,salt,head_url ";
    String INSERT_FIELDS = " name,password,salt,head_url ";

    @Select({"select ",SELECT_FIELDS," from",TABLE_NAME,"where id = #{id}"})
    @Results({
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "name",property = "name"),
            @Result(column = "salt",property = "salt"),
            @Result(column = "head_url",property = "headUrl")
    })
    User selectUserById(int userId);

    @Select({"select ",SELECT_FIELDS," from",TABLE_NAME,"where name = #{name}"})
    @Results({
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "name",property = "name"),
            @Result(column = "salt",property = "salt"),
            @Result(column = "head_url",property = "headUrl")
    })
    User selectUserByName(User user);

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,") values(#{name},#{password},#{salt},#{headUrl})"})
    Integer insertUser(User user);
}
