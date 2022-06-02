package com.example.cardgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
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

public class GameActivity extends AppCompatActivity {
    private GridView gridRival;
    private GridView gridMainPlayer;
    private LinearLayout gridBoardMain;
    private LinearLayout gridBoardRival;
    private static final int PLAYER_HAND_QTY = 3;
    private static Drawable lastImageDrawable;
    private static ArrayList<Card> mainPlayerDeck = MenuActivity.cards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gridRival = findViewById(R.id.grid_rival);
        gridMainPlayer = findViewById(R.id.grid_main_player);
        gridBoardMain = findViewById(R.id.board_main);
        gridBoardRival = findViewById(R.id.board_rival);


        createGame();

        /*gridMainPlayer.setOnItemClickListener((adapterView, view, position, l) -> {
            if (gridMainPlayer.findViewById(R.id.grid_cards) instanceof ImageView) {
                ImageView card = gridMainPlayer.findViewById(R.id.grid_cards);
                card.setOnTouchListener((view1, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view1);
                        view1.startDrag(data, shadowBuilder, view1, 0);
                        view1.setVisibility(View.INVISIBLE);
                        return true;
                    } else {
                        return false;
                    }
                });


            }

        });*/

        gridBoardMain.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:

                    break;
                case DragEvent.ACTION_DRAG_EXITED:

                    break;
                case DragEvent.ACTION_DROP:
                    ImageView card = new ImageView(this);
                    card.setImageDrawable(getLastImageDrawable());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 200, gridBoardMain.getHeight(), 100);
                    card.setLayoutParams(layoutParams);
                    gridBoardMain.addView(card);
                    // Dropped, reassign View to ViewGroup
                    /*View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);*/
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }
            return true;
        });


        /*gridMainPlayer.setOnDragListener((v, event) -> {
            switch(event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundResource(R.drawable.logo);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundResource(R.drawable.background_game);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundResource(R.drawable.background_game);
                    break;
                default:
            }
            return true;
        });*/
    }

    public void createGame(){
        //TODO - Recoger nombre de usuario de la base de datos para añadirlo al Main Player
        MainPlayer mainPlayer = new MainPlayer("Albert", 100);
        //ArrayList<Card> mainPlayerDeck = mainPlayer.getPlayerDeck();
        ArrayList<ImageView> imageCards = new ArrayList<>();

        ArrayList<Card> mainPlayerHand = new ArrayList<>();
        ArrayList<Card> rivalPlayerHand = new ArrayList<>();
        Collections.shuffle(mainPlayerDeck);
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            mainPlayerHand.add(mainPlayerDeck.get(i));
            mainPlayerDeck.remove(i);
        }
       /* for (Card card : mainPlayerHand){
            ImageView imageCard = new ImageView(this);
            imageCard.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT));
            Picasso.with(this).load(card.getImageUrl()).into(imageCard);
            imageCards.add(imageCard);
            gridMainPlayer.addView(imageCard);
        }*/
        CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand);
        gridMainPlayer.setAdapter(cardAdapter);
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            rivalPlayerHand.add(mainPlayerDeck.get(i));

        }
    }

    public static void setLastDrawableImage(Drawable cardDrawable){
        lastImageDrawable = cardDrawable;
    }

    public static Drawable getLastImageDrawable() {
        return lastImageDrawable;
    }

    public static Card getFirstCardDeck(){
        Card firstCard = mainPlayerDeck.get(0);
        mainPlayerDeck.remove(0);
        return firstCard;
    }
}