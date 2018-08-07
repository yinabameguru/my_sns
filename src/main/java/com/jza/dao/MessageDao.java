package com.jza.dao;

import com.jza.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDao {
    String INSERT_FIELDS = " from_id,to_id,content,created_date,has_read,conversation_id ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;
    String TABLE_NAME = " message ";
//    String SELECT_FIELDS2 = " id,from_id,to_id,content,created_date,conversation_id ";

            @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    Integer insertMessage(Message message);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where conversation_id = #{conversation_id}"})
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "from_id", property = "fromUser", one = @One(select = "com.jza.dao.UserDao.selectUserById")),
            @Result(column = "to_id", property = "toId"),
            @Result(column = "content", property = "content"),
            @Result(column = "created_date", property = "createdDate"),
            @Result(column = "has_read", property = "hasRead"),
            @Result(column = "conversation_id", property = "conversationId")
    })
    List<Message> selectMessage(@Param("conversation_id") String conversationId);

    @Select({
            "SELECT ",SELECT_FIELDS,",COUNT(conversation_id),COUNT(CASE WHEN has_read=0 THEN 1 ELSE NULL END) AS no_read_num FROM(",
                    "SELECT ",SELECT_FIELDS," FROM ",TABLE_NAME," WHERE from_id = #{userId} OR to_id = #{userId} ORDER BY created_date DESC",
            ")AS j GROUP BY conversation_id ORDER BY id DESC"
    })
    @Results({
            @Result(column = "count(conversation_id)", property = "id"),
            @Result(column = "from_id", property = "fromUser", one = @One(select = "com.jza.dao.UserDao.selectUserById")),
            @Result(column = "to_id", property = "toId"),
            @Result(column = "content", property = "content"),
            @Result(column = "created_date", property = "createdDate"),
            @Result(column = "no_read_num", property = "hasRead"),
            @Result(column = "conversation_id", property = "conversationId")
    })
    List<Message> selectMessageList(@Param("userId") Integer userId);


    @Update({"update",TABLE_NAME,"set has_read = #{newHasRead} where conversation_id = #{conversation_id}"})
    Integer updateHasRead(@Param("conversation_id") String conversationId,@Param("newHasRead") Integer newHasRead);
}
