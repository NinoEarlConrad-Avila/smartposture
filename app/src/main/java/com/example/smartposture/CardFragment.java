package com.example.smartposture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.model.CardData;
import com.example.smartposture.viewmodel.CardViewModel;

import java.util.ArrayList;
import java.util.List;

public class CardFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardViewModel cardViewModel;
    private CardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);

        adapter = new CardAdapter(getActivity(), new ArrayList<>(), cardData -> {
            Toast.makeText(getActivity(), "Clicked: " + cardData.getId(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        cardViewModel.getCardListLiveData().observe(getViewLifecycleOwner(), cardList -> {
            adapter.updateCardList(cardList);
        });

        return view;
    }
}
