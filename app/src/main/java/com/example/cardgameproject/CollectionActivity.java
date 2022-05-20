package com.example.cardgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cardgameproject.databinding.ActivityCollectionBinding;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CollectionActivity extends AppCompatActivity {

    private final String STORAGE_NAME = "CardImages";
    ActivityCollectionBinding binding;
    StorageReference storageReference;
    private SharedPreferences spShonenCard;
    public static MediaPlayer musicShonenCard = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicMainThemeColl();

        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String> champs = new ArrayList<>();
        ArrayList<Bitmap> imageList = new ArrayList<>();

        storageReference = FirebaseStorage.getInstance().getReference().child(STORAGE_NAME);
        storageReference.listAll().addOnSuccessListener(listResult -> {
            for(StorageReference fileRef : listResult.getItems()){
               champs.add(fileRef.getName());
                try {
                    File tempFile = File.createTempFile("tempfile",".jpg");
                    fileRef.getFile(tempFile).addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                        imageList.add(bitmap);
                        if(champs.size() == imageList.size()){
                            //TODO chapuza de la ostia, hay que mirarla
                            GridAdapter gridAdapter = new GridAdapter(this, champs, imageList);
                            binding.gridView.setAdapter(gridAdapter);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
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