package com.example.cardgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cardgameproject.databinding.ActivityCollectionBinding;

public class CollectionActivity extends AppCompatActivity {

    ActivityCollectionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String[] champs = {"Exreal","Fiora","Garen","Katarina","Lux","Vi"};
        int[] image = {R.drawable.ezreal,R.drawable.fiora,R.drawable.garen,R.drawable.kata,R.drawable.lux,R.drawable.vi};

        GridAdapter gridAdapter = new GridAdapter(this, champs, image);
        binding.gridView.setAdapter(gridAdapter);
    }
}