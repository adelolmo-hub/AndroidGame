package com.example.cardgameproject;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context context;
    ArrayList<Card> cards;
    LayoutInflater inflater;
    User user;
    ColorMatrix matrix = new ColorMatrix();
    ColorMatrixColorFilter colorFilter;


    public GridAdapter(Context context, ArrayList<Card> cards, User user) {
        this.context = context;
        this.cards = cards;
        this.user = user;
        matrix.setSaturation(0);
        colorFilter = new ColorMatrixColorFilter(matrix);
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


        price.setText("Price: " + cards.get(i).getPrice());
        Picasso.with(context).load(cards.get(i).getImageUrl()).into(imageView);


        if(!"complete".equals(fragments)){
            imageView.setColorFilter(colorFilter);
            if(fragments == null){
                name.setText("You don't have any fragment");
            }else{
                name.setText("Fragments: " + fragments);
            }
        }else{
            imageView.setColorFilter(null);
            name.setText("Complete");
        }

        return view;
    }




}
