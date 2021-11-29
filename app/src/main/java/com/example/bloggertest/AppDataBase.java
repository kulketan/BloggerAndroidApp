package com.example.bloggertest;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ModelPost.class}, version =  1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract PostDAO postDAO();

    private static AppDataBase INSTANCE;

    public static AppDataBase getDbInstance(Context context){

        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class,"blogger_db")
                        .allowMainThreadQueries()
                        .build();
        }
        return INSTANCE;
    }
}
