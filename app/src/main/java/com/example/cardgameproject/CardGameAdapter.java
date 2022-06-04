package com.example.cardgameproject;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardGameAdapter extends BaseAdapter {

    Context context;
    ArrayList<Card> cards;
    LayoutInflater inflater;
    int mainBoardHeight;

    Card card;

    public CardGameAdapter(Context context, ArrayList<Card> cards, int mainBoardHeight) {
        this.context = context;
        this.cards = cards;
        this.mainBoardHeight = mainBoardHeight;
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
    public View getView(int i, View v, ViewGroup viewGroup) {
        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(v == null){
            v = inflater.inflate(R.layout.grid_mainchar, null);
        }
        v.setTag("IDLL"+i);
        ImageView imageView = v.findViewById(R.id.grid_cards);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, mainBoardHeight);
        layoutParams.setMargins(30,0,30,0);
        imageView.setLayoutParams(layoutParams);
        imageView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                GameActivity.setLastDrawableImage(imageView.getDrawable());
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
                view.setVisibility(View.GONE);
                GameActivity.setLastViewClicked((View) imageView.getParent());
                return false;
            }
        });

        card = cards.get(i);

        Picasso.with(context).load(card.getImageUrl()).into(imageView);
        return v;
    }
}
