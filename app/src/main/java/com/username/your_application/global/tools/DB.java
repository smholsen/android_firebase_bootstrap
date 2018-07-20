package com.username.your_application.global.tools;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.username.your_application.global.tools.DB.Constants.PETS;
import static com.username.your_application.global.tools.DB.Constants.USERS;

public class DB {
    public enum DBRef {
        TMP, USER, PETS
    }
    public interface DBCallback {
        void onDBUpdateCompleted(boolean completed);
    }

    class Constants{
        public static final String USERS = "users";
        public static final String PETS = "pets";
    }
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static DatabaseReference getInstance(DBRef dbRef) {
        String UGUID = AuthHelper.getCurrentUserGUID();
        if (UGUID != null) {
            switch (dbRef) {
                case USER:
                    return database.getReference(USERS).child(UGUID);
                case PETS:
                    return database.getReference(USERS).child(UGUID).child(PETS);
                case TMP:
                    return database.getReference("tmp");
                default:
                    return database.getReference("tmp");
            }
        } else {
            throw new NullPointerException("Login is Not Valid. Could not Permit DB Connection.");
        }
    }


}
