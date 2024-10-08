package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.WorkoutModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CardViewModel extends ViewModel {
    private MutableLiveData<List<WorkoutModel>> cardListLiveData;
    private DatabaseReference databaseReference;

    public CardViewModel() {
        cardListLiveData = new MutableLiveData<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("workouts");
        loadCardData();
    }

    public LiveData<List<WorkoutModel>> getCardListLiveData() {
        return cardListLiveData;
    }

    private void loadCardData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<WorkoutModel> cardList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WorkoutModel cardData = snapshot.getValue(WorkoutModel.class);
                    if (cardData != null) {
                        cardList.add(cardData);
                    }
                }
                cardListLiveData.setValue(cardList); // Update LiveData with the new list
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
