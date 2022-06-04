package com.example.cardgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private GridView gridMainPlayer;
    private LinearLayout gridRival;
    private LinearLayout gridBoardMain;
    private LinearLayout gridBoardRival;
    private LinearLayout layoutHPRival;
    private LinearLayout layoutHPMain;
    private static final int PLAYER_HAND_QTY = 3;
    private static Drawable lastImageDrawable;
    private ArrayList<Card> mainPlayerDeck = new ArrayList<>();
    private ArrayList<Card> rivalPlayerDeck = MenuActivity.cards;
    private ArrayList<Card> mainPlayerHand = new ArrayList<>();
    private ArrayList<Card> rivalPlayerHand = new ArrayList<>();
    private View.OnClickListener selectedCardListener;
    private View.OnClickListener selectedRivalCardListener;
    private ImageView cardToAction;
    private static View lastViewClick;
    private int dropedCardCount = 0;
    private User mainUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gridRival = findViewById(R.id.grid_rival);
        gridMainPlayer = findViewById(R.id.grid_main_player);
        gridBoardMain = findViewById(R.id.board_main);
        gridBoardRival = findViewById(R.id.board_rival);
        layoutHPMain = findViewById(R.id.layout_hp_main);
        layoutHPRival = findViewById(R.id.layout_hp_rival);

        gridMainPlayer.setBackgroundResource(R.drawable.hand_main);
        gridBoardMain.setBackgroundResource(R.drawable.board_main);
        gridRival.setBackgroundResource(R.drawable.hand_rival);
        gridBoardRival.setBackgroundResource(R.drawable.board_rival);

        mainUser = (User) getIntent().getSerializableExtra("mainuser");
        mainPlayerDeck = mainUser.getDeck();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                createGame();
            }
        }, 1000);


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
                    v.setBackgroundResource(R.drawable.board_main);
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

        selectedCardListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.highlight);
                if(cardToAction != v) {
                    if (cardToAction != null){
                        cardToAction.setBackgroundResource(0);
                    }
                }
                cardToAction = (ImageView) v;
            }
        };

        selectedRivalCardListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFight(v);
            }
        };

    }

    private void cardFight(View view){
        Random coinflip = new Random();
        int result = coinflip.nextInt(2);
        if(result == 0){
            view.setVisibility(View.GONE);
            cardToAction.setBackgroundResource(0);
        } else {
            for(int i = 0; i < gridBoardMain.getChildCount(); i++){
                if(gridBoardMain.getChildAt(i) == cardToAction){
                    layoutHPMain.getChildAt(i).setVisibility(View.GONE);
                }
            }
            cardToAction.setVisibility(View.GONE);
        }
        Toast.makeText(this ,"Rival Win!", Toast.LENGTH_SHORT).show();
    }

    private void dropCard() {
        ImageView card = new ImageView(this);
        card.setImageDrawable(getLastImageDrawable());
        card.setOnClickListener(selectedCardListener);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 200, gridBoardMain.getHeight() - 50, 50);
        layoutParams.setMargins(0,20,0,0);
        card.setLayoutParams(layoutParams);
        card.setTag("IMGC" + dropedCardCount);
        dropedCardCount++;
        gridBoardMain.addView(card);
        TextView hpCard = new TextView(this);
        hpCard.setText("HP");
        hpCard.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams layoutParamsHP = new LinearLayout.LayoutParams( 200, layoutHPMain.getHeight(), 50);
        hpCard.setLayoutParams(layoutParamsHP);
        layoutHPMain.addView(hpCard);
        for (int x = 0; x < gridMainPlayer.getChildCount(); x++) {
            if (gridMainPlayer.getChildAt(x).getTag() == getLastViewClicked().getTag()) {
                mainPlayerHand.remove(x);
            }
        }
        if (mainPlayerDeck.size() > 0) {
            mainPlayerHand.add(mainPlayerDeck.get(0));
            mainPlayerDeck.remove(0);
            CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand, gridMainPlayer.getHeight());
            gridMainPlayer.setAdapter(cardAdapter);
        }
    }

    private void rivalPlay(){
        Random r = new Random();
        int result = r.nextInt((rivalPlayerHand.size()));
        ImageView card = new ImageView(this);
        Picasso.with(this).load(rivalPlayerHand.get(result).getImageUrl()).into(card);
        card.setOnClickListener(selectedRivalCardListener);
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

        CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand, gridMainPlayer.getHeight());
        gridMainPlayer.setAdapter(cardAdapter);
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            rivalPlayerHand.add(rivalPlayerDeck.get(i));

        }
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            ImageView cardRivalHand = new ImageView(this);
            cardRivalHand.setImageResource(R.drawable.rival_card);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 200, gridRival.getHeight());
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