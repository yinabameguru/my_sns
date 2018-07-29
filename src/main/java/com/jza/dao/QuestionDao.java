package com.jza.dao;

import com.jza.model.Question;
import com.jza.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDao {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title,content,user_id,created_date,comment_count ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    List<Question> selectLatestQuestions(@Param("userId") int userId);

    @Insert("insert into " + TABLE_NAME + "(" + INSERT_FIELDS + ")values(#{title},#{content},#{userId},#{createdDate},#{commentCount}) ")
    int insertQuestion(Question question);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id = #{id}"})
    @Results({
            @Result(id = true,property = "id",column = "id"),
            @Result(property = "title",column = "title"),
            @Result(property = "content",column = "content"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdDate",column = "created_date"),
            @Result(property = "commentCount",column = "comment_count")
    })
    Question selectQuestionById(@Param("id") Integer id);
}
