package com.uname.your_application.loggedIn;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.uname.your_application.R;
import com.uname.your_application.global.pet.Pet;
import com.uname.your_application.global.tools.DB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.uname.your_application.global.tools.DB.DBRef.PETS;

public class PetController {
    private String TAG = "Test";
    private LoggedInActivity loggedInActivity;
    private List<Pet> pets;
    private ArrayList<Pet> petsForUser;
    private MaterialFancyButton addFirstPetBtn;
    private DatabaseReference petsDB = DB.getInstance(PETS);

    public PetController(LoggedInActivity loggedInActivity) {
        this.loggedInActivity = loggedInActivity;
        this.pets = new ArrayList<>();
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
        ValueEventListener petsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Pet object and use the values to update the UI
                GenericTypeIndicator<Map<String, Pet>> t = new GenericTypeIndicator<Map<String, Pet>>() {};
                Map<String, Pet> pets = dataSnapshot.getValue(t);
                Log.d(TAG, "onDataChange: ");
                if (pets == null) pets = new HashMap<>();
                for (Map.Entry<String, Pet> petMap : pets.entrySet()) {
                    petMap.getValue().setUid(petMap.getKey());
                }
                refresh(pets.values());
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

    public void startAddNewPetActivity() {
        Intent intent = new Intent(loggedInActivity.getApplicationContext(), AddPetActivity.class);
        loggedInActivity.startActivity(intent);
    }

    private void refresh(Collection<Pet> pets) {
        int numPrevious = 0;
        if (this.pets != null) numPrevious = this.pets.size();
        List<Pet> list = new ArrayList<>();
        for (Pet p : pets) {
            if (!p.getDeleted()) {
                list.add(p);
            }
        }
        Collections.sort(list);
        this.pets = list;
        if (numPrevious != this.pets.size()) loggedInActivity.updatePetList(this.pets);
    }

    public static void addPet(final Pet pet, final DB.DBCallback callback) {
        final String TAG = "Test";
        Log.d(TAG, "addPet: " + pet.getName());
        String key = DB.getInstance(PETS).push().getKey();
        DB.getInstance(PETS).child(key).setValue((pet), new DatabaseReference.CompletionListener() {
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

    public List<Pet> getPets() {
        return pets;
    }
}
