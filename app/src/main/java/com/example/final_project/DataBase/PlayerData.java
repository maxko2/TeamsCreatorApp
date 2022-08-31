package com.example.final_project.DataBase;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Player_Table")
public class PlayerData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int pid;

    @ColumnInfo(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "age")
    private int age;


    @ColumnInfo(name = "basket_rate")
    private int basketRate;

        @ColumnInfo(name = "basket_height")
        private String basketHeight;

    @ColumnInfo(name = "foot_rate")
    private int footRate;

    @ColumnInfo(name = "pref_foot")
    private String foot;

    public String getFoot() {
        return foot;
    }

    public void setFoot(String foot) {
        this.foot = foot;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {return age;}

    public void setAge(int age) {this.age = age;}

    public void setBasketRate(int basketRate) {
        this.basketRate = basketRate;
    }

    public void setBasketHeight(String basketHeight) {
        this.basketHeight = basketHeight;
    }

    public void setFootRate(int footRate) {
        this.footRate = footRate;
    }

    public String getFullName() {
        return fullName;
    }

    public int getBasketRate() {
        return basketRate;
    }

    public String getBasketHeight() {
        return basketHeight;
    }

    public int getFootRate() {
        return footRate;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "pid=" + pid +
                ", fullName='" + fullName + '\'' +
                ", age=" + age +
                ", basketRate=" + basketRate +
                ", basketHeight='" + basketHeight + '\'' +
                ", footRate=" + footRate +
                ", foot='" + foot + '\'' +
                '}';
    }
}
