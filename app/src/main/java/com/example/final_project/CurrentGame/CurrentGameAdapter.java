package com.example.final_project.CurrentGame;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.Common.PreferenceManager;
import com.example.final_project.R;

import java.util.ArrayList;

public class CurrentGameAdapter extends RecyclerView.Adapter<CurrentGameAdapter.ViewHolder> {
    private ArrayList<PlayerData> playerDataList;
    private Activity context;

    private int flag;
    private CurrentGameFrag frag;
    private Application Mycontext;
    private ViewHolder viewHolder;
    PreferenceManager pm;
    ArrayList<PlayerData>arr1=new ArrayList<>();
    ArrayList<PlayerData>arr2=new ArrayList<>();

    private int pos;

    public CurrentGameAdapter(Activity context, ArrayList<PlayerData> playerData, CurrentGameFrag frag){
        this.frag=frag;
        this.context= context;
        this.playerDataList=playerData;

        pm=PreferenceManager.getInstance(context.getApplicationContext());
        this.flag=frag.getFlag();

        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflate the custom layout
        View teamsView = inflater.inflate(R.layout.row_team_item, parent, false);
        // Return a new holder instance

        for(int i=0;i<playerDataList.size();i++)
        {
            if (i%2==0){
                arr1.add(playerDataList.get(i));
            }else arr2.add(playerDataList.get(i));
        }
        viewHolder = new ViewHolder(teamsView);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PlayerData data1 = arr1.get(position);
            PlayerData data2 = arr2.get(position);
            Log.i("PLAYER DATA", data1.toString());
            holder.tvNameA.setText(data1.getFullName());
            if (flag == 1)
                holder.detailsA.setText("Rate: " + data1.getBasketRate() + "\nHeight: " + data1.getBasketHeight());
            else
                holder.detailsA.setText("Rate: " + data1.getFootRate() + "\nFoot: " + data1.getFoot());
            if (!pm.getString(data1.getPid() + "").equalsIgnoreCase("")) {
                byte[] b = Base64.decode(pm.getString(data1.getPid() + ""), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                holder.iconImgA.setImageBitmap(bitmap);
            } else holder.iconImgA.setImageResource(R.drawable.personavatar);


            holder.tvNameB.setText(data2.getFullName());
            if (flag == 1)
                holder.detailsB.setText("Rate: " + data2.getBasketRate() + "\nHeight: " + data2.getBasketHeight());
            else
                holder.detailsB.setText("Rate: " + data2.getFootRate() + "\nFoot: " + data2.getFoot());

            if (!pm.getString(data2.getPid() + "").equalsIgnoreCase("")) {
                byte[] b = Base64.decode(pm.getString(data2.getPid() + ""), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                holder.iconImgB.setImageBitmap(bitmap);
            } else holder.iconImgB.setImageResource(R.drawable.personavatar);

        }




    @Override
    public int getItemCount() {
        return this.playerDataList.size()/2;
    }

    public void reset() {
        pos=0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameA,tvNameB,detailsA,detailsB,tvAvgA,tvAvgB;

        private ImageView iconImgA,iconImgB;
        private final Context   context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context=itemView.getContext();
            tvNameA=(TextView) itemView.findViewById(R.id.tvNameA);
            tvNameB=(TextView) itemView.findViewById(R.id.tvNameB);
            detailsA=(TextView) itemView.findViewById(R.id.tvDetailsA);
            detailsB=(TextView) itemView.findViewById(R.id.tvDetailsB);
            iconImgA=(ImageView) itemView.findViewById(R.id.iconImgA);
            iconImgB=(ImageView) itemView.findViewById(R.id.iconImgB);

        }
    }
}
