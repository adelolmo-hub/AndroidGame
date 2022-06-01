package com.example.cardgameproject;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardGameAdapter extends BaseAdapter {

    Context context;
    ArrayList<Card> cards;
    LayoutInflater inflater;

    Card card;

    public CardGameAdapter(Context context, ArrayList<Card> cards) {
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
            view = inflater.inflate(R.layout.grid_mainchar, null);
        }
        ImageView imageView = view.findViewById(R.id.grid_cards);
        imageView.setMaxHeight(145);
        imageView.setMaxWidth(100);
        imageView.setMinimumHeight(145);
        imageView.setMinimumWidth(100);
        imageView.setOnDragListener(new View.OnDragListener(){

            @Override
            public boolean onDrag(View view, DragEvent event) {
                ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                ClipData dragData = new ClipData(
                        (CharSequence) view.getTag(),
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                        item);
                View.DragShadowBuilder myShadow = new DragShadowCardBuilder(imageView);
                view.startDragAndDrop(dragData,  // The data to be dragged
                        myShadow,  // The drag shadow builder
                        null,      // No need to use local data
                        0          // Flags (not currently used, set to 0)
                );
                return true;
            }
        });
        card = cards.get(i);

        Picasso.with(context).load(card.getImageUrl()).into(imageView);
        return view;
    }
}
