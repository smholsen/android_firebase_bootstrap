package com.username.your_application.global.user;

import java.util.Calendar;

import static com.username.your_application.entrance.constants.Account.FEMALE;
import static com.username.your_application.entrance.constants.Account.MALE;
import static com.username.your_application.entrance.constants.Account.PREF_NOT_SAY;
import static com.username.your_application.entrance.tools.UserTools.checkDob;

public class UserModel {

    public UserModel () {
    }

    private String email;
    private String password;
    private Calendar dob;
    private String gender;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Calendar getDob() {
        return dob;
    }

    public void setDob(Calendar dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean hasValidGender() {
        return gender != null && (gender.equals(MALE) || gender.equals(FEMALE) || gender.equals(PREF_NOT_SAY));
    }

    public boolean hasValidDob() {
        return checkDob(dob);
    }
}
