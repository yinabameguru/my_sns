package com.jza.dao;

import com.jza.model.Ticket;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface TicketDao {

    String INSERT_FIELDS  = " user_id,ticket,expired ";
    String TABLE_NAME = " login_ticket ";
    String SELECT_FIELDS = " id,status, " + INSERT_FIELDS;

    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values(#{userId},#{ticket},#{expired})"})
    Integer addTicket(@Param("userId") Integer userId,@Param("ticket") String ticket,@Param("expired") Date expired);

    @Update({"update",TABLE_NAME,"set status = 1 where ticket = #{ticket}"})
    Integer updateStatus(@Param("ticket") String ticket);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where ticket = #{ticket}"})
    @Results({
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "user_id",property = "userId"),
            @Result(column = "ticket",property = "ticket"),
            @Result(column = "expired",property = "expired"),
            @Result(column = "status",property = "status")
    })
    Ticket selectTicketByTicket(@Param("ticket") String ticket);
}
