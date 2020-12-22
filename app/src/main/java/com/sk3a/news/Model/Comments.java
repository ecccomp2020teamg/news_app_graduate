package com.sk3a.news.Model;

public class Comments {

    private String user_date,user_email,user_name,user_comment;

    public Comments(){

    }

    public Comments(String user_date,String user_email,String user_name,String user_comment) {
        this.user_date = user_date;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_comment= user_comment;

    }


    public String getUser_date() {
        return user_date;
    }

    public void setUser_date(String user_date) {
        this.user_date = user_date;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getUser_comment() {
        return user_comment;
    }

    public void setUser_comment(String user_comment) {
        this.user_comment = user_comment;
    }

}