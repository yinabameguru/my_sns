package com.jza.dao;

import com.jza.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {
    String TABLE_NAME = " user ";
    String SELECT_FIELDS = "name";
    @Select("select " + SELECT_FIELDS + " from" + TABLE_NAME + "where id = #{id}")
    User selectUser(int userId);
}
