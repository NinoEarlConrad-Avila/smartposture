package com.example.smartposture.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    LinearLayout squatCard;
    LinearLayout pushupCard;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        squatCard = view.findViewById(R.id.linearLayoutSquat);
        pushupCard = view.findViewById(R.id.linearLayoutPushup);

        squatCard.setEnabled(true);
        pushupCard.setEnabled(true);

        squatCard.setOnClickListener(v -> {
            squatCard.setEnabled(false);
            pushupCard.setEnabled(false);
            WorkoutDetailsStartFragment detailsFragment = WorkoutDetailsStartFragment.newInstance(
                    "Squats",
                    "squat",
                    1,
                    "A squat is a strength exercise in which the trainee lowers their hips from a standing position and then stands back up.",
                    "1.Stand with feet shoulder-width apart, toes facing forward.\n2.Engage your core and hinge at the hips.\n3.Drive the hips back, bend at the knees and ankles, and press your knees slightly open.\n4.Sit down into a squat position, keeping your heels and toes on the ground, chest up, and shoulders back.\n5.Repeat the movement several times."
            );
            ((MainActivity) requireActivity()).loadFragment(detailsFragment, true, null);
        });

        pushupCard.setOnClickListener(v -> {
            squatCard.setEnabled(false);
            pushupCard.setEnabled(false);
            WorkoutDetailsStartFragment detailsFragment = WorkoutDetailsStartFragment.newInstance(
                    "Push Up",
                    "pushup",
                    2,
                    "A push-up is a common strength training exercise performed in a prone position, lying horizontal and face down, raising and lowering the body using the arms.",
                    "1.Place your hands shoulder-width apart, body straight from head to heels, and engage your core.\n2.Slowly bend your elbows, lowering yourself until your chest nearly touches the ground.\n3.Press through your palms to lift your body back to the starting position, keeping your core tight.\n4.Perform the movement for your desired number of repetitions, maintaining proper form throughout."
            );
            ((MainActivity) requireActivity()).loadFragment(detailsFragment, true, null);
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
    @Override
    public void onResume(){
        super.onResume();
        enableWorkoutCards();
    }

    public void enableWorkoutCards() {
        if (squatCard != null) {
            squatCard.setEnabled(true);
        }
        if (pushupCard != null) {
            pushupCard.setEnabled(true);
        }
    }
    private void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(fragment.getClass().getName(), 0);

        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frag_workout, fragment, "WorkoutFragment");
            transaction.addToBackStack("WorkoutFragment");
            transaction.commit();
        }
    }
}
