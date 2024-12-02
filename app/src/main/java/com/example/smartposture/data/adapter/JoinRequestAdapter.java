package com.example.smartposture.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.JoinRequest;

import java.util.List;

public class JoinRequestAdapter extends RecyclerView.Adapter<JoinRequestAdapter.JoinRequestViewHolder> {

    private final List<JoinRequest> joinRequests;
    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onAccept(JoinRequest request);
        void onReject(JoinRequest request);
    }

    public JoinRequestAdapter(List<JoinRequest> joinRequests, OnItemActionListener listener) {
        this.joinRequests = joinRequests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JoinRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_join_request, parent, false);
        return new JoinRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JoinRequestViewHolder holder, int position) {
        JoinRequest request = joinRequests.get(position);
        holder.username.setText(request.getUser_name());

        holder.acceptButton.setOnClickListener(v -> listener.onAccept(request));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(request));
    }

    @Override
    public int getItemCount() {
        return joinRequests.size();
    }

    static class JoinRequestViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageButton acceptButton, rejectButton;

        public JoinRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            acceptButton = itemView.findViewById(R.id.accept);
            rejectButton = itemView.findViewById(R.id.reject);
        }
    }
}

