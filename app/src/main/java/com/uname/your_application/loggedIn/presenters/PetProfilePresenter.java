package com.uname.your_application.loggedIn.presenters;


import com.uname.your_application.global.pet.Pet;
import com.uname.your_application.global.tools.DB;
import com.uname.your_application.loggedIn.LoggedInActivity;

public class PetProfilePresenter {
    LoggedInActivity activity;
    Pet pet;

    public PetProfilePresenter(LoggedInActivity activity) {
        this.activity = activity;
    }

    public void update(Pet pet) {
        this.pet = pet;

    }

    public void deletePet() {
        // DeletePet
        pet.setDeleted(true);
        DB.getInstance(DB.DBRef.PETS).child(pet.getUid()).setValue(pet);
        // Go back. (DB Should call list automatically to update the view. Wont work if user is
        // offline though.)
        activity.onBackPressed();
    }
}
