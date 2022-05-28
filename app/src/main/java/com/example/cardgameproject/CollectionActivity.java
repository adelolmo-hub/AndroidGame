package com.example.cardgameproject;

import static com.example.cardgameproject.MenuActivity.musicShonenCard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.media.Image;
import android.media.MediaPlayer;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.cardgameproject.databinding.ActivityCollectionBinding;

public class CollectionActivity extends AppCompatActivity {


    ActivityCollectionBinding binding;
    private SharedPreferences spShonenCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User user = (User) getIntent().getSerializableExtra("user");
        musicMainThemeColl();

        GridAdapter gridAdapter = new GridAdapter(this, MenuActivity.cards, user);
        binding.gridView.setAdapter(gridAdapter);

        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fragments = user.getObtainedFragments().get(MenuActivity.cards.get(i).getName());
                if("complete".equals(fragments)){
                    ImageView im = view.findViewById(R.id.grid_image);
                    im.setBackgroundResource(R.drawable.highlight);
                }
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