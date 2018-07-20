package com.uname.your_application.entrance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.uname.your_application.R;
import com.uname.your_application.entrance.interfaces.VerificationWaiter;
import com.uname.your_application.entrance.tools.WaitForEmailVerificationTimerTask;
import com.uname.your_application.global.tools.AuthHelper;

import java.util.Timer;

public class WaitForEmailVerification extends AppCompatActivity implements VerificationWaiter{

    private AuthHelper auth;
    private TextView remainingTimeTextView;
    private Timer t;
    private View baseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = new AuthHelper(this);
        setContentView(R.layout.wait_for_email_verification);
        assignViews();
        auth.clearHistoryAndSignInIfAuthenticated(auth.getCurrentUser(), baseView);
        setEventListeners();
        waitForEmailVerification(auth.getCurrentUser());
    }

    private void setEventListeners() {

    }

    private void assignViews() {
        // Timer while waiting for email verification
        remainingTimeTextView = findViewById(R.id.remainingWaitingForEmailVerification);
        baseView = findViewById(R.id.waitForEmailVerificationRootView);
    }

    public void waitForEmailVerification(FirebaseUser user) {
        // TimerTask check for email verification
        t = new Timer( );
        WaitForEmailVerificationTimerTask verificationWaiter = new WaitForEmailVerificationTimerTask(this, user);
        t.scheduleAtFixedRate(verificationWaiter, 1500, 1500);
    }

    @Override
    public void killVerificationWaiter() {
        t.cancel();
    }

    @Override
    public TextView getRemainingTimeTextView() {
        return remainingTimeTextView;
    }

    @Override
    public void userJustGotVerified(FirebaseUser fUser) {
        auth.clearHistoryAndSignInIfAuthenticated(fUser, baseView);
    }
}
