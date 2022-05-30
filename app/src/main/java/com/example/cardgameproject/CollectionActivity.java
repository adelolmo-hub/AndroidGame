package com.example.cardgameproject;

import static com.example.cardgameproject.MenuActivity.musicShonenCard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.media.Image;
import android.media.MediaPlayer;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cardgameproject.databinding.ActivityCollectionBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class CollectionActivity extends AppCompatActivity {


    ActivityCollectionBinding binding;
    private SharedPreferences spShonenCard;
    public static ArrayList<Card> cards;
    DAOUser daoUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        musicMainThemeColl();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        daoUser = new DAOUser(firebaseAuth.getCurrentUser().getUid());

        cards = (ArrayList<Card>) getIntent().getSerializableExtra("cards");
        User user = (User) getIntent().getSerializableExtra("user");


        GridAdapter gridAdapter = new GridAdapter(this, cards, user);
        binding.gridView.setAdapter(gridAdapter);

        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Card card = cards.get(i);
                TextView textPrice = view.findViewById(R.id.tvPrice);
                AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(adapterView.getContext());
                createAccountBuilder.setTitle("Do you want to buy this card??");
                createAccountBuilder.setMessage(textPrice.getText().toString() + "\nYour money: " + user.getBerry());
                createAccountBuilder.setNegativeButton("", null);
                createAccountBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       int newMoney = user.getBerry() - Integer.parseInt(textPrice.getText().toString().replaceAll("\\D+",""));
                       HashMap<String, Object> map = new HashMap<>();
                       map.put(card.getName(), "complete");
                        daoUser.updateUserBuyCard(map);
                        daoUser.updateBerry(newMoney);
                    }
                });
                createAccountBuilder.show();
            }
        });
    }

    public void musicMainThemeColl(){
        Bundle bundle = getIntent().getExtras();
        spShonenCard = getSharedPreferences("shonenCardPreference", Context.MODE_PRIVATE);
        if(spShonenCard.getBoolean("music", true)) {
            musicShonenCard = MediaPlayer.create(this, R.raw.main_theme);
            if (bundle.getInt("mediaPlayerTimePos") > 0) {
                musicShonenCard.seekTo(bundle.getInt("mediaPlayerTimePos"));
            }
            musicShonenCard.start();
            musicShonenCard.setLooping(true);
        }
    }

    public void onClickBackColl(View view){
        Intent back = getIntent();
        setResult(RESULT_OK, back);
        back.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
        musicShonenCard.stop();
        musicShonenCard.reset();
        finish();
    }

}