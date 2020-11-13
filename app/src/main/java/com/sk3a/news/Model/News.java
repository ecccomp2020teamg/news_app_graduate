package com.sk3a.news.Model;

public class News {

    int id;

    String title , desc , url , date , category, isFeatured;

    public News() {
    }

    public News(int id, String title, String desc, String url, String date, String category, String isFeatured) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.date = date;
        this.category = category;
        this.isFeatured = isFeatured;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(String isFeatured) {
        this.isFeatured = isFeatured;
    }
}
