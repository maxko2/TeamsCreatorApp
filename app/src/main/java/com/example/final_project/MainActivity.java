package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.final_project.Common.BatteryChangeReceiver;
import com.example.final_project.Common.PreferenceManager;
import com.example.final_project.CurrentGame.CurrentGameFrag;
import com.example.final_project.DataBase.DataDao;
import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.DataBase.RoomDB;
import com.example.final_project.PlayersList.PlayersFrag;
import com.example.final_project.StartGame.StartGameFrag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, CurrentGameFrag.teamFragListener , StartGameFrag.startGameFragListener{


    BottomNavigationView bmenu;
    PreferenceManager pm;
    BatteryChangeReceiver bcr;
    ConstraintLayout cl;
    public static int fragCount;
    public static void increaseCnt() {
        fragCount++;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragOpen(MainFrag.class,"mainFrag");
        setContentView(R.layout.activity_main);
        RoomDB db = RoomDB.getInstance(this);
        DataDao userDao = db.getItemDao();
        List<PlayerData> users = userDao.getAll();
        bmenu=findViewById(R.id.bottomNavigationView2);
        bmenu.setOnItemSelectedListener(this);
        pm=PreferenceManager.getInstance(this);
        bcr=new BatteryChangeReceiver();
        cl=findViewById(R.id.cl);
        fragCount=0;
        bcr.setCL(cl);
        WorkManager.getInstance(this).cancelAllWork();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(bcr, ifilter);
        cl.setBackground(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
         unregisterReceiver(bcr);
        }catch (Exception e){}
    }



    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bcr);
    }

    public void fragOpen(Class<?> frag, String tag){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragContainer1, (Class<? extends Fragment>) frag, null,tag)
                    .addToBackStack(tag).commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }


    @Override
    public ArrayList<PlayerData> sendPlayers(ArrayList<PlayerData> arr,int flag) {
        CurrentGameFrag teamFrag;

        teamFrag = (CurrentGameFrag) getSupportFragmentManager().findFragmentByTag("teamFrag"+fragCount);
        if(teamFrag != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            //you could add this transactionn to backstack again here if you want to be able to pop it later
            fragmentTransaction.replace(R.id.fragContainer1, teamFrag, "teamFrag").commit();
            getSupportFragmentManager().executePendingTransactions();

        } else {
            fragOpen(CurrentGameFrag.class,"teamFrag"+fragCount);
            teamFrag = (CurrentGameFrag) getSupportFragmentManager().findFragmentByTag("teamFrag"+fragCount);
            teamFrag.setSelectedPlayersArr(arr,flag);

        }

        Log.i("arrMain",arr.toString());
        Log.i ("flagMain",flag+"");
        return null;
    }



    @Override
    public ArrayList<PlayerData> getPlayers(ArrayList<PlayerData> arr) {
        return null;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addPbM:
               fragOpen(AddPlayerFrag.class,"addPlayerFrag");
                break;
            case R.id.homebM:
                fragOpen(MainFrag.class,"mainFrag");
                break;
            case R.id.playerlistbM:
                fragOpen(PlayersFrag.class,"playersListFrag");
                break;
            case R.id.startgbM:
                CurrentGameFrag teamFrag;
                teamFrag = (CurrentGameFrag) getSupportFragmentManager().findFragmentByTag("teamFrag"+fragCount);
                if(teamFrag != null) {
                    gameInProgressDialog(teamFrag);
                } else {
                    fragOpen(StartGameFrag.class,"startGameFrag");
                }
                break;
            case R.id.currgbM:
                CurrentGameFrag teamFrag1;
                teamFrag1 = (CurrentGameFrag) getSupportFragmentManager().findFragmentByTag("teamFrag"+fragCount);
                if(teamFrag1==null)
                    noGameRunningDialog(teamFrag1);
                else {
                    teamFrag1.rvTeams.setAdapter(teamFrag1.adapter);
                    teamFrag1.setSelectedPlayersArr(teamFrag1.arr, teamFrag1.flag);

                    teamFrag1.adapter.notifyDataSetChanged();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    //you could add this transactionn to backstack again here if you want to be able to pop it later
                    fragmentTransaction.replace(R.id.fragContainer1, teamFrag1, "teamFrag"+fragCount).commit();
                    getSupportFragmentManager().executePendingTransactions();
                }
                break;
        }
        bmenu.getMenu().findItem(item.getItemId()).setChecked(true);
        return false;
    }

    public void gameInProgressDialog(CurrentGameFrag teamFrag){
                TextView title_of_dialog = new TextView(this);
                title_of_dialog.setHeight(150);
                title_of_dialog.setBackgroundColor(Color.parseColor("#40AC91"));
                title_of_dialog.setText("Game Started");
                title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                title_of_dialog.setTextColor(Color.WHITE);
                title_of_dialog.setGravity(Gravity.LEFT);
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                alertDialogBuilder.setCustomTitle(title_of_dialog);
                // set dialog message
                alertDialogBuilder
                        .setMessage("There is already a game in progress\n Do you want to stop and start new one?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getSupportFragmentManager().beginTransaction().remove(teamFrag).commit();
                                teamFrag.resetScreen();
                                fragCount++;
                                fragOpen(StartGameFrag.class,"startGameFrag");

                            }

                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                teamFrag.rvTeams.setAdapter(teamFrag.adapter);
                                teamFrag.setSelectedPlayersArr(teamFrag.arr, teamFrag.flag);
                                teamFrag.adapter.notifyDataSetChanged();
                                bmenu.getMenu().findItem(R.id.currgbM).setChecked(true);
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                //you could add this transactionn to backstack again here if you want to be able to pop it later
                                fragmentTransaction.replace(R.id.fragContainer1, teamFrag, "teamFrag"+fragCount).commit();
                                getSupportFragmentManager().executePendingTransactions();

                            }
                        });
                AlertDialog alert =alertDialogBuilder.create();
                alert.show();
                TextView textView = (TextView) alert.findViewById(android.R.id.message);
                textView.setGravity(Gravity.LEFT);
                textView.setTextSize(25);
                textView.setTextColor(Color.BLACK);

    }
    public void noGameRunningDialog(CurrentGameFrag teamFrag1){
        TextView title_of_dialog = new TextView(this);
        title_of_dialog.setHeight(150);
        title_of_dialog.setBackgroundColor(Color.parseColor("#40AC91"));
        title_of_dialog.setText("No available game");
        title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        title_of_dialog.setTextColor(Color.WHITE);
        title_of_dialog.setGravity(Gravity.LEFT);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setCustomTitle(title_of_dialog);
        // set dialog message
        alertDialogBuilder
                .setMessage("There isn't a game in progress")
                .setPositiveButton("Create game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fragCount++;
                        bmenu.getMenu().findItem(R.id.startgbM).setChecked(true);
                        fragOpen(StartGameFrag.class,"startGameFrag");
                    }

                });
        AlertDialog alert =alertDialogBuilder.create();
        alert.show();
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(25);
        textView.setTextColor(Color.BLACK);

    }
}