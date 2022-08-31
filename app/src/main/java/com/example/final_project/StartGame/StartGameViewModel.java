package com.example.final_project.StartGame;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.DataBase.RoomDB;

import java.util.ArrayList;


public class StartGameViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<PlayerData>> PlayerLiveData;
    private MutableLiveData<Integer> indexSelectedPlayer;
    private MutableLiveData<PlayerData> selectedPlayer;
    private RoomDB db;

    public MutableLiveData<ArrayList<PlayerData>> getPlayerLiveData() {
        return PlayerLiveData;
    }

    public void setPlayerLiveData(ArrayList<PlayerData> playerLiveData) {
        PlayerLiveData.setValue(playerLiveData);
    }
    public void setItemSelect(PlayerData pd){
        selectedPlayer.setValue(pd);
    }
    public MutableLiveData<Integer> getIndexSelectedPlayer() {
        return indexSelectedPlayer;
    }

    public void setIndexSelectedPlayer(MutableLiveData<Integer> indexSelectedPlayer) {
        this.indexSelectedPlayer = indexSelectedPlayer;
    }
    public void setPositionSelected(Integer index){
        indexSelectedPlayer.setValue(index);
    }
    public MutableLiveData<PlayerData> getSelectedPlayer() {
        return selectedPlayer;
    }

    public void setSelectedPlayer(MutableLiveData<PlayerData> selectedPlayer) {
        this.selectedPlayer = selectedPlayer;
    }

    private static StartGameViewModel instance;
    public Context context;
    public Activity activity;

    public StartGameViewModel(@NonNull Application application, Context context, Activity activity) {
        super(application);

        this.activity = activity;
        this.context = context;
        db = RoomDB.getInstance(context);
        PlayerLiveData = new MutableLiveData<>();
        selectedPlayer = new MutableLiveData<>();
        indexSelectedPlayer = new MutableLiveData<>();
        indexSelectedPlayer.setValue(-1);
        //set the default data to football
        PlayerLiveData.setValue((ArrayList<PlayerData>) db.getItemDao().getFootballPlayers());
    }


    public static StartGameViewModel getInstance(Application application, Context context, Activity activity) {
        if (instance == null) {
            instance = new StartGameViewModel(application, context, activity);
        }

        return instance;
    }


}
