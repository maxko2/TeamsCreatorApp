package com.example.final_project.StartGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.AddPlayerFrag;
import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.DataBase.RoomDB;
import com.example.final_project.MainFrag;
import com.example.final_project.Common.PreferenceManager;
import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class StartGameAdapter extends RecyclerView.Adapter<StartGameAdapter.ViewHolder> {
    private static ArrayList<PlayerData> plList;
    private static ArrayList<PlayerData> basketList;
    private static ArrayList<PlayerData> footList;
    private static ArrayList<PlayerData> selectedPlayers;
    private Context context;
    private StartGameViewModel viewModel;
    private Application myApp;
    private ViewHolder viewHolder;
    private AppCompatActivity activity;
    private StartGameFrag frag;
    private Button startBtn;
    PreferenceManager pm;
    BottomNavigationView bmenu;
    private int flagBasketOrFootball=0;//basket= 1 football=2;

    private int selectedPos = -1;
    RoomDB db;
    public StartGameAdapter(Application app, Context context, Activity act,StartGameFrag frag){
        myApp=app;
        db=RoomDB.getInstance(context);
        this.context=context;
        this.frag=frag;
        startBtn=frag.startBtn;
        pm=PreferenceManager.getInstance(context.getApplicationContext());
        activity=(AppCompatActivity) context;
        viewModel= StartGameViewModel.getInstance(app,myApp,act);
        selectedPlayers = new ArrayList<>();
        bmenu = activity.findViewById(R.id.bottomNavigationView2);
        basketList= (ArrayList<PlayerData>) db.getItemDao().getBasketBallPlayers();
        footList= (ArrayList<PlayerData>) db.getItemDao().getFootballPlayers();
            if(basketList.size()<1&&footList.size()<1)
            addPlayerDialog();
        plList=new ArrayList<>();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the custom layout
        View playerView = inflater.inflate(R.layout.row_startgame_item, parent, false);
        //startBtn.setOnClickListener(this);
        // Return a new holder instance
        viewHolder = new ViewHolder(playerView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerData playerData = plList.get(position);
        Observer<Integer> observeSelectedIndex = new Observer<Integer>() {
            @Override
            public void onChanged(Integer index) {
                selectedPos = index;
            }
        };
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked&&!selectedPlayers.contains(playerData)){
                    selectedPlayers.add(playerData);
                }else if(!isChecked){
                    selectedPlayers.remove(playerData);
                }
                frag.tvCounter.setText("Number of players selected: "+selectedPlayers.size());
                if(selectedPlayers.size()%2==1||selectedPlayers.size()==0)
                    frag.tvCounter.setBackgroundResource(R.color.red);
                else
                    frag.tvCounter.setBackgroundResource(R.color.green);

            }

        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pos = holder.getAdapterPosition();
                plList.remove(pos);
                viewModel.setPlayerLiveData(plList);


                // this logic will keep the selected row select when change the position index
                if (pos < selectedPos){
                    selectedPos--;
                    viewModel.setPositionSelected(selectedPos);
                } else if (pos == selectedPos){
                    selectedPos = -1;
                    viewModel.setPositionSelected(-1);
                    viewModel.setItemSelect(null);
                }
                notifyDataSetChanged();
                return true;
            }
        });



        holder.bindData(playerData);

    }

    @Override
    public int getItemCount() {
        return this.plList.size();
    }


    public void setBasketDB() {
        flagBasketOrFootball=1;
        selectedPlayers.clear();
        plList.clear();
        plList.addAll(basketList);
        viewModel.setPlayerLiveData(plList);
        this.notifyDataSetChanged();
    }

    public void setFootDB() {
        flagBasketOrFootball=2;
        selectedPlayers.clear();
        plList.clear();
        plList.addAll(footList);
        viewModel.setPlayerLiveData(plList);
        this.notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder   {

        private View itemView;
        private TextView tvName,tvAge,tvDetails;
        private ImageView icon;
        private CheckBox checkBox;
        private final Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context=itemView.getContext();
            this.itemView=itemView.findViewById(R.id.stargame_item);
            this.tvName=(TextView) itemView.findViewById(R.id.tvNameS);
            this.tvAge=(TextView)itemView.findViewById(R.id.tvAgeS);
            this.tvDetails=(TextView)itemView.findViewById(R.id.tvDetailsS);
            this.icon=(ImageView)itemView.findViewById(R.id.iconImgS);
            this.checkBox=(CheckBox)itemView.findViewById(R.id.checkBoxBtnS);

        }

        public void bindData(PlayerData pd){
            tvName.setText("Name: "+pd.getFullName());
            tvAge.setText("Age: "+pd.getAge());
            int rate;
            if (flagBasketOrFootball==1) {
                rate = pd.getBasketRate();
                tvDetails.setText("Rate: " +rate+"\nHeight: "+pd.getBasketHeight());
            }
            else {
                rate = pd.getFootRate();
                tvDetails.setText("Rate: " +rate+"\nPreferred foot: "+pd.getFoot());
            }

            if (selectedPlayers.contains(pd))
                checkBox.setChecked(true);
            else checkBox.setChecked(false);
            if (!pm.getString(pd.getPid() + "").equalsIgnoreCase("")) {
                byte[] b = Base64.decode(pm.getString(pd.getPid() + ""), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                icon.setImageBitmap(bitmap);
            }
            else icon.setImageResource(R.drawable.personavatar);



        }
    }

    public ArrayList<PlayerData> getPlayersList (){
        return selectedPlayers;
    }
    public int getFlag (){
        return flagBasketOrFootball;
    }


    private void addPlayerDialog() {
        TextView title_of_dialog = new TextView(context);
        title_of_dialog.setHeight(150);
        title_of_dialog.setBackgroundColor(Color.parseColor("#40AC91"));
        title_of_dialog.setText("Empty list");
        title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        title_of_dialog.setTextColor(Color.WHITE);
        title_of_dialog.setGravity(Gravity.LEFT);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCustomTitle(title_of_dialog);
        // set dialog message
        alertDialogBuilder
                .setMessage("Must have at least 2 players from the same genre\n add another player?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bmenu.getMenu().findItem(R.id.addPbM).setChecked(true);
                        activity.getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragContainer1, AddPlayerFrag.class, null, "addPTag")
                                .addToBackStack(null)
                                .commit();
                        activity.getSupportFragmentManager().executePendingTransactions();
                    }
                })
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bmenu.getMenu().findItem(R.id.homebM).setChecked(true);
                        activity.getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragContainer1, MainFrag.class, null, "addPTag")
                                .addToBackStack(null)
                                .commit();
                        activity.getSupportFragmentManager().executePendingTransactions();
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
