package com.uname.your_application.entrance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.uname.your_application.R;
import com.uname.your_application.global.tools.AuthHelper;

import static com.uname.your_application.entrance.constants.Account.ACTIVE;
import static com.uname.your_application.entrance.constants.Account.INACTIVE;
import static com.uname.your_application.entrance.constants.Account.METHOD;
import static com.uname.your_application.entrance.constants.Account.METHOD_EMAIL;
import static com.uname.your_application.entrance.constants.Account.METHOD_FB;
import static com.uname.your_application.global.tools.StaticTools.setEnabledButton;


public class LandingScreenActivity extends AppCompatActivity {
    public MaterialFancyButton loginButton;
    public MaterialFancyButton signupButton;
    public MaterialFancyButton fbContinueButton;
    private AuthHelper auth;
    private String state = ACTIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.auth = new AuthHelper(this);
        setContentView(R.layout.activity_landing);
        auth.clearHistoryAndSignInIfAuthenticated(auth.getCurrentUser(), findViewById(R.id.landingRootView));
        assignViews();
        setEventListeners();
    }

    private void setEventListeners() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                intent.putExtra(METHOD, METHOD_EMAIL);
                startActivity(intent);
            }
        });
        fbContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                intent.putExtra(METHOD, METHOD_FB);
                startActivity(intent);
            }
        });
    }

    private void assignViews() {
        loginButton = findViewById(R.id.login_btn);
        signupButton = findViewById(R.id.signup_btn);
        fbContinueButton = findViewById(R.id.facebook_btn);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (state.equals(INACTIVE)) {
            setEnabledButton(signupButton, false);
            setEnabledButton(fbContinueButton, false);
            setEnabledButton(loginButton, false);
            findViewById(R.id.spinner).setVisibility(View.VISIBLE);
        } else {
            setEnabledButton(signupButton, true);
            setEnabledButton(fbContinueButton, true);
            setEnabledButton(loginButton, true);
            findViewById(R.id.spinner).setVisibility(View.INVISIBLE);
        }
    }

    public void setState(String state) {
        this.state = state;
    }
}

