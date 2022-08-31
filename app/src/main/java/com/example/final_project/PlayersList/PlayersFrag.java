package com.example.final_project.PlayersList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.AddPlayerFrag;
import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.DataBase.RoomDB;
import com.example.final_project.MainFrag;
import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class PlayersFrag extends Fragment {

    private RecyclerView rvPlayers;
    private PlayersAdapter adapter;
    private RoomDB db;
    private List<PlayerData> playerDataList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    BottomNavigationView bmenu;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playersfrag, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvPlayers = view.findViewById(R.id.rvPlayers);
        db=RoomDB.getInstance(getContext());
        playerDataList = db.getItemDao().getAll();
        bmenu = getActivity().findViewById(R.id.bottomNavigationView2);
        if(playerDataList.size()==0)
            checkEmptyDB();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PlayersAdapter(getActivity().getApplication(),getContext(),getActivity());

        rvPlayers.setAdapter(adapter);
        rvPlayers.setLayoutManager(linearLayoutManager);
    }


    private void checkEmptyDB() {
            TextView title_of_dialog = new TextView(getContext());
            title_of_dialog.setHeight(150);
            title_of_dialog.setBackgroundColor(Color.parseColor("#40AC91"));
            title_of_dialog.setText("Empty list");
            title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            title_of_dialog.setTextColor(Color.WHITE);
            title_of_dialog.setGravity(Gravity.LEFT);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setCustomTitle(title_of_dialog);
            // set dialog message
            alertDialogBuilder
                    .setMessage("No players added yet, do you want to add a player?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setReorderingAllowed(true)
                                    .replace(R.id.fragContainer1, AddPlayerFrag.class, null, "addPTag")
                                    .addToBackStack(null)
                                    .commit();
                            getActivity().getSupportFragmentManager().executePendingTransactions();
                        }
                    })
                    .setCancelable(false)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            bmenu.getMenu().findItem(R.id.homebM).setChecked(true);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setReorderingAllowed(true)
                                    .replace(R.id.fragContainer1, MainFrag.class, null, "addPTag")
                                    .addToBackStack(null)
                                    .commit();
                            getActivity().getSupportFragmentManager().executePendingTransactions();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
            TextView textView = (TextView) alert.findViewById(android.R.id.message);
            textView.setGravity(Gravity.LEFT);
            textView.setTextSize(25);
            textView.setTextColor(Color.BLACK);
        }

}
