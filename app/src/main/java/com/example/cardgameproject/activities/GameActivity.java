package com.example.cardgameproject.activities;

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

import com.example.cardgameproject.adapters.CardGameAdapter;
import com.example.cardgameproject.R;
import com.example.cardgameproject.models.Card;
import com.example.cardgameproject.models.DAOUser;
import com.example.cardgameproject.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class represents the Game Activity in the Shonen Card game.
 * It displays the Game Board and controlls de Game Mechanics.
 */
public class GameActivity extends AppCompatActivity {
    //MainPlayer Hand
    private GridView gridMainPlayer;

    //Linear Layouts
    private LinearLayout gridRival;
    private LinearLayout gridBoardMain;
    private LinearLayout gridBoardRival;
    private LinearLayout layoutHPRival;
    private LinearLayout layoutHPMain;

    //Max quantity of cards in hand
    private static final int PLAYER_HAND_QTY = 3;
    private static Drawable lastImageDrawable;

    //Decks arrays
    private ArrayList<Card> mainPlayerDeck = new ArrayList<>();
    private final ArrayList<Card> rivalPlayerDeck = new ArrayList<>();
    private final ArrayList<Card> mainPlayerHand = new ArrayList<>();
    private final ArrayList<Card> rivalPlayerHand = new ArrayList<>();
    private ArrayList<Card> allCards;

    //Listeners
    private View.OnClickListener selectedCardListener;
    private View.OnClickListener selectedRivalCardListener;

    //Cards Actions
    private static Card lastCardDropped;
    private static View lastViewClick;
    private ImageView cardToAction;

    //Classes
    private User mainUser;
    private DAOUser daoUser;
    private Handler handler;

    //Your turn
    private boolean isTurn;


