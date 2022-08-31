package com.example.final_project;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.final_project.Common.PreferenceManager;
import com.example.final_project.DataBase.DataDao;
import com.example.final_project.DataBase.PlayerData;
import com.example.final_project.DataBase.RoomDB;
import com.example.final_project.PlayersList.PlayersViewModel;
import com.example.final_project.StartGame.StartGameViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddPlayerFrag extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    CheckBox basketball,football;
    EditText name,age;
    Spinner footRate,basketRate,basketHeight,foot;
    Button submit,clear;
    RoomDB db= RoomDB.getInstance(getContext());
    DataDao playerDao = db.getItemDao();
    PlayerData tmp;
    ImageView img;
    String id;
    String encodedImage="";
    BottomNavigationView bmenu;
    PlayersViewModel pvm;
    PreferenceManager pm=PreferenceManager.getInstance(getContext());
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState    );
        basketball=(CheckBox)view.findViewById(R.id.basketballCB);
        football=(CheckBox)view.findViewById(R.id.footballCB);
        footRate=(Spinner) view.findViewById(R.id.fRateSp);
        basketRate=(Spinner) view.findViewById(R.id.bRateSp);
        basketHeight=(Spinner) view.findViewById(R.id.bHeightSp);
        foot=(Spinner) view.findViewById(R.id.footSp);
        submit = (Button) view.findViewById(R.id.submitBtn);
        clear = (Button) view.findViewById(R.id.clearBtn);
        name=(EditText) view.findViewById(R.id.etName);
        age=(EditText) view.findViewById(R.id.etAge);
        img=(ImageView) view.findViewById(R.id.imgBtn);
        submit.setOnClickListener(this);
        clear.setOnClickListener(this);
        basketball.setOnCheckedChangeListener(this);
        football.setOnCheckedChangeListener(this);
        footRate.setEnabled(false);
        bmenu = getActivity().findViewById(R.id.bottomNavigationView2);
        foot.setEnabled(false);
        basketRate.setEnabled(false);
        basketHeight.setEnabled(false);
        ArrayAdapter<CharSequence> numbers = ArrayAdapter.createFromResource(getContext(),R.array.numbers, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> heights = ArrayAdapter.createFromResource(getContext(),R.array.heights, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> prefFoot = ArrayAdapter.createFromResource(getContext(),R.array.foot, android.R.layout.simple_spinner_item);
        numbers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heights.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefFoot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        footRate.setAdapter(numbers);
        basketRate.setAdapter(numbers);
        foot.setAdapter(prefFoot);
        basketHeight.setAdapter(heights);
        img.setOnClickListener(this);
        pvm=PlayersViewModel.getInstance(getActivity().getApplication(), getContext(),getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.addplayerfrag, container, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submitBtn:
                if(age.getText().toString().isEmpty()||name.getText().toString().isEmpty()){
                    ToastMsg("Name and Age cannot be empty");
                    break;
                }
                if ((!football.isChecked()&&!basketball.isChecked())){
                    ToastMsg("Must check Football or Basketball (or both)");
                    break;
                }
                if ((!TextUtils.isDigitsOnly(age.getText().toString()))) {
                    ToastMsg("Age must be numbers only");
                    break;
                }
                tmp=new PlayerData();
                tmp.setFullName(name.getText().toString());
                tmp.setAge(Integer.parseInt(age.getText().toString()));
                if(football.isChecked()) {
                    tmp.setFootRate(Integer.parseInt(footRate.getSelectedItem().toString()));
                    tmp.setFoot(foot.getSelectedItem().toString());
                }
                if(basketball.isChecked()) {
                    tmp.setBasketRate(Integer.parseInt(basketRate.getSelectedItem().toString()));
                    tmp.setBasketHeight(basketHeight.getSelectedItem().toString());
                }
                id=playerDao.insert(tmp)+"";
                if(!encodedImage.equals(""))
                    pm.setString(id,encodedImage);
                sumbitDialogPop();
                pvm.updateIcons(id);
                break;

            case R.id.imgBtn:
                boolean pick=true;
                if(pick){
                    if(!checkCameraPermission()) {
                        requestCameraPermisson();

                }else pickImage();


                }else
                if(!checkStoragePermission()) {
                    requestStoragePermisson();
                }else pickImage();


                break;

            case R.id.clearBtn:
                DialogPop();
                break;

            default:break;
        }
    }

    private void sumbitDialogPop() {
        TextView title_of_dialog = new TextView(getContext());
        title_of_dialog.setHeight(210);
        title_of_dialog.setBackgroundColor(Color.parseColor("#40AC91"));
        title_of_dialog.setText("Player Added Succesfully");
        title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        title_of_dialog.setTextColor(Color.WHITE);
        title_of_dialog.setGravity(Gravity.LEFT);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setCustomTitle(title_of_dialog);
        // set dialog message
        alertDialogBuilder
                .setMessage("Add another player?")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        name.setText("");
                        age.setText("");
                        football.setChecked(false);
                        basketball.setChecked(false);
                        encodedImage="";
                        img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.personavatar));
                    }
                })
                .setCancelable(false)
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        bmenu.getMenu().findItem(R.id.homebM).setChecked(true);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragContainer1, MainFrag.class, null,"addPTag")
                                .addToBackStack(null)
                                .commit();
                        getActivity().getSupportFragmentManager().executePendingTransactions();
                    }});
        AlertDialog alert =alertDialogBuilder.create();
        alert.show();
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(25);
        textView.setTextColor(Color.BLACK);
    }


    private void pickImage() {
            final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.R)

                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo"))
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    }
                    else if (options[item].equals("Choose from Gallery"))
                    {
                        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                    else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK && requestCode==1){
            Bitmap bm=(Bitmap)data.getExtras().get("data");
            img.setImageBitmap(bm);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        }
        if(resultCode==RESULT_OK && requestCode==2){
            img.setImageURI(data.getData());
            Bitmap bitmap=null;
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

           // Bitmap bm=(Bitmap)data.getExtras().get("data");
            img.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        }
    }


    private void requestStoragePermisson() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

    }

    private void requestCameraPermisson() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    private boolean checkCameraPermission() {
        boolean res1= ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;
        boolean res2= ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return res1&&res2;
    }
    private boolean checkStoragePermission() {
        boolean res2= ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return res2;
    }


    public void DialogPop(){
        TextView title_of_dialog = new TextView(getContext());
        title_of_dialog.setHeight(150);
        title_of_dialog.setBackgroundColor(Color.parseColor("#40AC91"));
        title_of_dialog.setText("Clear");
        title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        title_of_dialog.setTextColor(Color.WHITE);
        title_of_dialog.setGravity(Gravity.LEFT);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setCustomTitle(title_of_dialog);
        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to clear all?")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        name.setText("");
                        age.setText("");
                        football.setChecked(false);
                        basketball.setChecked(false);
                        img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.personavatar));
                    }
                })
                .setCancelable(false).setNegativeButton("No",null);
        AlertDialog alert =alertDialogBuilder.create();
        alert.show();
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(25);
        textView.setTextColor(Color.BLACK);
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            footRate.setEnabled(football.isChecked());
            basketRate.setEnabled(basketball.isChecked());
            basketHeight.setEnabled(basketball.isChecked());
            foot.setEnabled(football.isChecked());

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