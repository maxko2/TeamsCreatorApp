package com.example.final_project.CurrentGame;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.MainActivity;
import com.example.final_project.MainFrag;
import com.example.final_project.Common.PreferenceManager;
import com.example.final_project.R;
import com.example.final_project.StartGame.StartGameFrag;
import com.example.final_project.Common.TimerWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class CurrentGameFrag extends Fragment  implements View.OnClickListener {
    public RecyclerView rvTeams;
    public CurrentGameAdapter adapter;
    private double avgA,avgB;
    public int flag;
    public ArrayList<PlayerData>arr=new ArrayList<>();
    TextView tvTime,tvAvgA,tvAvgB;
    Button startBtn,ClearBtn;
    teamFragListener listener;
    Spinner sp;
    PreferenceManager pm;
    BottomNavigationView bmenu;


    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.listener = (teamFragListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    getActivity().getClass().getName() +
                    " must implements the interface 'teamFragListener'");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.teamsfrag, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        tvAvgA.setText("Average: "+avgA);
        tvAvgB.setText("Average: "+avgB);
        adapter = new CurrentGameAdapter(getActivity(),arr,this);
        rvTeams.setAdapter(adapter);
        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvTeams = view.findViewById(R.id.rvTeams);
        startBtn = view.findViewById(R.id.StartBtn);
        tvAvgA = (TextView) view.findViewById(R.id.tvAvgA);
        tvAvgB = (TextView)view.findViewById(R.id.tvAvgB);
        startBtn.setOnClickListener(this);
        sp=view.findViewById(R.id.timerSpinner);
        pm=PreferenceManager.getInstance(getContext());
        ArrayAdapter<CharSequence> minutes = ArrayAdapter.createFromResource(getContext(),R.array.minutes, android.R.layout.simple_spinner_item);
        minutes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(minutes);
        ClearBtn = view.findViewById(R.id.ClearBtn);
        bmenu = getActivity().findViewById(R.id.bottomNavigationView2);
        ClearBtn.setOnClickListener(this);
        tvTime = view.findViewById(R.id.Timer);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void CalcAVG(ArrayList<PlayerData> playerDataList) {
        double avgTeamA=0,avgTeamB=0;
        if(playerDataList!=null) {
            for (int i = 0; i < playerDataList.size(); i++) {
                Log.i("FLAGIS", flag + "");
                if (i % 2 == 0) {
                    if (flag == 1)
                        avgTeamA += playerDataList.get(i).getBasketRate();
                    else
                        avgTeamA += playerDataList.get(i).getFootRate();
                } else {
                    if (flag == 1) {
                        avgTeamB += playerDataList.get(i).getBasketRate();
                    } else avgTeamB += playerDataList.get(i).getFootRate();
                }
            }
            avgA = avgTeamA / (playerDataList.size() / 2.0);
            avgB = avgTeamB / (playerDataList.size() / 2.0);
        }

    }

    public int getFlag (){
        return this.flag;
    }



    public void setSelectedPlayersArr(ArrayList<PlayerData>arr ,int flag){
        this.arr=arr;
        this.flag=flag;
        CalcAVG(arr);
        tvAvgA.setText("Average: "+avgA);
        tvAvgB.setText("Average: "+avgB);
        adapter = new CurrentGameAdapter(getActivity(),arr,this);
        rvTeams.setAdapter(adapter);
        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.StartBtn:
                if(!sp.getSelectedItem().toString().equals("Select Minutes")) {
                    Data.Builder data = new Data.Builder();
                    data.putString("timerSet", sp.getSelectedItem().toString());
                    WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(TimerWorker.class).setInputData(data.build()).build();
                    WorkManager.getInstance(getContext())
                            // requestId is the WorkRequest id
                            .getWorkInfoByIdLiveData(uploadWorkRequest.getId())
                            .observeForever(new Observer<WorkInfo>() {
                                @Override
                                public void onChanged(@Nullable WorkInfo workInfo) {
                                    if (workInfo != null) {
                                        Data progress = workInfo.getProgress();
                                        String st = progress.getString("time");
                                        String finished = progress.getString("timeFinished");
                                        if(st!=null)
                                            tvTime.setText(st);

                                        if (finished != null && finished.equals("finished")) {
                                            tvTime.setText("0:00");
                                            gameFinishedDialog();
                                            MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.timersound);
                                            mp.start();
                                            startBtn.setEnabled(true);
                                            ToastMsg("Game is finished!!!");
                                        }
                                    }
                                }
                            });

                    WorkManager.getInstance(getContext()).enqueue(uploadWorkRequest);
                    startBtn.setEnabled(false);

                }
                else
                     Toast.makeText(getContext(), "Please select time for timer", Toast.LENGTH_LONG).show();

                break;
            case R.id.ClearBtn:
                TimerWorker.killTime();
                startBtn.setEnabled(true);
                tvTime.setText("0:00");
                break;
            default:break;
        }


    }
    public void resetScreen()
    {
        rvTeams.setAdapter(null);
        rvTeams.removeAllViews();
        TimerWorker.killTime();
        tvTime.setText("0:00");
    }
    public void gameFinishedDialog(){
        TextView title_of_dialog = new TextView(getContext());
        title_of_dialog.setHeight(150);
        title_of_dialog.setBackgroundColor(Color.parseColor("#40AC91"));
        title_of_dialog.setText("Time is finished");
        title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        title_of_dialog.setTextColor(Color.WHITE);
        title_of_dialog.setGravity(Gravity.LEFT);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setCustomTitle(title_of_dialog);
        // set dialog message
        alertDialogBuilder
                .setMessage("Start another game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvTime.setText("0:00");
                        bmenu.getMenu().findItem(R.id.startgbM).setChecked(true);
                        MainActivity.increaseCnt();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragContainer1, StartGameFrag.class, null, "startGameFrag")
                                .addToBackStack(null)
                                .commit();
                        getActivity().getSupportFragmentManager().executePendingTransactions();
                    }


                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bmenu.getMenu().findItem(R.id.homebM).setChecked(true);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragContainer1, MainFrag.class, null, "mainFrag")
                                .addToBackStack(null)
                                .commit();
                        getActivity().getSupportFragmentManager().executePendingTransactions();

                    }
                });
        AlertDialog alert =alertDialogBuilder.create();
        alert.show();
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(25);
        textView.setTextColor(Color.BLACK);
    }


    public interface teamFragListener{
        ArrayList<PlayerData> getPlayers(ArrayList<PlayerData> arr);
        //put here methods you want to utilize to communicate with the hosting activity
    }


    private void ToastMsg(String st){
        Toast toast = Toast.makeText(getContext(), st, Toast.LENGTH_LONG);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            View view = (View) toast.getView();
            view.setBackgroundResource(R.drawable.toast);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setTextColor(Color.parseColor("#FFFFFF"));
        }
        toast.show();
    }
}
