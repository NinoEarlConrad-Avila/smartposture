package com.example.smartposture.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.smartposture.R;
import com.example.smartposture.adapter.WorkoutCardAdapter;
import com.example.smartposture.viewmodel.CardViewModel;

import java.util.ArrayList;

public class WorkoutFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardViewModel cardViewModel;
    private WorkoutCardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        LinearLayout squatCard = view.findViewById(R.id.linearLayoutSquat);
        LinearLayout pushupCard = view.findViewById(R.id.linearLayoutPushup);

        squatCard.setOnClickListener(v -> {
            WorkoutDetailsStartFragment detailsFragment = WorkoutDetailsStartFragment.newInstance(
                    "Squats",
                    "squat",
                    1,
                    "A squat is a strength exercise in which the trainee lowers their hips from a standing position and then stands back up."
            );
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_workout, detailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        pushupCard.setOnClickListener(v -> {
            WorkoutDetailsStartFragment detailsFragment = WorkoutDetailsStartFragment.newInstance(
                    "Push Up",
                    "pushup",
                    2,
                    "A push-up is a common strength training exercise performed in a prone position, lying horizontal and face down, raising and lowering the body using the arms."
            );
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_workout, detailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });




//        recyclerView = view.findViewById(R.id.recyclerViewWorkoutCards);

//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

//        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
//
//        adapter = new WorkoutCardAdapter(getActivity(), new ArrayList<>(), cardData -> {
//            WorkoutDetailsStartFragment detailsFragment = WorkoutDetailsStartFragment.newInstance(
//                    cardData.getTitle(),
//                    cardData.getPath(),
//                    cardData.getId(),
//                    cardData.getDescription()
//            );
//
//            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//            transaction.replace(R.id.frag_workout, detailsFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        });

//        recyclerView.setAdapter(adapter);

//        cardViewModel.getCardListLiveData().observe(getViewLifecycleOwner(), cardList -> {
//            adapter.updateCardList(cardList);
//        });

        return view;
    }
}
