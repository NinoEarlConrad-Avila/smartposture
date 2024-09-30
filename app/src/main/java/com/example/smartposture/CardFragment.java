package com.example.smartposture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.view.WorkoutDetailsStartFragment;
import com.example.smartposture.viewmodel.CardViewModel;

import java.util.ArrayList;

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
            WorkoutDetailsStartFragment detailsFragment = WorkoutDetailsStartFragment.newInstance(cardData.getTitle(), cardData.getPath(), cardData.getId(), cardData.getDescription());
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_workout, detailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        recyclerView.setAdapter(adapter);

        cardViewModel.getCardListLiveData().observe(getViewLifecycleOwner(), cardList -> {
            adapter.updateCardList(cardList);
        });

        return view;
    }
}
