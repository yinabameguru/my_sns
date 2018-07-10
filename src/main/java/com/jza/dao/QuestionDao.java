package com.jza.dao;

import com.jza.model.Question;
import com.jza.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionDao {
    List<Question> selectLatestQuestions(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);
    @Select("select * from user")
    List<User> fu();
}
