package com.example.final_project;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.final_project.Common.DarkModeSharedPreference;
import com.example.final_project.DataBase.DataDao;
import com.example.final_project.DataBase.RoomDB;


public class MainFrag extends Fragment  implements CompoundButton.OnCheckedChangeListener {
	Button addPlayer,playersList,startGame;
	RoomDB db;
	DataDao pd;
	DarkModeSharedPreference sh;
	private static Switch darkSwitch;
	@Override
	public void onAttach(@NonNull Context context) {
		db=RoomDB.getInstance(getActivity().getBaseContext());
		pd=db.getItemDao();

		super.onAttach(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mainfrag, container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		darkSwitch= view.findViewById(R.id.switch1);
			darkSwitch.setOnCheckedChangeListener(this);
			sh=DarkModeSharedPreference.getInstance(getContext());
			String darkmodeString = sh.getString("darkmode");
			String batteryP = sh.getString("batteryp");
			if(!batteryP.equals("")) {
				if (Float.parseFloat(batteryP) > 15) {
					if (darkmodeString == null) {
						sh.setString("darkmode", "off");
						darkSwitch.setChecked(false);
					} else if (darkmodeString.equals("on"))
						darkSwitch.setChecked(true);
				}else darkSwitch.setEnabled(false);
			}

		super.onViewCreated(view, savedInstanceState);
	}

	public static void setSwitch(boolean flag){
		darkSwitch.setEnabled(flag);
	}

	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
		ConstraintLayout cl=getActivity().findViewById(R.id.cl);
		if(b)
		{
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			cl.setBackground(null);
			sh.setString("darkmode","on");
		}else{
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			cl.setBackground(ContextCompat.getDrawable(cl.getContext(), R.drawable.background));
			sh.setString("darkmode","off");
		}
	}
}
