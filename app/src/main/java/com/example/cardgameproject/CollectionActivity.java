package com.example.cardgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}