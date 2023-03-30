package com.example.cardgameproject.adapters;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardgameproject.R;
import com.example.cardgameproject.models.Card;
import com.example.cardgameproject.models.DAOUser;
import com.example.cardgameproject.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context context;
    ArrayList<Card> cards;
    ArrayList<Card> deck;
    LayoutInflater inflater;
    User user;
    ColorMatrix matrix = new ColorMatrix();
    ColorMatrixColorFilter colorFilter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DAOUser daoUser;

    Card card;

    public GridAdapter(Context context, ArrayList<Card> cards, User user) {
        this.context = context;
        this.cards = cards;
        this.user = user;
        matrix.setSaturation(0);
        colorFilter = new ColorMatrixColorFilter(matrix);
        deck = user.getDeck();
        daoUser = new DAOUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String fragments = null;

        if(!(user.getObtainedFragments() == null)){
            fragments = user.getObtainedFragments().get(cards.get(i).getName());
        }
        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(view == null){
            view = inflater.inflate(R.layout.grid_item, null);
        }
        ImageView imageView = view.findViewById(R.id.grid_image);
        TextView price = view.findViewById(R.id.tvPrice);
        TextView name = view.findViewById(R.id.tvName);

        card = cards.get(i);
        imageView.setTag(card);

        Picasso.with(context).load(card.getImageUrl()).into(imageView);

        if(deck.contains((Card) imageView.getTag())){
            imageView.setBackgroundResource(R.drawable.highlight);
        }else{
            imageView.setBackgroundResource(0);
        }


        if(!"complete".equals(fragments)){
            imageView.setColorFilter(colorFilter);
            if(fragments == null){
                name.setText("You don't have any fragment");
                price.setText("Price: " + card.getPrice());
            }else{
                int finalPrice = Math.toIntExact((card.getPrice() / 6) * Integer.parseInt(fragments));
                name.setText("Fragments: " + fragments);
                price.setText("Price: " + finalPrice);
            }
        }else{
            imageView.setColorFilter(null);
            name.setText("Complete");
            price.setText("");
            imageView.setOnLongClickListener(longClickListenerView -> {
                    ImageView im = longClickListenerView.findViewById(R.id.grid_image);
                    Card cardPrueba = (Card) longClickListenerView.getTag();
                    if(deck.contains(cardPrueba)){
                        if(deck.size() > 5) {
                            deck.remove(cardPrueba);
                            im.setBackgroundResource(android.R.color.transparent);
                        }else{
                            Toast.makeText(context, "You can't have less than 5 cards in your deck", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        deck.add(cardPrueba);
                        im.setBackgroundResource(R.drawable.highlight);
                    }
                    daoUser.updateUserDeck(deck);
                    return true;
            });
        }
        return view;
    }




}
