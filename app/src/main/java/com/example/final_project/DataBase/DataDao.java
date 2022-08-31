package com.example.final_project.DataBase;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

@Dao
public interface DataDao {
    @Query("SELECT * FROM Player_Table")
    List<PlayerData> getAll();

    @Query("SELECT * FROM Player_Table WHERE foot_rate!=0")
    List<PlayerData> getFootballPlayers();

    @Query("SELECT * FROM Player_Table WHERE basket_rate!=0")
    List<PlayerData> getBasketBallPlayers();

    @Insert
    long insert(PlayerData playerData);

    @Delete
    void delete(PlayerData playerData);

    @Query("DELETE  FROM Player_Table")
    void deleteAll();
}



