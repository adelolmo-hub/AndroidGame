package com.example.cardgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
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
    private static LinearLayout gridBoardMain;
    private LinearLayout gridBoardRival;
    private static final int PLAYER_HAND_QTY = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gridRival = findViewById(R.id.grid_rival);
        gridMainPlayer = findViewById(R.id.grid_main_player);
        gridBoardMain = findViewById(R.id.board_main);


        createGame();
        /*gridMainPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(gridMainPlayer.getChildAt(position) instanceof ImageView) {
                    ImageView card = (ImageView) gridMainPlayer.getChildAt(position);
                    card.setOnLongClickListener(new View.OnLongClickListener(){

                        @Override
                        public boolean onLongClick(View view) {
                            ImageView hola = (ImageView) view;
                            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                            ClipData dragData = new ClipData(
                                    (CharSequence) view.getTag(),
                                    new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                                    item);
                            View.DragShadowBuilder myShadow = new DragShadowCardBuilder(card);
                            view.startDragAndDrop(dragData,  // The data to be dragged
                                    myShadow,  // The drag shadow builder
                                    null,      // No need to use local data
                                    0          // Flags (not currently used, set to 0)
                            );
                            return true;
                        }
                    });
                }

            }
        });*/
        /*gridMainPlayer.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch(event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // code block
                        v.get
                        ImageView imageView = new ImageView(v.getContext());
                        imageView.setImageResource(R.drawable.button_rounded);
                        imageView.setMaxHeight(145);
                        imageView.setMaxWidth(100);
                        imageView.setMinimumHeight(145);
                        imageView.setMinimumWidth(100);
                        gridBoardMain.addView(imageView);
                        String hola = "hola";
                        break;

                    case DragEvent.ACTION_DROP:
                        // code block
                        gridBoardMain.addView(v);
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        // code block
                        String w = "hola";
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        // code block
                        String r = "hola";
                        break;
                }

                return false;
            }
        });*/


    }

    public void createGame(){
        //TODO - Recoger nombre de usuario de la base de datos para añadirlo al Main Player
        MainPlayer mainPlayer = new MainPlayer("Albert", 100);
        //ArrayList<Card> mainPlayerDeck = mainPlayer.getPlayerDeck();
        ArrayList<ImageView> imageCards = new ArrayList<>();
        ArrayList<Card> mainPlayerDeck = MenuActivity.cards;
        ArrayList<Card> mainPlayerHand = new ArrayList<>();
        ArrayList<Card> rivalPlayerHand = new ArrayList<>();
        Collections.shuffle(mainPlayerDeck);
        for(int i = 0; i < PLAYER_HAND_QTY; i++) {
            mainPlayerHand.add(mainPlayerDeck.get(i));
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

    public static LinearLayout getMainLayout(){
        return gridBoardMain;
    }
}