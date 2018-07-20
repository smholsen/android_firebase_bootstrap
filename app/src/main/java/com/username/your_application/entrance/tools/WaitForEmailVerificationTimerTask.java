package com.username.your_application.entrance.tools;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.username.your_application.entrance.interfaces.VerificationWaiter;

import java.util.TimerTask;

import static com.username.your_application.entrance.constants.Account.WAIT_TIME_FOR_EMAIL_VERIFICATION_CHECK;

public class WaitForEmailVerificationTimerTask extends TimerTask{
    private int remainingTime = WAIT_TIME_FOR_EMAIL_VERIFICATION_CHECK;
    private Activity activity;
    private VerificationWaiter vwActivity;
    private FirebaseUser user;

    public WaitForEmailVerificationTimerTask(Activity activity, FirebaseUser user) {
        this.activity = activity;
        this.vwActivity = (VerificationWaiter) activity;
        this.user = user;
    }

    @Override
    public void run() {
        if (remainingTime > 1) {
            remainingTime -=1;
            activity.runOnUiThread(new RunnableUpdaterForSetText(vwActivity.getRemainingTimeTextView(), String.valueOf(remainingTime)));
        } else {
            user.reload();
            Log.d("TEST", String.valueOf(user.isEmailVerified()));
            if (user.isEmailVerified()) {
                vwActivity.killVerificationWaiter();
                vwActivity.userJustGotVerified(user);
            } else {
                remainingTime = 5;
                activity.runOnUiThread(new RunnableUpdaterForSetText(vwActivity.getRemainingTimeTextView(), String.valueOf(remainingTime)));
            }
        }
    }
}
