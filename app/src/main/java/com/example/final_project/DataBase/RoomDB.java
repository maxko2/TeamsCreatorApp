package com.example.final_project.DataBase;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.List;

@Database(entities = {PlayerData.class},version = 1,exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    public abstract DataDao getItemDao();
    private static RoomDB INSTANCE;
    public static RoomDB getInstance(Context context){
        if(INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),RoomDB.class,"players-database")
                    .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
    public List<PlayerData> getAllItems(){
        return getItemDao().getAll();
    }

}
