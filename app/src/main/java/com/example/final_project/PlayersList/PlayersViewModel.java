package com.example.final_project.PlayersList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.Common.PreferenceManager;
import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.DataBase.RoomDB;

import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayersViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<PlayerData>> playersLiveData;
    private MutableLiveData<Integer> indexSelectedItem;
    private MutableLiveData<PlayerData> itemSelected;
    private MutableLiveData<HashMap<Integer,String>> iconsLivedata;
    private RoomDB rdb;
    private PreferenceManager pm;
    private  HashMap<Integer,String> icons;


    public static PlayersViewModel instance;
    public PlayersViewModel(@NonNull Application application, Context context, Activity activity) {
        super(application);
        playersLiveData=new MutableLiveData<>();
        indexSelectedItem=new MutableLiveData<>();
        itemSelected=new MutableLiveData<>();
        iconsLivedata=new MutableLiveData<>();
        rdb=RoomDB.getInstance(context);
        pm=PreferenceManager.getInstance(context);
        ArrayList<PlayerData> players= (ArrayList<PlayerData>) rdb.getItemDao().getAll();
        playersLiveData.setValue(players);
        icons=new HashMap<>();
        indexSelectedItem.setValue(RecyclerView.NO_POSITION);
        for(int i=0;i<players.size();i++){
            int pid=players.get(i).getPid();
                icons.put(pid,pm.getString(pid+""));
        }
        iconsLivedata.setValue(icons);
    }
    public static PlayersViewModel getInstance(Application application, Context context, Activity activity){
        if (instance==null){
            instance =new PlayersViewModel(application,context,activity);
        }
        return instance;
    }


    public MutableLiveData<ArrayList<PlayerData>> getPlayersLiveData() {
        playersLiveData.setValue((ArrayList<PlayerData>)rdb.getItemDao().getAll());
        return playersLiveData;
    }

    public void setPlayersLiveData(ArrayList<PlayerData> playersLiveData) {
        this.playersLiveData.setValue(playersLiveData);
    }

    public MutableLiveData<Integer> getIndexSelectedItem() {
        return indexSelectedItem;
    }

    public void setIndexSelectedItem(Integer indexSelectedItem) {
        this.indexSelectedItem.setValue(indexSelectedItem);
    }

    public MutableLiveData<PlayerData> getItemSelected() {
        return itemSelected;
    }

    public void setItemSelected(PlayerData itemSelected) {
        this.itemSelected.setValue(itemSelected);
    }

    public MutableLiveData<HashMap<Integer, String>> getIconsLivedata() {
        return iconsLivedata;
    }

    public void setIconsLivedata(HashMap<Integer, String> iconsLivedata) {
        this.iconsLivedata.setValue(iconsLivedata);
    }

    public void deletePlayer(PlayerData p){
        rdb.getItemDao().delete(p);
    }

    public void deleteIcon (String key){
        iconsLivedata.getValue().remove(key);
        pm.delObj(key);
    }

    public void updateIcons(String id){
        icons.put(Integer.parseInt(id),pm.getString(id+""));
    }

}
