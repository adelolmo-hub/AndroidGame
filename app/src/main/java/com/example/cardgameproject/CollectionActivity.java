package com.example.cardgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.media.MediaPlayer;

import android.os.Bundle;

import android.view.View;

import com.example.cardgameproject.databinding.ActivityCollectionBinding;

public class CollectionActivity extends AppCompatActivity {


    ActivityCollectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicMainThemeColl();

        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        GridAdapter gridAdapter = new GridAdapter(this, MenuActivity.cards);
        binding.gridView.setAdapter(gridAdapter);

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