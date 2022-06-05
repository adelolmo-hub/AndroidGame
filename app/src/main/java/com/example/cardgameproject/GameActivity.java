package com.example.cardgameproject;

import androidx.appcompat.app.AlertDialog;
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
    private static Card lastCardDropped;
    private ImageView cardToAction;
    private static View lastViewClick;
    private int dropedCardCount = 0;
    private User mainUser;
    //private DAOUser daoUser;
    private Handler handler;
    private boolean turn;
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
        //daoUser = (DAOUser) getIntent().getSerializableExtra("daouser");
        mainPlayerDeck = mainUser.getDeck();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                createGame();
            }
        }, 1000);

        turn = true;

        gridBoardMain.setOnDragListener((v, event) -> {
            if (turn) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundColor(Color.argb(255, 255, 255, (float) 0.20));
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundResource(R.drawable.board_main);
                        break;
                    case DragEvent.ACTION_DROP:
                        v.setBackgroundResource(R.drawable.board_main);
                        dropCard();
                        turn = false;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int rivalMovement = (int) (Math.random() * 2);
                                if (rivalMovement == 1 && gridBoardRival.getChildCount() > 0) {
                                    cardRivalPlayerFight();
                                } else {
                                    if (gridRival.getChildCount() > 0) {
                                        rivalDropCardPlay();
                                    }else {
                                        cardRivalPlayerFight();
                                    }
                                }
                                turn = true;
                            }
                        }, 3000);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
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
                if(turn) {
                    cardMainPlayerFight(v);
                    turn = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int rivalMovement = (int) (Math.random() * 2);
                            if (rivalMovement == 1 && gridBoardRival.getChildCount() > 0) {
                                cardRivalPlayerFight();
                            } else {
                                if (gridRival.getChildCount() > 0) {
                                    rivalDropCardPlay();
                                }else {
                                    cardRivalPlayerFight();
                                }
                            }
                            turn = true;
                        }
                    }, 3000);
                }
            }
        };

    }

    private void cardMainPlayerFight(View view){
        for(int rivalCard = 0; rivalCard < gridBoardRival.getChildCount(); rivalCard++){
            if(gridBoardRival.getChildAt(rivalCard) == view){
                Card card = (Card) view.getTag();
                Card mainCard = (Card) cardToAction.getTag();
                if(card.getHealth() - mainCard.getDamage() > 0) {
                    card.setHealth(card.getHealth() - mainCard.getDamage());
                    TextView textHP = (TextView) layoutHPRival.getChildAt(rivalCard);
                    textHP.setText("HP " + card.getHealth());
                } else {
                    card.setHealth(0);
                    view.setVisibility(View.GONE);
                    layoutHPRival.getChildAt(rivalCard).setVisibility(View.GONE);
                }
                for (int cardMain = 0; cardMain < gridBoardMain.getChildCount(); cardMain++) {
                    if (gridBoardMain.getChildAt(cardMain) == cardToAction) {
                        if (mainCard.getHealth() - card.getDamage() > 0) {
                            mainCard.setHealth(mainCard.getHealth() - card.getDamage());
                            TextView textHP = (TextView) layoutHPMain.getChildAt(cardMain);
                            textHP.setText("HP " + mainCard.getHealth());
                        } else {
                            mainCard.setHealth(0);
                            cardToAction.setVisibility(View.GONE);
                            layoutHPMain.getChildAt(cardMain).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    private void cardRivalPlayerFight(){
        int cardRivalPlayer = (int) (Math.random() * gridBoardRival.getChildCount());
        int cardToFight = (int) (Math.random() * gridBoardMain.getChildCount());
        Card card = (Card) gridBoardRival.getChildAt(cardRivalPlayer).getTag();
        Card mainCard = (Card) gridBoardMain.getChildAt(cardToFight).getTag();
        if(mainCard.getHealth() - card.getDamage() > 0) {
            card.setHealth(mainCard.getHealth() - card.getDamage());
            TextView textHP = (TextView) layoutHPMain.getChildAt(cardToFight);
            textHP.setText("HP " + mainCard.getHealth());
        } else {
            card.setHealth(0);
            gridBoardMain.getChildAt(cardToFight).setVisibility(View.GONE);
            layoutHPMain.getChildAt(cardToFight).setVisibility(View.GONE);
        }

        if(card.getHealth() - mainCard.getDamage() > 0) {
            mainCard.setHealth(card.getHealth() - mainCard.getDamage());
            TextView textHP = (TextView) layoutHPRival.getChildAt(cardRivalPlayer);
            textHP.setText("HP " + mainCard.getHealth());
        } else {
            card.setHealth(0);
            gridBoardRival.getChildAt(cardRivalPlayer).setVisibility(View.GONE);
            layoutHPRival.getChildAt(cardRivalPlayer).setVisibility(View.GONE);
        }

    }

    private void dropCard() {
        ImageView card = new ImageView(this);
        card.setImageDrawable(getLastImageDrawable());
        card.setOnClickListener(selectedCardListener);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 200, gridBoardMain.getHeight() - 50, 50);
        layoutParams.setMargins(0,20,0,0);
        card.setLayoutParams(layoutParams);
        card.setTag(getLastCardDropped());
        dropedCardCount++;
        gridBoardMain.addView(card);
        createHealthCard(layoutHPMain, String.valueOf(getLastCardDropped().getHealth()));

        for (int x = 0; x < gridMainPlayer.getChildCount(); x++) {
            if (gridMainPlayer.getChildAt(x) == getLastViewClicked()) {
                mainPlayerHand.remove(x);
            }
        }
        if (mainPlayerDeck.size() > 0) {
            mainPlayerHand.add(mainPlayerDeck.get(0));
            mainPlayerDeck.remove(0);
            CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand, gridMainPlayer.getHeight(), (gridMainPlayer.getWidth() / 4));
            gridMainPlayer.setAdapter(cardAdapter);
        }
        getLastViewClicked().setVisibility(View.GONE);
    }

    private void rivalDropCardPlay(){
        Random r = new Random();
        int result = r.nextInt((rivalPlayerHand.size()));
        ImageView card = new ImageView(this);
        Picasso.with(this).load(rivalPlayerHand.get(result).getImageUrl()).into(card);
        card.setOnClickListener(selectedRivalCardListener);
        card.setTag(rivalPlayerHand.get(result));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 200, gridBoardRival.getHeight() - 50, 50);
        layoutParams.setMargins(0,20,0,0);
        card.setLayoutParams(layoutParams);
        gridBoardRival.addView(card);
        createHealthCard(layoutHPRival, String.valueOf(rivalPlayerHand.get(result).getHealth()));
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

        CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand, gridMainPlayer.getHeight(), (gridMainPlayer.getWidth() / 4));
        gridMainPlayer.setAdapter(cardAdapter);
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            rivalPlayerHand.add(rivalPlayerDeck.get(i));
            rivalPlayerDeck.remove(i);
        }
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            ImageView cardRivalHand = new ImageView(this);
            cardRivalHand.setImageResource(R.drawable.rival_card);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( (gridRival.getWidth() / 4), gridRival.getHeight());
            layoutParams.setMargins(30,0,30,0);
            cardRivalHand.setLayoutParams(layoutParams);
            gridRival.addView(cardRivalHand);
        }
    }

    private void createHealthCard(LinearLayout layout, String cardHP){
        TextView hpCard = new TextView(this);
        hpCard.setText("HP " + cardHP);
        hpCard.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams layoutParamsHP = new LinearLayout.LayoutParams( 200, layout.getHeight(), 50);
        hpCard.setLayoutParams(layoutParamsHP);
        layout.addView(hpCard);
    }

    /*public void winGame(View view){
        int numCard = (int) (Math.random() * (mainUser.getDeck().size()));
        int numFragments = (int) (Math.random() * (5 - 1)+1);

        daoUser.updateCardFragments(mainUser.getDeck().get(numCard).getName(), numFragments).addOnSuccessListener(suc -> {
            String message = "You have been rewarded with " + numFragments + " fragments of " + mainUser.getDeck().get(numCard).getName();
            alertDialogReward(message);
        }).addOnFailureListener(e -> {
            String message = "An unknown error ocurred";
            alertDialogReward(message);
        });

    }*/

    public void alertDialogReward(String message){
        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
        createAccountBuilder.setTitle("Reward");
        createAccountBuilder.setMessage(message);
        createAccountBuilder.setPositiveButton("Ok", null);

        createAccountBuilder.show();
    }

    public static void setLastCard(Card card, Drawable cardDrawable){
        lastCardDropped = card;
        lastImageDrawable = cardDrawable;
    }

    private Drawable getLastImageDrawable() {
        return lastImageDrawable;
    }
    private Card getLastCardDropped(){
        return lastCardDropped;
    }
    public static void setLastViewClicked(View layout){
        lastViewClick = layout;
    }
    private View getLastViewClicked(){
        return lastViewClick;
    }
}