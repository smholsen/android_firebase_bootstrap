package com.username.your_application.entrance.tools;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.username.your_application.entrance.LogInActivity;
import com.username.your_application.global.tools.AuthHelper;


public class FacebookLoginManager {

    private final LogInActivity activity;
    private final AuthHelper auth;
    private CallbackManager callbackManager;
    LoginManager loginManager;

    public FacebookLoginManager(final LogInActivity activity, AuthHelper auth) {
        this.activity = activity;
        this.auth = auth;
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FB", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FB", "facebook:onCancel");
                activity.onBackPressed();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FB", "facebook:onError", error);
                activity.onBackPressed();
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithFacebook(credential, activity);
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

}
