package com.example.final_project.StartGame;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StartGameFrag extends Fragment implements View.OnClickListener {
    private RecyclerView rvStartGame;
    private StartGameAdapter adapter;
    startGameFragListener listener;
    TextView tvCounter;
    Button startBtn;
    RadioGroup rg;
    BottomNavigationView bmenu;

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.listener = (startGameFragListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    getActivity().getClass().getName() +
                    " must implements the interface 'starGameFragListener'");
        }
        super.onAttach(context);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.startgamefrag, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvStartGame = view.findViewById(R.id.rvStartGame);
        rg=view.findViewById(R.id.radioG);
        startBtn = view.findViewById(R.id.startGameBtnFrag);
        startBtn.setOnClickListener(this);
        tvCounter=view.findViewById(R.id.tvCounter);
        bmenu = getActivity().findViewById(R.id.bottomNavigationView2);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                switch(index){
                    case 0:
                        adapter.setFootDB();
                        break;
                    case 1:
                        adapter.setBasketDB();
                        break;
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new StartGameAdapter(getActivity().getApplication(),getContext(),getActivity(),this);
        rvStartGame.setAdapter(adapter);
        rvStartGame.setLayoutManager(new LinearLayoutManager(getActivity()));
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


    @Override
    public void onClick(View view) {
        ArrayList<PlayerData> arr=adapter.getPlayersList();
        if(arr.size()<1)

            ToastMsg("Must select at least 2 players to start a game");
        else if(arr.size()%2!=0) {
            ToastMsg("Must select a even number of players");
        }
        else {
            Collections.sort(arr, new Comparator<PlayerData>() {
                @Override
                public int compare(PlayerData playerData, PlayerData t1) {
                    if (adapter.getFlag() == 1) {
                        if (playerData.getBasketRate() > t1.getBasketRate())
                            return 1;
                        if (playerData.getBasketRate() < t1.getBasketRate())
                            return -1;
                        return 0;
                    } else if (adapter.getFlag() == 2) {
                        if (playerData.getFootRate() > t1.getFootRate())
                            return 1;
                        if (playerData.getFootRate() < t1.getFootRate())
                            return -1;
                        return 0;
                    }
                    return 0;
                }
            });

            arr= randomizeTeams(arr);
            bmenu.getMenu().findItem(R.id.currgbM).setChecked(true);
            listener.sendPlayers(arr,adapter.getFlag());

        }

    }
    public ArrayList<PlayerData> randomizeTeams(ArrayList<PlayerData> pd){
        ArrayList<PlayerData> newArr = new ArrayList<>();
        for (int i=0; i<pd.size();i=i+2)
        {
            if(Math.random()>0.5) {
                newArr.add(pd.get(i));
                newArr.add(pd.get(i+1));
                }
            else {
                newArr.add(pd.get(i + 1));
                newArr.add(pd.get(i ));
            };
        }
        return newArr;
    }


    public interface startGameFragListener{
         ArrayList<PlayerData> sendPlayers(ArrayList<PlayerData> arr,int flag);
        //put here methods you want to utilize to communicate with the hosting activity
    }


}



