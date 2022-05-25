package com.example.cardgameproject;

import android.content.Context;
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


    public GridAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
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

        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(view == null){
            view = inflater.inflate(R.layout.grid_item, null);
        }
        ImageView imageView = view.findViewById(R.id.grid_image);
        TextView price = view.findViewById(R.id.tvPrice);
        TextView name = view.findViewById(R.id.tvName);

        name.setText(cards.get(i).getName());
        price.setText(String.valueOf("Price: " + cards.get(i).getPrice()));
        Picasso.with(context).load(cards.get(i).getImageUrl()).into(imageView);


        return view;
    }




}
