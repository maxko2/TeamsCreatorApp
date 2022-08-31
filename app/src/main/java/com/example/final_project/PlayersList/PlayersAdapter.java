package com.example.final_project.PlayersList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.DataBase.RoomDB;
import com.example.final_project.Common.PreferenceManager;
import com.example.final_project.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.ViewHolder>  {
    private List<PlayerData> playerDataList;
    private Context context;
    private HashMap<Integer,String> icons;
    private PlayersViewModel playersViewModel;
    private ViewHolder viewHolder;

    public PlayersAdapter(Application app, Context context, Activity activity){
        playersViewModel=PlayersViewModel.getInstance(app,context,activity);
        this.context=context;
        this.playerDataList=playersViewModel.getPlayersLiveData().getValue();
        this.icons=playersViewModel.getIconsLivedata().getValue();
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflate the custom layout
        View playersView = inflater.inflate(R.layout.row_player_item, parent, false);
        // Return a new holder instance
        viewHolder = new ViewHolder(playersView);

        Observer<ArrayList<PlayerData>> observerSize=new Observer<ArrayList<PlayerData>>() {
            @Override
            public void onChanged(ArrayList<PlayerData> playerData) {
            }
        };
        playersViewModel.getPlayersLiveData().observe((LifecycleOwner)context,observerSize);

        Observer<HashMap> iconsNum=new Observer<HashMap>() {
            @Override
            public void onChanged(HashMap hashMap) {
            }
        };
        playersViewModel.getIconsLivedata().observe((LifecycleOwner)context,iconsNum);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlayerData data = playerDataList.get(position);
        Observer<Integer> observerSelectedIndex=new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        };
        playersViewModel.getIndexSelectedItem().observe((LifecycleOwner)context,observerSelectedIndex);
        holder.tvName.setText("Name: " + data.getFullName());
        holder.tvAge.setText("Age: " + data.getAge() + "");

        StringBuilder sb = new StringBuilder();
        if (data.getFootRate() != 0) {
            sb.append("Football Rate: ");
            sb.append(data.getFootRate() + "\n");
            sb.append("Preferred Foot: ");
            sb.append(data.getFoot()+"\n");
        }
        if (data.getBasketRate() != 0) {
            sb.append("Basketball Rate: ");
            sb.append(data.getBasketRate());
            sb.append("\nHeight: ");
            sb.append(data.getBasketHeight());
        }
        holder.tvDetails.setText(sb.toString());
        if (icons.containsKey(data.getPid())) {
            String tmpIcon=icons.get(data.getPid());
            byte[] b = Base64.decode(icons.get(data.getPid()), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            if(bitmap!=(null))
                holder.iconImg.setImageBitmap(bitmap);
            else
                holder.iconImg.setImageResource(R.drawable.personavatar);
        }

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    TextView title_of_dialog = new TextView(context);
                    title_of_dialog.setHeight(150);
                    title_of_dialog.setBackgroundColor(Color.parseColor("#40AC91"));
                    title_of_dialog.setText("Delete Player");
                    title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    title_of_dialog.setTextColor(Color.WHITE);
                    title_of_dialog.setGravity(Gravity.LEFT);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setCustomTitle(title_of_dialog);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    PlayerData data = playerDataList.get(holder.getAdapterPosition());
                                    playersViewModel.deleteIcon(data.getPid()+"");
                                    playersViewModel.deletePlayer(data);
                                    int position = holder.getAdapterPosition();
                                    playerDataList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeRemoved(position, playerDataList.size());
                                    notifyDataSetChanged();
                                }
                            })
                            .setCancelable(false)
                            .setNegativeButton("No",null);
                    AlertDialog alert =alertDialogBuilder.create();
                    alert.show();
                    TextView textView = (TextView) alert.findViewById(android.R.id.message);
                    textView.setGravity(Gravity.LEFT);
                    textView.setTextSize(25);
                    textView.setTextColor(Color.BLACK);

                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return this.playerDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //private View itemView;
        private TextView tvName,tvAge,tvDetails;
        private ImageView iconImg,deleteBtn;
        private final Context   context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvName=(TextView) itemView.findViewById(R.id.tvName);
            tvAge=(TextView) itemView.findViewById(R.id.tvAge);
            tvDetails=(TextView) itemView.findViewById(R.id.tvDetails);
            deleteBtn=(ImageView) itemView.findViewById(R.id.deleteBtn);
            iconImg=(ImageView) itemView.findViewById(R.id.iconImg);
        }

        }
    }



