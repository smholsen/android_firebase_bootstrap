package com.username.your_application.loggedIn;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.username.your_application.R;
import com.username.your_application.global.pet.Pet;
import com.username.your_application.global.tools.DB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.username.your_application.global.tools.DB.DBRef.PETS;

class PetsController {
    private String TAG = "Test";
    private LoggedInActivity loggedInActivity;
    private List<Pet> pets;
    private ArrayList<Pet> petsForUser;
    private MaterialFancyButton addFirstPetBtn;
    private DatabaseReference petsDB = DB.getInstance(PETS);

    PetsController(LoggedInActivity loggedInActivity) {
        this.loggedInActivity = loggedInActivity;
        assignViews();
        setEventListeners();
        initializeView();
    }

    private void initializeView() {

    }

    private void assignViews() {
        addFirstPetBtn = loggedInActivity.findViewById(R.id.no_pet_add_pet_btn);
    }

    private void setEventListeners() {
        addFirstPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loggedInActivity.getApplicationContext(), AddPetActivity.class);
                loggedInActivity.startActivity(intent);
            }
        });
        ValueEventListener petsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Pet object and use the values to update the UI
                GenericTypeIndicator<Map<String, Pet>> t = new GenericTypeIndicator<Map<String, Pet>>() {};
                Map<String, Pet> pets = dataSnapshot.getValue(t);
                Log.d(TAG, "onDataChange: ");
                if (pets != null) refresh(pets.values());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        DB.getInstance(PETS).addValueEventListener(petsListener);

    }

    private void refresh(Collection<Pet> pets) {
        List<Pet> list = new ArrayList<>(pets);
        Collections.sort(list);
        this.pets = list;
        if (this.pets.size() > 0) {
            loggedInActivity.updatePetList(this.pets);
            // Special case if only 1 pet
            if (this.pets.size() == 1) {

            } else {

            }
        }
    }

    static void addPet(final Pet pet, final DB.DBCallback callback) {
        final String TAG = "Test";
        Log.d(TAG, "addPet: " + pet.getName());

        DB.getInstance(PETS).child(pet.getName()).setValue((pet), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "onComplete: " +  "Data could not be saved " + databaseError.getMessage());
                    callback.onDBUpdateCompleted(false);
                } else {
                    Log.d(TAG, "onComplete: " +  "Data saved sucessfully");
                    callback.onDBUpdateCompleted(true);
                }
            }
        });
    }
}
