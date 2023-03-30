package com.example.cardgameproject.activities;

import static com.example.cardgameproject.activities.MenuActivity.musicShonenCard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.media.MediaPlayer;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardgameproject.adapters.GridAdapter;
import com.example.cardgameproject.R;
import com.example.cardgameproject.databinding.ActivityCollectionBinding;
import com.example.cardgameproject.models.Card;
import com.example.cardgameproject.models.DAOUser;
import com.example.cardgameproject.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class CollectionActivity extends AppCompatActivity {


    ActivityCollectionBinding binding;
    private SharedPreferences spShonenCard;
    public static ArrayList<Card> cards;
    DAOUser daoUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        musicMainThemeColl();

        user = User.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        daoUser = new DAOUser(firebaseAuth.getCurrentUser().getUid());

        cards = (ArrayList<Card>) getIntent().getSerializableExtra("cards");


        GridAdapter gridAdapter = new GridAdapter(this, cards, user);
        binding.gridView.setAdapter(gridAdapter);

        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Card card = cards.get(i);
                TextView textPrice = view.findViewById(R.id.tvPrice);
                TextView textFragments = view.findViewById(R.id.tvName);
                ImageView imageView = view.findViewById(R.id.grid_image);
                AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(adapterView.getContext());
                createAccountBuilder.setTitle("Do you want to buy this card??");
                createAccountBuilder.setMessage(textPrice.getText().toString() + "\nYour money: " + user.getBerry());
                createAccountBuilder.setNegativeButton("Cancel", null);
                createAccountBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       int newMoney = user.getBerry() - Integer.parseInt(textPrice.getText().toString().replaceAll("\\D+",""));
                       if(newMoney >= 0) {
                           HashMap<String, Object> map = new HashMap<>();
                           map.put(card.getName(), "complete");
                           imageView.setColorFilter(null);
                           textFragments.setText("Complete");
                           textPrice.setText("");
                           daoUser.updateUserBuyCard(map);
                           daoUser.updateBerry(newMoney);

                       }else{
                           Toast.makeText(CollectionActivity.this, "You don't have enough money", Toast.LENGTH_SHORT).show();
                       }
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