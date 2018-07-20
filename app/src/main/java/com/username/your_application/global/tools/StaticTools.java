package com.username.your_application.global.tools;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.rilixtech.materialfancybutton.MaterialFancyButton;

public class StaticTools {

    public static void setFont(Context context, MaterialFancyButton view, String fontname) {
        view.setCustomTextFont(fontname);
    }

    public static void setEnabledButton(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (enabled) {
            view.setAlpha(1);
        } else {
            view.setAlpha(0.7f);
        }
    }

    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInputFromWindow(
                    view.getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void hideSoftKeyboard(Context context, View currentFocus) {
        // View view = this.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }


    public static void showSnackbar(String message, final View rootView) {
        Snackbar snackbar = Snackbar
                .make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
