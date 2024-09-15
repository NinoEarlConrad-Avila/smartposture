package com.example.smartposture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartposture.model.CardData;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private static Context context;
    private List<CardData> cardList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CardData cardData);
    }

    public CardAdapter(Context context, List<CardData> cardList, OnItemClickListener listener) {
        this.context = context;
        this.cardList = cardList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardData card = cardList.get(position);
        holder.bind(card, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void updateCardList(List<CardData> newCardList) {
        cardList.clear();
        cardList.addAll(newCardList);
        notifyDataSetChanged(); // Notify RecyclerView to refresh the data
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView workoutName;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.workoutImg);
            workoutName = itemView.findViewById(R.id.workoutName);
        }

        public void bind(final CardData card, final OnItemClickListener listener) {
            int resourceId = context.getResources().getIdentifier(card.getPath(), "drawable", context.getPackageName());

            if (resourceId != 0) {
                Glide.with(context)
                        .load(resourceId)
                        .into(img);
            } else {
                Glide.with(context)
                        .load(R.drawable.default_image)
                        .into(img);
            }

            workoutName.setText(card.getTitle());
            itemView.setOnClickListener(view -> listener.onItemClick(card));
        }
    }
}
