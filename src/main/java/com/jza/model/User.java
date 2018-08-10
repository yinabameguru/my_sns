package com.jza.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class User {
    private int id;
    @Email(message = "邮箱错误")
    private String name;
    @Size(min = 6,max = 30,message = "请输入6-30位密码")
    private String password;
    private String salt;
    private String headUrl;
    private String activationCode;
    private int activationStatus;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", activationCode='" + activationCode + '\'' +
                ", activationStatus=" + activationStatus +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public int getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(int activationStatus) {
        this.activationStatus = activationStatus;
    }

    public User(){}

    public User(String name) {
        this.name = name;
    }

    public User(User user) {
        this.name = user.getName();
        this.password = user.getPassword();
    }
}
