package com.uname.your_application.loggedIn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.uname.your_application.R;
import com.uname.your_application.global.pet.Pet;
import com.uname.your_application.global.tools.AuthHelper;
import com.uname.your_application.loggedIn.presenters.PetProfilePresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoggedInActivity extends AppCompatActivity {
    private String TAG = "Test";

    private TextView mTextMessage;
    // Layouts
    LinearLayout accountLayout;
    LinearLayout dashboardLayout;
    RelativeLayout petsLayout;
    RelativeLayout petProfileLayout;
    private ViewGroup previousView = null;
    private Button logoutBtn;
    private AuthHelper auth;
    private LinearLayout currentView;
    private HashMap<ViewGroup, Integer> layoutIndexes;
    private ListView petsList;
    private PetListAdapter petListAdapter;
    private PetController petController;
    private PetProfilePresenter petProfilePresenter;
    private boolean currentlyShowingPetProfile = false;
    private Button deletePetButton;
    private FloatingActionButton addPetBtn;
    private MaterialFancyButton addFirstPetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = new AuthHelper(this);
        setContentView(R.layout.activity_logged_in);
        assignViews();
        setEventListeners();
        petController = new PetController(this);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        petProfilePresenter = new PetProfilePresenter(this);
        // Initialize with dashboard
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        swapToView(dashboardLayout);
    }

    private void assignViews() {
        petsLayout = findViewById(R.id.layout_pets_logged_in);
        petsList = findViewById(R.id.layout_pets_list);
        petListAdapter = new PetListAdapter(new ArrayList<Pet>(), getApplicationContext(), this);
        petsList.setAdapter(petListAdapter);
        accountLayout = findViewById(R.id.layout_account_logged_in);
        dashboardLayout = findViewById(R.id.layout_dashboard);
        logoutBtn = findViewById(R.id.logoutBtn);
        petProfileLayout = findViewById(R.id.pet_profile_view);
        deletePetButton = findViewById(R.id.pet_profile_delete);
        addPetBtn = findViewById(R.id.add_pet_btn);
        addFirstPetBtn = findViewById(R.id.no_pet_add_pet_btn);
    }


    private void setEventListeners() {
        // Account
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.logOutCurrentUser();
            }
        });

        // Dashboard

        // Pets
        addPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petController.startAddNewPetActivity();
            }
        });
        addFirstPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petController.startAddNewPetActivity();
            }
        });

        // Pet Profile
        deletePetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petProfilePresenter.deletePet();
            }
        });
    }

    public void swapToView(ViewGroup currentView) {
        if (previousView == null) {
            currentView.setVisibility(View.VISIBLE);
        } else if (!(currentView.equals(previousView))) {
            currentlyShowingPetProfile = currentView.equals(petProfileLayout);
            previousView.setVisibility(View.INVISIBLE);
            currentView.setVisibility(View.VISIBLE);
        }
        previousView = currentView;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pets:
                    swapToView(petsLayout);
                    setNoPetsViewVisible(petController.getPets().size() < 1);
                    return true;
                case R.id.navigation_dashboard:
                    swapToView(dashboardLayout);
                    setNoPetsViewVisible(petController.getPets().size() < 1);
                    return true;
                case R.id.navigation_account:
                    swapToView(accountLayout);
                    setNoPetsViewVisible(false);
                    return true;
            }
            return false;
        }
    };

    private void setNoPetsViewVisible(boolean visible) {
        Log.d(TAG, "setNoPetsViewVisible: " + visible);
        if (visible) {
            findViewById(R.id.layout_no_pets).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layout_no_pets).setVisibility(View.INVISIBLE);
        }
    }

    public void updatePetList(List<Pet> pets) {
        // I know this is not really good but the list is seldom updated and I don't have time.
        // It is only called on start of activity and whenever a pet is added/ removed.
        petListAdapter = new PetListAdapter(new ArrayList<Pet>(), getApplicationContext(), this);
        petsList.setAdapter(petListAdapter);
        if (pets.size() > 0) {
            setNoPetsViewVisible(false);
            findViewById(R.id.layout_pets_list).setVisibility(View.VISIBLE);
            petListAdapter.addAll(pets);
            Log.d(TAG, "updatePetList: " + petsList.getAdapter());
            Log.d(TAG, "updatePetList: " + petsList.getAdapter().getCount());
        } else {
            Log.d(TAG, "updatePetList: " + "No pets");
            setNoPetsViewVisible(true);
            findViewById(R.id.layout_pets_list).setVisibility(View.INVISIBLE);
        }
    }

    public void showPetProfile(Pet pet) {
        swapToView(petProfileLayout);
        petProfilePresenter.update(pet);
    }

    @Override
    public void onBackPressed() {
        if (currentlyShowingPetProfile) {
            swapToView(petsLayout);
            currentlyShowingPetProfile = false;
        } else {
            super.onBackPressed();
        }
    }
}