    /**
     * ONCREATE
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get references to some views in the layout
        gridRival = findViewById(R.id.grid_rival);
        gridMainPlayer = findViewById(R.id.grid_main_player);
        gridBoardMain = findViewById(R.id.board_main);
        gridBoardRival = findViewById(R.id.board_rival);
        layoutHPMain = findViewById(R.id.layout_hp_main);
        layoutHPRival = findViewById(R.id.layout_hp_rival);

        // Set the backgrounds for some views
        gridMainPlayer.setBackgroundResource(R.drawable.hand_main);
        gridBoardMain.setBackgroundResource(R.drawable.board_main);
        gridRival.setBackgroundResource(R.drawable.hand_rival);
        gridBoardRival.setBackgroundResource(R.drawable.board_rival);
        layoutHPRival.setBackgroundResource(R.color.black);
        layoutHPMain.setBackgroundResource(R.color.black);

        // Get some data from the intent that started this activity
        allCards = (ArrayList<Card>) getIntent().getSerializableExtra("allCards");
        mainUser = (User) getIntent().getSerializableExtra("mainuser");

        // Create a DAOUser instance for the current user and add a listener
        daoUser = new DAOUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        daoUser.addListener();

        // Get the main player's deck
        mainPlayerDeck = mainUser.getDeck();

        // Create a Handler instance to run some code after a delay
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createGame();
            }
        }, 1000);

        // Set a boolean flag to indicate whose turn it is
        isTurn = true;

        // Set a drag listener for the main player's board
        gridBoardMain.setOnDragListener((v, event) -> {
            // Check if it's the main player's turn
            if (isTurn) {
                // Handle the drag event
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
                        isTurn = false;
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

        //Set a click listener for the selected card
        selectedCardListener = v -> {
            v.setBackgroundResource(R.drawable.highlight);
            if (cardToAction != v) {
                if (cardToAction != null) {
                    cardToAction.setBackgroundResource(0);
                }
            }
            cardToAction = (ImageView) v;
        };

        //Set a click listener for the selected enemy card
        selectedRivalCardListener = v -> {
            if (isTurn) {
                cardMainPlayerFight(v);
                isTurn = false;
                handler.postDelayed(this::rivalPlay, 2000);
            }
        };

    }

    // This method is called when the rival plays its turn
    private void rivalPlay() {
        // Check if it's the player's turn, the rival and the player's boards are empty, and the game is not finished
        boolean gameFinished = rivalPlayAction();
        if (isTurn && gridBoardRival.getChildCount() == 0 && gridMainPlayer.getChildCount() == 0 && !gameFinished) {
            // Display a message to inform the player that their turn is skipped
            Toast.makeText(this, "You can't move, turn skipped!", Toast.LENGTH_SHORT).show();
            // Schedule the rival to play again after a 2-second delay
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rivalPlay();
                }
            }, 2000);
        }
    }

    // This method implements the rival's actions during its turn
    private boolean rivalPlayAction() {
        // Randomly select a movement for the rival
        int rivalMovement = (int) (Math.random() * 2);
        if (rivalMovement == 1 && gridBoardRival.getChildCount() > 0 && gridBoardMain.getChildCount() > 0) {
            // If the rival has cards on both boards, attack the player's card on the main board
            cardRivalPlayerFight();
        } else {
            if (gridRival.getChildCount() > 0) {
                // If the rival has cards on the hand, play a card on the rival board
                rivalDropCardPlay();
            } else if (gridBoardMain.getChildCount() > 0) {
                // If the rival has cards only on the main board, attack the player's card on the main board
                cardRivalPlayerFight();
            }
        }
        // Set the turn to the player's turn
        isTurn = true;
        // Check if the game is finished
        return checkPlayersDeck();
    }

    // This method is called when the player attacks a rival's card on the rival's board
    private void cardMainPlayerFight(View view) {
        for (int rivalCard = 0; rivalCard < gridBoardRival.getChildCount(); rivalCard++) {
            int rivalCardIndex = rivalCard;
            if (gridBoardRival.getChildAt(rivalCard) == view) {
                Card card = (Card) view.getTag();
                Card mainCard = (Card) cardToAction.getTag();
                // Play an attack animation
                attackAnimation(view);
                if (card.getHealth() - mainCard.getDamage() > 0) {
                    // If the rival's card is not destroyed, reduce its health by the player's card damage
                    card.setHealth(card.getHealth() - mainCard.getDamage());
                    TextView textHP = (TextView) layoutHPRival.getChildAt(rivalCardIndex);
                    textHP.setText("HP " + card.getHealth());
                } else {
                    // If the rival's card is destroyed, remove it from the rival's board and its health from the layout
                    card.setHealth(0);
                    gridBoardRival.removeView(view);
                    layoutHPRival.removeViewAt(rivalCardIndex);
                }
                // Check if the player's card is also destroyed
                for (int cardMain = 0; cardMain < gridBoardMain.getChildCount(); cardMain++) {
                    if (gridBoardMain.getChildAt(cardMain) == cardToAction) {
                        if (mainCard.getHealth() - card.getDamage() > 0) {
                            // If the player's card is not destroyed, reduce its health by the rival's card damage
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

    private void cardRivalPlayerFight() {
        // Checks if there are any cards on the rival player's board
        if (gridBoardRival.getChildCount() != 0) {
            // Selects a random card from the rival player's board
            int cardRivalPlayer = (int) (Math.random() * gridBoardRival.getChildCount());
            // Selects a random card from the main player's board
            int cardToFight = (int) (Math.random() * gridBoardMain.getChildCount());
            // Gets the Card object associated with the selected card from the rival player's board
            Card card = (Card) gridBoardRival.getChildAt(cardRivalPlayer).getTag();
            // Gets the Card object associated with the selected card from the main player's board
            Card mainCard = (Card) gridBoardMain.getChildAt(cardToFight).getTag();
            // Checks if the main player's card can survive the damage inflicted by the rival player's card
            if (mainCard.getHealth() - card.getDamage() > 0) {
                // If the main player's card survives, subtract the damage from its health
                mainCard.setHealth(mainCard.getHealth() - card.getDamage());
                // Updates the health text view associated with the main player's card
                TextView textHP = (TextView) layoutHPMain.getChildAt(cardToFight);
                textHP.setText("HP " + mainCard.getHealth());
                // Animates the attack on the main player's card
                attackAnimation(gridBoardMain.getChildAt(cardToFight));
            } else {
                // If the main player's card cannot survive, set its health to 0 and remove it from the board
                mainCard.setHealth(0);
                gridBoardMain.removeViewAt(cardToFight);
                layoutHPMain.removeViewAt(cardToFight);
            }
            // Checks if the rival player's card can survive the damage inflicted by the main player's card
            if (card.getHealth() - mainCard.getDamage() > 0) {
                // If the rival player's card survives, subtract the damage from its health
                card.setHealth(card.getHealth() - mainCard.getDamage());
                // Updates the health text view associated with the rival player's card
                TextView textHP = (TextView) layoutHPRival.getChildAt(cardRivalPlayer);
                textHP.setText("HP " + card.getHealth());
                // Animates the attack on the rival player's card
                attackAnimation(gridBoardRival.getChildAt(cardRivalPlayer));
            } else {
                // If the rival player's card cannot survive, set its health to 0 and remove it from the board
                card.setHealth(0);
                gridBoardRival.removeViewAt(cardRivalPlayer);
                layoutHPRival.removeViewAt(cardRivalPlayer);
            }
        }
    }

    private void dropCard() {
        // Creates an ImageView object to hold the image of the last card dropped by the player
        ImageView card = new ImageView(this);
        card.setImageDrawable(getLastImageDrawable());
        // Sets a click listener for the card
        card.setOnClickListener(selectedCardListener);
        // Sets the layout parameters for the card
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, gridBoardMain.getHeight() - 50, 50);
        layoutParams.setMargins(0, 20, 0, 0);
        card.setLayoutParams(layoutParams);
        // Sets the tag for the card as the last card dropped by the player
        card.setTag(getLastCardDropped());
        // Adds the card to the main player's board
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

    // This method selects a random card from the rival player's hand, creates an ImageView of it, and adds it to the game board
    private void rivalDropCardPlay() {
        Random r = new Random();
        int result = r.nextInt((rivalPlayerHand.size()));
        ImageView card = new ImageView(this);
        Picasso.with(this).load(rivalPlayerHand.get(result).getImageUrl()).into(card); // Uses Picasso library to load the image of the selected card into the ImageView
        card.setOnClickListener(selectedRivalCardListener); // Sets an OnClickListener for the ImageView
        card.setTag(rivalPlayerHand.get(result)); // Sets a tag for the ImageView with the card's data
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, gridBoardRival.getHeight() - 50, 50); // Sets the layout parameters for the ImageView
        layoutParams.setMargins(0, 20, 0, 0); // Sets the margins for the ImageView
        card.setLayoutParams(layoutParams);
        gridBoardRival.addView(card); // Adds the ImageView to the game board
        createHealthCard(layoutHPRival, String.valueOf(rivalPlayerHand.get(result).getHealth())); // Creates a health card for the selected card and adds it to the game board
        rivalPlayerHand.remove(result);

        // If the rival player's deck still has cards, it adds the top card to the hand and removes it from the deck
        if (rivalPlayerDeck.size() > 0) {
            rivalPlayerHand.add(rivalPlayerDeck.get(0));
            rivalPlayerDeck.remove(0);
        }
        // If the rival player's hand has less than 3 cards, it removes the leftmost card from the hand
        if (rivalPlayerHand.size() < 3) {
            gridRival.removeViewAt(0);
        }
    }

    // This method sets up the game by shuffling the player's deck, creating the rival player's deck and hand, and displaying the player's hand
    public void createGame() {

        // Shuffles the player's deck and creates the rival player's deck
        Collections.shuffle(mainPlayerDeck);
        createRivalDeck(mainPlayerDeck.size());

        // Adds the top PLAYER_HAND_QTY cards from the player's deck to the player's hand
        for (int i = 0; i < PLAYER_HAND_QTY; i++) {
            mainPlayerHand.add(mainPlayerDeck.get(i));
            mainPlayerDeck.remove(i);
        }

        // Creates an adapter for the player's hand and sets it to the player's hand grid
        CardGameAdapter cardAdapter = new CardGameAdapter(this, mainPlayerHand, gridMainPlayer.getHeight(), (gridMainPlayer.getWidth() / 4));
        gridMainPlayer.setAdapter(cardAdapter);

        // Adds the top PLAYER_HAND_QTY cards from the rival player's deck to the rival player's hand and creates ImageViews to represent them in the rival player's hand grid
        for (int i = 0; i < PLAYER_HAND_QTY; i++) {
            rivalPlayerHand.add(rivalPlayerDeck.get(i));
            rivalPlayerDeck.remove(i);
        }
        for (int i = 0; i < PLAYER_HAND_QTY; i++) {
            ImageView cardRivalHand = new ImageView(this);
            cardRivalHand.setImageResource(R.drawable.rival_card);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((gridRival.getWidth() / 4), gridRival.getHeight());
            layoutParams.setMargins(((gridRival.getWidth() / 4) / 6), 0, ((gridRival.getWidth() / 4) / 6), 0);
            cardRivalHand.setLayoutParams(layoutParams);
            gridRival.addView(cardRivalHand);
        }
    }

    private void createRivalDeck(int mainPlayerDeckSize) {
        // Shuffle allCards list to randomize the order of the cards.
        Collections.shuffle(allCards);
        // Add a specified number of cards to the rival player's deck.
        for (int i = 0; i < mainPlayerDeckSize; i++) {
            // Add the selected cards to the rival player's deck.
            rivalPlayerDeck.add(allCards.get(i));
        }
    }

    private void createHealthCard(LinearLayout layout, String cardHP) {
        // Create a new TextView to display the health card information.
        TextView hpCard = new TextView(this);
        // Set the text for the health card.
        hpCard.setText("HP " + cardHP);
        // Center-align the text.
        hpCard.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        // Set the layout parameters for the TextView.
        LinearLayout.LayoutParams layoutParamsHP = new LinearLayout.LayoutParams(200, layout.getHeight(), 50);
        // Apply the layout parameters to the TextView.
        hpCard.setLayoutParams(layoutParamsHP);
        // Add the TextView to the health card layout.
        layout.addView(hpCard);
    }

    private void attackAnimation(View cardRecieveAttack) {
        ImageView v = (ImageView) cardRecieveAttack; // Create an image for the card receiving the attack.
        final int redColor = getResources().getColor(android.R.color.holo_red_light); // Get the red color for the animation.
        ValueAnimator colorAnimator = ObjectAnimator.ofFloat(0f, 1f); // Create a color animation object.
        // Add an animation update listener.
        colorAnimator.addUpdateListener(animation -> { // Method to update the animation.
            float mul = (Float) animation.getAnimatedValue(); // Get the current animation value.
            int alphaOrange = adjustAlpha(redColor, mul); // Adjust the transparency value for the animation.
            v.setColorFilter(alphaOrange, PorterDuff.Mode.SRC_ATOP); // Apply the color filter to the card image.
            if (mul == 0.0) { // If the animation value is 0, then remove the color filter.
                v.setColorFilter(null);
            }
        });

        colorAnimator.setDuration(1000); // Set the duration of the animation.
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE); // Set the repeat mode of the animation.
        colorAnimator.setRepeatCount(1); // Set the number of repetitions of the animation.
        colorAnimator.start(); // Start the animation.
    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor); // Calculate the new alpha value based on the factor and the original alpha value.
        int red = Color.red(color); // Get the red component of the original color.
        int green = Color.green(color); // Get the green component of the original color.
        int blue = Color.blue(color); // Get the blue component of the original color.
        return Color.argb(alpha, red, green, blue); // Return the new color with the adjusted alpha value.
    }

    public void winGame() {
        int numCard = (int) (Math.random() * (allCards.size())); // Choose a random card from the list of all cards.
        int numFragments = (int) (Math.random() * (5 - 1) + 1); // Choose a random number of fragments between 1 and 4.

        daoUser.updateCardFragments(allCards.get(numCard).getName(), numFragments).addOnSuccessListener(suc -> { // Update the user's card fragments in the database.
            String message = "You have been rewarded with " + numFragments + " fragments of " + allCards.get(numCard).getName(); // Create a message to display in the reward alert dialog.
            alertDialogReward(message); // Show an alert dialog to inform the user of the reward.
        }).addOnFailureListener(e -> { // If the update fails, show an alert dialog with an error message.
            String message = "An unknown error occurred";
            alertDialogReward(message);
        });
    }

    public void alertDialogReward(String message) {
        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this); // Create a new alert dialog builder.
        createAccountBuilder.setTitle("Win Reward"); // Set the title of the alert dialog.
        createAccountBuilder.setMessage(message); // Set the message of the alert dialog.
        createAccountBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() { // Set a button to close the alert dialog.
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish(); // Finish the activity.
            }
        });

        createAccountBuilder.show(); // Show the alert dialog.
    }

    public void loseTieGame(String message, String title) {
        alertDialogLose(message, title); // Show an alert dialog with the specified message and title.
    }

    public void alertDialogLose(String message, String title) {
        /* Creates and shows an alert dialog with a given message and title */
        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
        createAccountBuilder.setTitle(title);
        createAccountBuilder.setMessage(message);
        createAccountBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish(); // Finishes the activity
            }
        });

        createAccountBuilder.show(); // Shows the alert dialog
    }

    private boolean checkPlayersDeck() {
        /* Checks if either player has an empty deck and performs the corresponding action */
        if ((gridRival.getChildCount() == 0 && gridBoardRival.getChildCount() == 0) && (gridMainPlayer.getChildCount() == 0 && gridBoardMain.getChildCount() == 0)) {
            loseTieGame("You tie the Game", "Tie"); // Ends the game in a tie
            return true;
        } else if (gridRival.getChildCount() == 0 && gridBoardRival.getChildCount() == 0) {
            winGame(); // Ends the game with a win for the player
            return true;
        } else if (gridMainPlayer.getChildCount() == 0 && gridBoardMain.getChildCount() == 0) {
            loseTieGame("You lose the Game", "Lose"); // Ends the game with a loss for the player
            return true;
        }
        return false; // Returns false if no action was taken
    }

    public static void setLastCard(Card card, Drawable cardDrawable) {
        // Sets the last card that was dropped onto the game board, along with its drawable representation
        lastCardDropped = card;
        lastImageDrawable = cardDrawable;
    }

    private Drawable getLastImageDrawable() {
        // Returns the drawable representation of the last card that was dropped onto the game board
        return lastImageDrawable;
    }

    private Card getLastCardDropped() {
        // Returns the last card that was dropped onto the game board
        return lastCardDropped;
    }

    public static void setLastViewClicked(View layout) {
        // Sets the last view that was clicked
        lastViewClick = layout;
    }

    private View getLastViewClicked() {
        // Returns the last view that was clicked
        return lastViewClick;
    }
}