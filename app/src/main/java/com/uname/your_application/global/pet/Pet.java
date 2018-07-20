package com.uname.your_application.global.pet;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import static com.uname.your_application.entrance.constants.Account.FEMALE;
import static com.uname.your_application.entrance.constants.Account.MALE;
import static com.uname.your_application.entrance.tools.UserTools.getMinAnimalDobYear;
import static com.uname.your_application.global.pet.Pet.Constants.CAT;
import static com.uname.your_application.global.pet.Pet.Constants.DOG;

public class Pet implements Comparable<Pet>{
    private String uid;

    public class Constants {

        public static final String PETS = "pets";
        public static final String DOG = "Dog";
        public static final String CAT = "Cat";
        public static final String MALE = "m";

        public static final String FEMALE = "f";
    }
    private static String TAG = "TEST";


    private String name;

    private String animalType;
    private String gender;
    private Calendar birth;
    private boolean deleted = false;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public boolean checkName(String name) {
        return name.length() > 1;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirth(Calendar birth) {
        this.birth = birth;
    }

    public String getAnimalType() {
        return animalType;
    }

    public boolean checkAnimalType(String animalType) {
        return getPossibleAnimalTypes().contains(animalType);
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getGender() {
        return gender;
    }

    public boolean checkGender(String gender) {
        return gender != null && (gender.equals(MALE) || gender.equals(FEMALE));
    }

    public static ArrayList<String> getPossibleAnimalTypes() {
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(DOG);
        tmp.add(CAT);
        return tmp;
    }

    public boolean checkBirth() {
        Log.d(TAG, "checkBirth: " + (birth != null) + (birth.before(Calendar.getInstance())) + " " + (birth.get(Calendar.YEAR) > getMinAnimalDobYear()));
        return birth != null && birth.before(Calendar.getInstance()) && birth.get(Calendar.YEAR) > getMinAnimalDobYear();
    }

    @Override
    public int compareTo(@NonNull Pet o) {
        return name.compareTo(o.getName());
    }
}
