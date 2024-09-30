package com.example.smartposture.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.smartposture.R;
import com.example.smartposture.CardFragment;
import com.example.smartposture.model.CardData;

public class WorkoutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new CardFragment());
            transaction.commit();
        }

        return view;
    }

    private void navigateToWorkoutDetails(CardData cardData) {
        WorkoutDetailsStartFragment detailsFragment = WorkoutDetailsStartFragment.newInstance(
                cardData.getTitle(), cardData.getPath(), cardData.getId(), cardData.getDescription()
        );

        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, detailsFragment)
                .addToBackStack("WorkoutDetails")
                .commit();
    }
}
