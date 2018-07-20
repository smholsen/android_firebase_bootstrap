package com.username.your_application.entrance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.username.your_application.R;
import com.username.your_application.entrance.constants.Account;
import com.username.your_application.entrance.interfaces.GoogleFetching;
import com.username.your_application.entrance.interfaces.VerificationWaiter;
import com.username.your_application.entrance.tools.FacebookLoginManager;
import com.username.your_application.entrance.tools.GoogleEmailFetcher;
import com.username.your_application.entrance.tools.WaitForEmailVerificationTimerTask;
import com.username.your_application.global.tools.AuthHelper;

import java.util.Arrays;
import java.util.Timer;

import static com.username.your_application.entrance.constants.Account.FROM_FB;
import static com.username.your_application.entrance.constants.Account.METHOD;
import static com.username.your_application.entrance.constants.Account.METHOD_EMAIL;
import static com.username.your_application.entrance.constants.Account.METHOD_FB;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToEnd;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToEndWithDelay;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToStart;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToStartWithDelay;
import static com.username.your_application.entrance.tools.UserTools.checkEmail;
import static com.username.your_application.global.tools.StaticTools.hideSoftKeyboard;
import static com.username.your_application.global.tools.StaticTools.setEnabledButton;
import static com.username.your_application.global.tools.StaticTools.showSnackbar;
import static com.username.your_application.global.tools.StaticTools.showSoftKeyboard;

public class LogInActivity extends AppCompatActivity implements GoogleFetching, VerificationWaiter{


    private AuthHelper auth;
    public View baseView;
    private MaterialFancyButton loginButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    private String email;
    private String password;

    // Containers
    private ViewGroup loginFieldsContainer;
    private ViewGroup loggingInLoaderContainer;
    private GoogleEmailFetcher googleEmailFetcher;
    private FacebookLoginManager fbLogin;
    private TextView title;
    private ViewGroup waitingForEmailVerificationLoaderContainer;
    private Timer t;
    private TextView remainingTimeTextView;
    private TextView explanationText;

    // Method


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = new AuthHelper(this);
        setContentView(R.layout.activity_login);
        baseView = findViewById(R.id.loginRootView);
        auth.clearHistoryAndSignInIfAuthenticated(auth.getCurrentUser(), baseView);
        assignViews();
        if (getIntent().getStringExtra(METHOD).equals(METHOD_EMAIL)) {
            setEventListeners();
            setEnabledButton(loginButton, false);
            googleEmailFetcher = new GoogleEmailFetcher(this);
            googleEmailFetcher.prepareIt();
            if (getIntent().getBooleanExtra(FROM_FB, false)) {
                //TODO: Add explanation in View.
                explanationText.setVisibility(View.VISIBLE);
            }
        } else if (getIntent().getStringExtra(METHOD).equals(METHOD_FB)) {
            fbLogin = new FacebookLoginManager(this, auth);
            LoginManager.getInstance().logInWithReadPermissions(LogInActivity.this, Arrays.asList("public_profile", "user_friends"));
            setWaitingForFBLogin();
        }
    }

    private void setWaitingForFBLogin() {
        title.setVisibility(View.INVISIBLE);
        loginFieldsContainer.setVisibility(View.INVISIBLE);
        loggingInLoaderContainer.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
    }

    public void setWaitingForEmailVerification(FirebaseUser fUser) {
        TransitionManager.beginDelayedTransition(loggingInLoaderContainer, getSlideTransitionToStart());
        loggingInLoaderContainer.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition(waitingForEmailVerificationLoaderContainer, getSlideTransitionToEndWithDelay());
        waitingForEmailVerificationLoaderContainer.setVisibility(View.VISIBLE);
        // TimerTask check for email verification
        t = new Timer( );
        WaitForEmailVerificationTimerTask verificationWaiter = new WaitForEmailVerificationTimerTask(this, fUser);
        t.scheduleAtFixedRate(verificationWaiter, 1500, 1500);
    }

    private void assignViews() {
        title = findViewById(R.id.loginText);
        emailEditText = findViewById(R.id.emailLogInEditText);
        passwordEditText = findViewById(R.id.passwordLogInEditText);
        loginButton = findViewById(R.id.doLogInBtn);
        loginFieldsContainer = findViewById(R.id.stageOneOfLogIn);
        loggingInLoaderContainer = findViewById(R.id.stageTwoOfLogIn);
        waitingForEmailVerificationLoaderContainer = findViewById(R.id.stageThreeOfLogIn);
        // Timer while waiting for email verification
        remainingTimeTextView = findViewById(R.id.remainingWaitingForEmailVerification);
        // From FB
        explanationText = findViewById(R.id.explanationTextLogIn);
    }

    private void setEventListeners() {
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (emailEditText.getText().length() == 0) {
                        googleEmailFetcher.signIn();
                        showSoftKeyboard(getBaseContext(), view);
                    }
                }
            }
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    email = String.valueOf(charSequence);
                    possiblyEnableLoginBtn();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    password = String.valueOf(charSequence);
                    possiblyEnableLoginBtn();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    // For prefill w/ Google Sign In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Account.GOOGLE_SIGN_IN_RC) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            googleEmailFetcher.handleSignInResult(task);
        } else {
            // Pass the activity result back to the Facebook SDK
            Log.d("TEST", "gotCallback");
            fbLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        }
    }

    public void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            showSnackbar(getString(R.string.request_manual_mail), baseView);
        } else {
            emailEditText.setText(account.getEmail());
            passwordEditText.requestFocus();
        }
    }

    private void attemptLogin() {
        hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
        TransitionManager.beginDelayedTransition(loginFieldsContainer, getSlideTransitionToStart());
        loginFieldsContainer.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition(loginButton);
        loginButton.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition(loggingInLoaderContainer, getSlideTransitionToEndWithDelay());
        loggingInLoaderContainer.setVisibility(View.VISIBLE);
        auth.attemptEmailLogin(email, password, this);
    }

    public void onInvalidLoginCallback() {
        TransitionManager.beginDelayedTransition(loggingInLoaderContainer, getSlideTransitionToEnd());
        loggingInLoaderContainer.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition(loginButton);
        loginButton.setVisibility(View.VISIBLE);
        TransitionManager.beginDelayedTransition(loginFieldsContainer, getSlideTransitionToStartWithDelay());
        loginFieldsContainer.setVisibility(View.VISIBLE);
    }

    private void possiblyEnableLoginBtn() {
        if (email != null && password != null && checkEmail(email) && password.length() > 2){
            setEnabledButton(loginButton, true);
        } else {
            setEnabledButton(loginButton, false);
        }
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
    public void userJustGotVerified(FirebaseUser user) {
        auth.clearHistoryAndSignInIfAuthenticated(user, baseView);
    }
}
