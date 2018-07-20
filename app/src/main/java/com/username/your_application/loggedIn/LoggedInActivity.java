package com.username.your_application.loggedIn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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

import com.username.your_application.R;
import com.username.your_application.global.pet.Pet;
import com.username.your_application.global.tools.AuthHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoggedInActivity extends AppCompatActivity {
    private String TAG = "Test";

    private TextView mTextMessage;
    // Layouts
    LinearLayout accountLayout;
    RelativeLayout petsLayout;
    private ViewGroup previousView = null;
    private Button logoutBtn;
    private AuthHelper auth;
    private LinearLayout currentView;
    private HashMap<ViewGroup, Integer> layoutIndexes;
    private PetsController petsController;
    private ListView petsList;
    private PetListAdapter petListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = new AuthHelper(this);
        setContentView(R.layout.activity_logged_in);
        assignViews();
        setEventListeners();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Initialize with petView
        swapToView(petsLayout);
        petsController = new PetsController(this);
    }

    private void assignViews() {
        petsLayout = findViewById(R.id.layout_pets_logged_in);
        petsList = findViewById(R.id.layout_pets_list);
        petListAdapter = new PetListAdapter(new ArrayList<Pet>(), getApplicationContext());
        petsList.setAdapter(petListAdapter);
        accountLayout = findViewById(R.id.layout_account_logged_in);
        logoutBtn = findViewById(R.id.logoutBtn);
        setLayoutIndexes();
    }

    private void setLayoutIndexes() {
        layoutIndexes = new HashMap<>();
        // Index on bottom actionbar
        layoutIndexes.put(petsLayout, 0);
        layoutIndexes.put(accountLayout, 1);
    }

    private void setEventListeners() {
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.logOutCurrentUser();
            }
        });
    }

    public void swapToView(ViewGroup currentView) {
        if (previousView == null) {
            currentView.setVisibility(View.VISIBLE);
        } else if (!(currentView.equals(previousView))) {
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
                    return true;
                case R.id.navigation_account:
                    swapToView(accountLayout);
                    return true;
            }
            return false;
        }
    };

    public void updatePetList(List<Pet> pets) {
        if (pets.size() > 0) {
            findViewById(R.id.layout_no_pets).setVisibility(View.INVISIBLE);
            findViewById(R.id.layout_pets_list).setVisibility(View.VISIBLE);
            petListAdapter.addAll(pets);
            Log.d(TAG, "updatePetList: " + petsList.getAdapter());
            Log.d(TAG, "updatePetList: " + petsList.getAdapter().getCount());
        } else {
            findViewById(R.id.layout_no_pets).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_pets_list).setVisibility(View.INVISIBLE);
        }
    }
}
