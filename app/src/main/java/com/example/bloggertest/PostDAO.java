package com.example.bloggertest;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PostDAO {

    @Query("SELECT * FROM ModelPost LIMIT 10")
    List<ModelPost> getPosts();

    @Insert
    void insertPosts(ArrayList<ModelPost> modelPosts);

    @Query("DELETE FROM ModelPost")
    void deleteAll();

}
