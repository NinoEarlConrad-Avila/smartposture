package com.example.smartposture.util;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class AdditionalSpaceBottom extends RecyclerView.ItemDecoration{
    private int spacing;

    public AdditionalSpaceBottom(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemCount = parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);

        if (position == itemCount - 1) {
            outRect.bottom = spacing;
        }
    }
}
