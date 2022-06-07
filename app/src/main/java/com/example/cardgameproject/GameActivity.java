package com.example.cardgameproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
    private ArrayList<Card> rivalPlayerDeck = new ArrayList<>();
    private ArrayList<Card> mainPlayerHand = new ArrayList<>();
    private ArrayList<Card> rivalPlayerHand = new ArrayList<>();
    private ArrayList<Card> allCards;
    private View.OnClickListener selectedCardListener;
    private View.OnClickListener selectedRivalCardListener;
    private static Card lastCardDropped;
    private ImageView cardToAction;
    private static View lastViewClick;
    private int dropedCardCount = 0;
    private User mainUser;
    private DAOUser daoUser;
    private Handler handler;
    private boolean turn;

    private AnimatorSet cardAttackSet;

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

        layoutHPRival.setBackgroundResource(R.color.black);
        layoutHPMain.setBackgroundResource(R.color.black);

        allCards = (ArrayList<Card>) getIntent().getSerializableExtra("allCards");
        mainUser = (User) getIntent().getSerializableExtra("mainuser");
        daoUser = new DAOUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        daoUser.addListener();
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
                    case DragEvent.ACTION_DRAG_ENTERED:
                        PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(Color.argb(255, 255, 255, (float) 255), PorterDuff.Mode.MULTIPLY);
                        v.getBackground().setColorFilter(greyFilter);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.getBackground().setColorFilter(null);
                        break;
                    case DragEvent.ACTION_DROP:
                        v.getBackground().setColorFilter(null);
                        dropCard();
                        turn = false;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rivalPlay();
                            }
                        }, 2000);
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
                    //checkPlayersDeck();
                    turn = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rivalPlay();
                        }
                    }, 2000);
                }
            }
        };

    }

    private void rivalPlay(){
        boolean gameFinished = rivalPlayAction();
        if(turn && gridBoardRival.getChildCount() == 0 && gridMainPlayer.getChildCount() == 0 && !gameFinished){
            Toast.makeText(this, "You can't move, turn skipped!", Toast.LENGTH_SHORT).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rivalPlay();
                }
            }, 2000);
        }
    }

    private boolean rivalPlayAction(){
        int rivalMovement = (int) (Math.random() * 2);
        if (rivalMovement == 1 && gridBoardRival.getChildCount() > 0 && gridBoardMain.getChildCount() > 0) {
            cardRivalPlayerFight();
        } else {
            if (gridRival.getChildCount() > 0) {
                rivalDropCardPlay();
            }else if (gridBoardMain.getChildCount() > 0){
                cardRivalPlayerFight();
            }
        }
        turn = true;
        return checkPlayersDeck();
    }

    private void cardMainPlayerFight(View view){
        for(int rivalCard = 0; rivalCard < gridBoardRival.getChildCount(); rivalCard++){
            int rivalCardIndex = rivalCard;
            if(gridBoardRival.getChildAt(rivalCard) == view){
                Card card = (Card) view.getTag();
                Card mainCard = (Card) cardToAction.getTag();
                attackAnimation(view);
                if(card.getHealth() - mainCard.getDamage() > 0) {
                    card.setHealth(card.getHealth() - mainCard.getDamage());
                    TextView textHP = (TextView) layoutHPRival.getChildAt(rivalCardIndex);
                    textHP.setText("HP " + card.getHealth());
                } else {
                    card.setHealth(0);
                    gridBoardRival.removeView(view);
                    layoutHPRival.removeViewAt(rivalCardIndex);
                }
                for (int cardMain = 0; cardMain < gridBoardMain.getChildCount(); cardMain++) {
                    if (gridBoardMain.getChildAt(cardMain) == cardToAction) {
                        if (mainCard.getHealth() - card.getDamage() > 0) {
                            mainCard.setHealth(mainCard.getHealth() - card.getDamage());
                            TextView textHP = (TextView) layoutHPMain.getChildAt(cardMain);
                            textHP.setText("HP " + mainCard.getHealth());
                            attackAnimation(cardToAction);
                        } else {
                            mainCard.setHealth(0);
                            gridBoardMain.removeView(cardToAction);
                            layoutHPMain.removeViewAt(cardMain);
                        }
                    }
                }
            }
        }
    }

    private void cardRivalPlayerFight(){
        if(gridBoardRival.getChildCount() != 0) {
            int cardRivalPlayer = (int) (Math.random() * gridBoardRival.getChildCount());
            int cardToFight = (int) (Math.random() * gridBoardMain.getChildCount());
            Card card = (Card) gridBoardRival.getChildAt(cardRivalPlayer).getTag();
            Card mainCard = (Card) gridBoardMain.getChildAt(cardToFight).getTag();
            if (mainCard.getHealth() - card.getDamage() > 0) {
                mainCard.setHealth(mainCard.getHealth() - card.getDamage());
                TextView textHP = (TextView) layoutHPMain.getChildAt(cardToFight);
                textHP.setText("HP " + mainCard.getHealth());
                attackAnimation(gridBoardMain.getChildAt(cardToFight));
            } else {
                mainCard.setHealth(0);
                gridBoardMain.removeViewAt(cardToFight);
                layoutHPMain.removeViewAt(cardToFight);
            }

            if (card.getHealth() - mainCard.getDamage() > 0) {
                card.setHealth(card.getHealth() - mainCard.getDamage());
                TextView textHP = (TextView) layoutHPRival.getChildAt(cardRivalPlayer);
                textHP.setText("HP " + card.getHealth());
                attackAnimation(gridBoardRival.getChildAt(cardRivalPlayer));
            } else {
                card.setHealth(0);
                gridBoardRival.removeViewAt(cardRivalPlayer);
                layoutHPRival.removeViewAt(cardRivalPlayer);
            }
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
        getLastViewClicked().setVisibility(View.GONE);
        for (int x = 0; x < gridMainPlayer.getChildCount(); x++) {
            if (gridMainPlayer.getChildAt(x) == getLastViewClicked()) {
                mainPlayerHand.remove(x);
            }
        }
        if (mainPlayerDeck.size() > 0) {
            mainPlayerHand.add(mainPlayerDeck.get(0));
            mainPlayerDeck.remove(0);
        }
        CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand, gridMainPlayer.getHeight(), (gridMainPlayer.getWidth() / 4));
        gridMainPlayer.setAdapter(cardAdapter);
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
            gridRival.removeViewAt(0);
        }
    }

    public void createGame(){

        Collections.shuffle(mainPlayerDeck);
        createRivalDeck(mainPlayerDeck.size());
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
            layoutParams.setMargins(((gridRival.getWidth() / 4) /6),0,((gridRival.getWidth() / 4) /6),0);
            cardRivalHand.setLayoutParams(layoutParams);
            gridRival.addView(cardRivalHand);
        }
    }

    private void createRivalDeck(int mainPlayerDeckSize){
        Collections.shuffle(allCards);
        for(int i = 0; i < mainPlayerDeckSize; i++) {
            rivalPlayerDeck.add(allCards.get(i));
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

    private void attackAnimation(View cardRecieveAttack){
        ImageView v = (ImageView) cardRecieveAttack;
        final int redColor = getResources().getColor(android.R.color.holo_red_light);
        ValueAnimator colorAnimator = ObjectAnimator.ofFloat(0f, 1f);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float mul = (Float) animation.getAnimatedValue();
                int alphaOrange = adjustAlpha(redColor, mul);
                v.setColorFilter(alphaOrange, PorterDuff.Mode.SRC_ATOP);
                if (mul == 0.0) {
                    v.setColorFilter(null);
                }
            }
        });

        colorAnimator.setDuration(1000);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.setRepeatCount(1);
        colorAnimator.start();
    }
    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public void winGame(){
        int numCard = (int) (Math.random() * (allCards.size()));
        int numFragments = (int) (Math.random() * (5 - 1)+1);

        daoUser.updateCardFragments(allCards.get(numCard).getName(), numFragments).addOnSuccessListener(suc -> {
            String message = "You have been rewarded with " + numFragments + " fragments of " + allCards.get(numCard).getName();
            alertDialogReward(message);
        }).addOnFailureListener(e -> {
            String message = "An unknown error ocurred";
            alertDialogReward(message);
        });
    }

    public void alertDialogReward(String message){
        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
        createAccountBuilder.setTitle("Win Reward");
        createAccountBuilder.setMessage(message);
        createAccountBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        createAccountBuilder.show();
    }

    public void loseTieGame(String message, String title){
        alertDialogLose(message, title);
    }

    public void alertDialogLose(String message, String title){
        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
        createAccountBuilder.setTitle(title);
        createAccountBuilder.setMessage(message);
        createAccountBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        createAccountBuilder.show();
    }

    private boolean checkPlayersDeck(){
        if((gridRival.getChildCount() == 0 && gridBoardRival.getChildCount() == 0) && (gridMainPlayer.getChildCount() == 0 && gridBoardMain.getChildCount() == 0)){
            loseTieGame("You tie the Game", "Tie");
            return true;
        } else if(gridRival.getChildCount() == 0 && gridBoardRival.getChildCount() == 0){
            winGame();
            return true;
        } else if(gridMainPlayer.getChildCount() == 0 && gridBoardMain.getChildCount() == 0){
            loseTieGame("You lose the Game", "Lose");
            return true;
        }
        return false;
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