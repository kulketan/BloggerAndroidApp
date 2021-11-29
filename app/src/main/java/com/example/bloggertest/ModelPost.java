package com.example.bloggertest;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ModelPost {

    //variables
    @ColumnInfo(name = "authorName")
    String authorName;
    @ColumnInfo(name = "content")
    String content;
    @PrimaryKey(autoGenerate = false)
    @NonNull String id;
    @ColumnInfo(name = "published")
    String published;
    @ColumnInfo(name = "selfLink")
    String selfLink;
    @ColumnInfo(name = "title")
    String title;
    @ColumnInfo(name = "updated")
    String updated;
    @ColumnInfo(name = "url")
    String url;

    //constructor

    public ModelPost(String authorName, String content, String id, String published, String selfLink, String title, String updated, String url) {
        this.authorName = authorName;
        this.content = content;
        this.id = id;
        this.published = published;
        this.selfLink = selfLink;
        this.title = title;
        this.updated = updated;
        this.url = url;
    }

    //getter and setters


    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
