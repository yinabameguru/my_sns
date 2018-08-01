package com.jza.dao;

import com.jza.model.Comment;
import org.apache.ibatis.annotations.*;

import java.awt.*;
import java.util.List;

@Mapper
public interface CommentDao {

    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " content,user_id,entity_id,entity_type,created_date,status ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;


    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values(#{content},#{userId},#{entityId},#{entityType},#{createdDate},#{status}) "})
    Integer insertComment(Comment comment);

    @Update({"update",TABLE_NAME,"set( status = #{status},) where id = #{id}"})
    Integer updateStatus(@Param("status") Integer status,@Param("id") Integer userId);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where entity_id = #{entity_id} and entity_type = #{entity_type} order by created_date desc"})
    @Results({
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "content",property = "content"),
            @Result(column = "user_id",property = "userId"),
            @Result(column = "entity_id",property = "entityId"),
            @Result(column = "entity_type",property = "entityType"),
            @Result(column = "created_date",property = "createdDate"),
            @Result(column = "status",property = "status")
    })
    List<Comment> selectComments(@Param("entity_id") Integer entityId, @Param("entity_type") Integer entityType);

    @Select({"select count(id) from",TABLE_NAME,"where entity_id = #{entity_id} and entity_type = #{entity_type}"})
    Integer selectCount(@Param("entity_id") Integer entityId,@Param("entity_type") Integer entityType);
}
