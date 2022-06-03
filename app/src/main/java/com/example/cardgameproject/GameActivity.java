package com.example.cardgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private GridView gridMainPlayer;
    private LinearLayout gridRival;
    private LinearLayout gridBoardMain;
    private LinearLayout gridBoardRival;
    private static final int PLAYER_HAND_QTY = 3;
    private static Drawable lastImageDrawable;
    private static ArrayList<Card> mainPlayerDeck = MenuActivity.cards;
    private ArrayList<Card> rivalPlayerDeck = MenuActivity.cards;
    private ArrayList<Card> mainPlayerHand = new ArrayList<>();
    private ArrayList<Card> rivalPlayerHand = new ArrayList<>();

    private static View lastViewClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gridRival = findViewById(R.id.grid_rival);
        gridMainPlayer = findViewById(R.id.grid_main_player);
        gridBoardMain = findViewById(R.id.board_main);
        gridBoardRival = findViewById(R.id.board_rival);

        gridMainPlayer.setBackgroundResource(R.drawable.hand_main);
        gridBoardMain.setBackgroundResource(R.drawable.board_main);
        gridRival.setBackgroundResource(R.drawable.hand_rival);
        gridBoardRival.setBackgroundResource(R.drawable.board_rival);
        createGame();

        gridBoardMain.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.argb(255, 255, 255, (float) 0.73));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundResource(R.drawable.board_main);
                    break;
                case DragEvent.ACTION_DROP:
                    dropCard();
                    rivalPlay();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private void dropCard() {
        ImageView card = new ImageView(this);
        card.setImageDrawable(getLastImageDrawable());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 200, gridBoardMain.getHeight() - 50, 50);
        layoutParams.setMargins(0,20,0,0);
        card.setLayoutParams(layoutParams);
        gridBoardMain.addView(card);
        for (int x = 0; x < gridMainPlayer.getChildCount(); x++) {
            if (gridMainPlayer.getChildAt(x).getTag() == getLastViewClicked().getTag()) {
                mainPlayerHand.remove(x);
            }
        }
        if (mainPlayerDeck.size() > 0) {
            mainPlayerHand.add(mainPlayerDeck.get(0));
            mainPlayerDeck.remove(0);
            CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand);
            gridMainPlayer.setAdapter(cardAdapter);
        }
    }

    private void rivalPlay(){
        Random r = new Random();
        int result = r.nextInt((PLAYER_HAND_QTY-1));
        ImageView card = new ImageView(this);
        Picasso.with(this).load(rivalPlayerHand.get(result).getImageUrl()).into(card);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 200, gridBoardRival.getHeight() - 50, 50);
        layoutParams.setMargins(0,20,0,0);
        card.setLayoutParams(layoutParams);
        gridBoardRival.addView(card);
        rivalPlayerHand.remove(result);
        if(rivalPlayerDeck.size() > 0) {
            rivalPlayerHand.add(rivalPlayerDeck.get(0));
            rivalPlayerDeck.remove(0);
        }
        if(rivalPlayerHand.size() < 3){
            gridRival.removeView(gridRival.getChildAt(0));
        }
    }

    public void createGame(){
        //TODO - Recoger nombre de usuario de la base de datos para añadirlo al Main Player
        MainPlayer mainPlayer = new MainPlayer("Albert", 100);
        //ArrayList<Card> mainPlayerDeck = mainPlayer.getPlayerDeck();

        Collections.shuffle(mainPlayerDeck);
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            mainPlayerHand.add(mainPlayerDeck.get(i));
            mainPlayerDeck.remove(i);
        }

        CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand);
        gridMainPlayer.setAdapter(cardAdapter);
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            rivalPlayerHand.add(rivalPlayerDeck.get(i));

        }
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            ImageView cardRivalHand = new ImageView(this);
            cardRivalHand.setImageResource(R.drawable.rival_card);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 200, 200);
            layoutParams.setMargins(30,0,30,0);
            cardRivalHand.setLayoutParams(layoutParams);
            gridRival.addView(cardRivalHand);
        }
    }

    public static void setLastDrawableImage(Drawable cardDrawable){
        lastImageDrawable = cardDrawable;
    }

    public static Drawable getLastImageDrawable() {
        return lastImageDrawable;
    }
    public static void setLastViewClicked(View layout){
        lastViewClick = layout;
    }
    public static View getLastViewClicked(){
        return lastViewClick;
    }
}