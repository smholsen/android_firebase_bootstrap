package com.username.your_application.global.tools;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.username.your_application.R;
import com.username.your_application.entrance.LandingScreenActivity;
import com.username.your_application.entrance.LogInActivity;
import com.username.your_application.entrance.SignUpActivity;
import com.username.your_application.loggedIn.LoggedInActivity;

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.username.your_application.entrance.constants.Account.FROM_FB;
import static com.username.your_application.entrance.constants.Account.METHOD;
import static com.username.your_application.entrance.constants.Account.METHOD_EMAIL;
import static com.username.your_application.global.tools.StaticTools.showSnackbar;

public class AuthHelper {

    private final FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Activity activity;

    public AuthHelper (@Nullable Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (!currentUser.isEmailVerified()) FirebaseAuth.getInstance().signOut();
        }
    }

    public static String getCurrentUserGUID() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean createUserEmail(String email, String password, String gender, Calendar dob, @Nullable final SignUpActivity signUpActivity) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification();
                                Log.d("TEST", "mail sent");
                                if (signUpActivity != null) {
                                    signUpActivity.waitForEmailVerification(user);
                                }
                            }
                            // clearHistoryAndSignInIfAuthenticated(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            FirebaseUser user = mAuth.getCurrentUser();
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                // If the user is already registered, but have not verified his email, then we can simply delete the old user and create a new one.
                                // TODO:
                                if (signUpActivity != null) {
                                    signUpActivity.goToPreviousStage();
                                }
                                showSnackbar(e.getMessage(), activity.findViewById(R.id.signUpRootView));
                            } catch (Exception e) {
                                if (signUpActivity != null) {
                                    signUpActivity.goToPreviousStage();
                                    showSnackbar(signUpActivity.getString(R.string.something_went_wrong), activity.findViewById(R.id.signUpRootView));
                                }
                            }
                        }
                    }
                });
        return true;
    }


    public void clearHistoryAndSignInIfAuthenticated(FirebaseUser user, View currentRootView) {
        // If user is logged in then go straight to the main logged in view.
        if (user != null && user.isEmailVerified()) {
            Log.d("TEST", "logged in");
            Intent intent = new Intent(activity.getApplicationContext(), LoggedInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        } else if (user != null && !user.isEmailVerified()) {
            showSnackbar(activity.getString(R.string.must_verify_email), currentRootView);
        }
    }

    public void logOutCurrentUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(activity.getApplicationContext(), LandingScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void attemptEmailLogin(final String email, String password, final LogInActivity logInActivity) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    clearHistoryAndSignInIfAuthenticated(user, logInActivity.baseView);
                                } else {
                                    user.sendEmailVerification();
                                    logInActivity.setWaitingForEmailVerification(user);
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                if (logInActivity != null) {
                                    showSnackbar(e.getMessage(), activity.findViewById(R.id.loginRootView));
                                    logInActivity.onInvalidLoginCallback();
                                }
                            }
                        }
                    }
                });
    }

    // Facebook
    public void signInWithFacebook(AuthCredential credential, final LogInActivity logInActivity) {
        mAuth.signInWithCredential(credential)
        .addOnCompleteListener(logInActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FB", "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            Log.d("TEST", "isemailverified");
                            clearHistoryAndSignInIfAuthenticated(user, logInActivity.findViewById(R.id.loginRootView));
                        } else {
                            Log.d("TEST", "sent email" + user.getEmail());
                            user.sendEmailVerification();
                            logInActivity.setWaitingForEmailVerification(user);
                        }
                    } else {
                        logInActivity.onBackPressed();
                        showSnackbar("Authentication failed", logInActivity.findViewById(R.id.loginRootView));
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FB", "signInWithCredential:failure", task.getException());
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        // TODO: Start LoginProcess with email and attach extra to intent to tell the user he can merge the users after he has logged in.
                        Intent intent = new Intent(activity.getApplicationContext(), LogInActivity.class);
                        intent.putExtra(METHOD, METHOD_EMAIL);
                        intent.putExtra(FROM_FB, true);
                        activity.startActivity(intent);
                        // showSnackbar("A user is already registered with that email. You can merge these after you have logged in", logInActivity.findViewById(R.id.loginRootView));
                    } catch (Exception e) {
                        showSnackbar("Authentication failed", logInActivity.findViewById(R.id.loginRootView));
                    }
                    logInActivity.onBackPressed();
                }
            }
        });
    }
}
