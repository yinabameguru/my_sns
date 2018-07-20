package com.jza.dao;

import com.jza.model.Question;
import com.jza.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionDao {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title,content,user_id,created_date,comment_count ";

    List<Question> selectLatestQuestions(@Param("userId") int userId);

    @Insert("insert into " + TABLE_NAME + "(" + INSERT_FIELDS + ")values(#{title},#{content},#{userId},#{createdDate},#{commentCount}) ")
    int insertQuestion(Question question);

}
