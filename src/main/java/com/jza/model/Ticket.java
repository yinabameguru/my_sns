package com.jza.model;

import java.util.Date;

public class Ticket {
    private Integer id;
    private Integer user_id;
    private String ticket;
    private Date expired;
    private Integer status;
    private User user;

    public Ticket(){

    }
    public Ticket(Integer user_id, String ticket, Date expired, Integer status) {
        this.user_id = user_id;
        this.ticket = ticket;
        this.expired = expired;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ticket{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", ticket='" + ticket + '\'' +
                ", expired=" + expired +
                ", status=" + status +
                ", user=" + user +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
