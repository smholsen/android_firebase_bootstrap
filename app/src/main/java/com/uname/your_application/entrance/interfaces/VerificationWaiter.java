package com.uname.your_application.entrance.interfaces;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public interface VerificationWaiter {
    void killVerificationWaiter();
    TextView getRemainingTimeTextView();
    void userJustGotVerified(FirebaseUser user);
}
