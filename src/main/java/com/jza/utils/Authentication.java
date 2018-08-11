package com.jza.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

class Authentication extends Authenticator {
    String username=null;
    String password=null;

    public Authentication(){
    }
    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }
    protected PasswordAuthentication getPasswordAuthentication(){
        PasswordAuthentication pa = new PasswordAuthentication(username, password);
        return pa;
    }
}