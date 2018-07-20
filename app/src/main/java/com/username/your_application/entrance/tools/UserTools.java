package com.username.your_application.entrance.tools;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class UserTools {
    public static boolean checkEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean checkDob(Calendar dob) {
        return dob != null && dob.get(Calendar.YEAR) <= getMaxDobYear() && dob.get(Calendar.YEAR) >= getMinDobYear();
    }

    public static int getMinDobYear() {
        return Calendar.getInstance().get(Calendar.YEAR) - 100;
    }

    public static int getMinAnimalDobYear() {
        return Calendar.getInstance().get(Calendar.YEAR) - 50;
    }
    public static int getMaxAnimalDobYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    public static int getMaxDobYear() {
        return Calendar.getInstance().get(Calendar.YEAR) - 3;
    }

    public static String getMonthForInt(int num) {
        String month = "error";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getShortMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

}
