package com.example.final_project.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.final_project.MainFrag;
import com.example.final_project.R;

public class BatteryChangeReceiver extends BroadcastReceiver {
    ConstraintLayout cl;
    DarkModeSharedPreference sh;
    @Override
    public void onReceive(Context context, Intent intent) {
        sh=DarkModeSharedPreference.getInstance(context);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;
        sh.setString("batteryp",batteryPct+"");
        if(batteryPct<=15) {
            MainFrag.setSwitch(false);
            Toast.makeText(context, "Battery's dying,\nDark mode enabled", Toast.LENGTH_LONG).show();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            cl.setBackground(null);
        }
        else {
            if (!sh.getString("darkmode").equals("on")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                cl.setBackground(ContextCompat.getDrawable(this.cl.getContext(), R.drawable.background));
                MainFrag.setSwitch(true);
            }

        }
    }
    public void setCL(ConstraintLayout cl){
        this.cl=cl;
    }

}