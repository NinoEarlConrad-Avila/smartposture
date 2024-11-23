//package com.example.smartposture.adapter;
//
//import java.util.UUID;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.smartposture.R;
//import com.example.smartposture.model.JoinRequestModel;
//import com.example.smartposture.viewmodel.JoinRequestViewModel;
//
//import java.util.List;
//
//public class JoinRequestsAdapter extends RecyclerView.Adapter<com.example.smartposture.adapter.JoinRequestsAdapter.JoinRequestViewHolder> {
//    private List<JoinRequestModel> joinRequestList;
//    private final JoinRequestViewModel viewModel;
//
//    public JoinRequestsAdapter(List<JoinRequestModel> joinRequestList, JoinRequestViewModel viewModel) {
//        this.joinRequestList = joinRequestList;
//        this.viewModel = viewModel; // Initialize ViewModel here
//    }
//
//    @NonNull
//    @Override
//    public JoinRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_join_request, parent, false);
//        return new JoinRequestViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull JoinRequestViewHolder holder, int position) {
//        JoinRequestModel request = joinRequestList.get(position);
//        holder.usernameTextView.setText(request.getUsername());
//        holder.statusTextView.setText("Pending");
//
//        holder.acceptRequest.setOnClickListener(v -> {
//            viewModel.acceptRequest( request.getRequestId(), request.getUsername(), request.getRoomId());
//            removeRequest(position); // Optionally remove from list
//        });
//
//        holder.declineRequest.setOnClickListener(v -> {
//            viewModel.declineRequest(request.getRequestId());
//            removeRequest(position); // Optionally remove from list
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return joinRequestList.size();
//    }
//
//    public void updateJoinRequests(List<JoinRequestModel> joinRequests) {
//        this.joinRequestList = joinRequests;
//        notifyDataSetChanged();
//    }
//
//    private void removeRequest(int position) {
//        joinRequestList.remove(position);
//        notifyItemRemoved(position);
//    }
//
//    private String generateRandomId() {
//        return UUID.randomUUID().toString();
//    }
//
//    static class JoinRequestViewHolder extends RecyclerView.ViewHolder {
//        TextView usernameTextView, statusTextView;
//        Button acceptRequest, declineRequest;
//
//        public JoinRequestViewHolder(@NonNull View itemView) {
//            super(itemView);
//            usernameTextView = itemView.findViewById(R.id.usernameTextView);
//            statusTextView = itemView.findViewById(R.id.statusTextView);
//            acceptRequest = itemView.findViewById(R.id.acceptRequest);
//            declineRequest = itemView.findViewById(R.id.declineRequest);
//        }
//    }
//}
