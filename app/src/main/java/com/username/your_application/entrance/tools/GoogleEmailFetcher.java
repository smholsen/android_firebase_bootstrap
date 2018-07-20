package com.username.your_application.entrance.tools;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.username.your_application.entrance.constants.Account;
import com.username.your_application.entrance.interfaces.GoogleFetching;

public class GoogleEmailFetcher {
    private GoogleSignInClient mGoogleSignInClient;
    private Activity activity;
    private GoogleFetching gfActivity;
    GoogleSignInAccount account;

    public GoogleEmailFetcher(GoogleFetching activity) {
        this.activity = (Activity) activity;
        this.gfActivity = activity;
        prepareIt();
    }

    public void prepareIt() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        signOut();
    }

    public void signIn() {
        // For Google AutoFill
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, com.username.your_application.entrance.constants.Account.GOOGLE_SIGN_IN_RC);
    }

    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            gfActivity.updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(Account.GOOGLE_SIGN_IN_RC_DEBUG, "signInResult:failed code=" + e.getStatusCode());
            gfActivity.updateUI(null);
        }
    }

}
