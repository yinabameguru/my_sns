package com.jza.dao;

import com.jza.model.Ticket;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface TicketDao {

    String INSERT_FIELDS  = " user_id,ticket,expired,status ";
    String TABLE_NAME = " login_ticket ";
    String SELECT_FIELDS = " id " + INSERT_FIELDS;

    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values(#{userId},#{ticket},#{expired})"})
    Integer addTicket(@Param("userId") Integer userId,@Param("ticket") String ticket,@Param("expired") Date expired);

    @Update({"update",TABLE_NAME,"set status = 1 where user_id = #{userId}"})
    Integer updateStatus(@Param("userId") Integer userId);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where user_id = #{userId}"})
    @Results({
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "user_id",property = "userId"),
            @Result(column = "ticket",property = "ticket"),
            @Result(column = "expired",property = "expired"),
            @Result(column = "status",property = "status")
    })
    Ticket selectTicketByTicket(@Param("ticket") String ticket);
}
