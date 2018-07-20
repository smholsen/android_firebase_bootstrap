package com.username.your_application.entrance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.username.your_application.R;
import com.username.your_application.entrance.constants.Account;
import com.username.your_application.global.customViews.OverriddenEditText;
import com.username.your_application.entrance.interfaces.GoogleFetching;
import com.username.your_application.entrance.interfaces.VerificationWaiter;
import com.username.your_application.entrance.tools.GoogleEmailFetcher;
import com.username.your_application.entrance.tools.WaitForEmailVerificationTimerTask;
import com.username.your_application.global.tools.AuthHelper;
import com.username.your_application.global.user.UserModel;
import com.username.your_application.global.dialogPlus.GenderAdapter;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;

import static com.username.your_application.entrance.constants.Account.FEMALE;
import static com.username.your_application.entrance.constants.Account.MALE;
import static com.username.your_application.entrance.constants.Account.PREF_NOT_SAY;
import static com.username.your_application.entrance.constants.Account.STAGE_ONE;
import static com.username.your_application.entrance.constants.Account.STAGE_TWO;
import static com.username.your_application.entrance.tools.UserTools.getMonthForInt;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToEnd;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToEndWithDelay;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToStart;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToStartWithDelay;
import static com.username.your_application.global.tools.StaticTools.hideSoftKeyboard;
import static com.username.your_application.global.tools.StaticTools.setEnabledButton;
import static com.username.your_application.global.tools.StaticTools.setFont;
import static com.username.your_application.global.tools.StaticTools.showSnackbar;
import static com.username.your_application.global.tools.StaticTools.showSoftKeyboard;
import static com.username.your_application.entrance.tools.UserTools.checkDob;
import static com.username.your_application.entrance.tools.UserTools.checkEmail;
import static com.username.your_application.entrance.tools.UserTools.checkPassword;
import static com.username.your_application.entrance.tools.UserTools.getMaxDobYear;
import static com.username.your_application.entrance.tools.UserTools.getMinDobYear;


public class SignUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, OnItemClickListener, GoogleFetching, VerificationWaiter {

    private int stageOfSignUp = STAGE_ONE;
    // User to Create
    private UserModel user;
    // Temporary User Values
    private String email;
    private String password;

    private MaterialFancyButton nextButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private DatePickerDialog dobSpinnerPicker;
    private DialogPlus genderSelector;

    // These are used for AutoFill for Email.
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;

    private View baseView;

    private ViewGroup stageOneContainer;
    private ViewGroup stageTwoContainer;
    private ViewGroup stageThreeContainer;
    private OverriddenEditText dobEditText;
    private OverriddenEditText genderEditText;

    // CheckMarks if value is Ok
    private TransitionDrawable emailDoneCheckMark;
    private TransitionDrawable pwDoneCheckMark;
    private TransitionDrawable dobDoneCheckMark;
    private TransitionDrawable genderDoneCheckMark;
    // Bool to evaluate if animations should play
    private boolean emailPreviouslyOK = false;
    private boolean passwordPreviouslyOK = false;
    private boolean dobPreviouslyOK = false;
    private Calendar dobCal;

    // Auth
    private AuthHelper auth;

    // This activity
    SignUpActivity signUpActivity;
    private TextView remainingTimeTextView;
    private Timer t;
    private GoogleEmailFetcher googleEmailFetcher;
    private boolean doNotGoBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpActivity = this;
        auth = new AuthHelper(this);
        setContentView(R.layout.activity_signup);
        auth.clearHistoryAndSignInIfAuthenticated(auth.getCurrentUser(), findViewById(R.id.signUpRootView));
        baseView = findViewById(R.id.signUpRootView);
        user = new UserModel();
        assignViews();
        setEventListeners();
        dobSpinnerPicker = buildDobPicker();
        genderSelector = buildGenderSelector();
        setEnabledButton(nextButton, false);
        googleEmailFetcher = new GoogleEmailFetcher(this);
        googleEmailFetcher.prepareIt();
    }

    private DialogPlus buildGenderSelector() {
        return DialogPlus.newDialog(this)
                .setAdapter(new GenderAdapter(this, true))
                .setGravity(Gravity.CENTER)
                .setOnItemClickListener(this).setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
    }

    private DatePickerDialog buildDobPicker() {
        return new SpinnerDatePickerDialogBuilder()
                .context(SignUpActivity.this)
                .callback(SignUpActivity.this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .defaultDate(Calendar.getInstance().get(Calendar.YEAR) - 20, 0, 1)
                .maxDate(getMaxDobYear(), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .minDate(getMinDobYear(), 0, 1)
                .build();
    }

    private void assignViews() {
        nextButton = findViewById(R.id.next_signup);
        emailEditText = findViewById(R.id.emailSignUpEditText);
        setFont(this, nextButton, "Roboto-Bold.ttf");
        passwordEditText = findViewById(R.id.passwordSignUpEditText);
        stageOneContainer = findViewById(R.id.stageOneOfSignUp);
        stageTwoContainer = findViewById(R.id.stageTwoOfSignUp);
        stageThreeContainer = findViewById(R.id.stageThreeOfSignUp);
        dobEditText = findViewById(R.id.dobSignUpEditText);
        dobEditText.setFocusable(false);
        genderEditText = findViewById(R.id.genderSignUpEditText);
        genderEditText.setFocusable(false);
        // Drawables
        emailDoneCheckMark = new TransitionDrawable(new Drawable[]{getDrawable(R.mipmap.transparent), getDrawable(R.mipmap.ic_done_green)});
        pwDoneCheckMark = new TransitionDrawable(new Drawable[]{getDrawable(R.mipmap.transparent), getDrawable(R.mipmap.ic_done_green)});
        dobDoneCheckMark = new TransitionDrawable(new Drawable[]{getDrawable(R.mipmap.transparent), getDrawable(R.mipmap.ic_done_green)});
        genderDoneCheckMark = new TransitionDrawable(new Drawable[]{getDrawable(R.mipmap.transparent), getDrawable(R.mipmap.ic_done_green)});
        // Timer while waiting for email verification
        remainingTimeTextView = findViewById(R.id.remainingWaitingForEmailVerification);
    }

    @SuppressLint("ClickableViewAccessibility")
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
                email = String.valueOf(charSequence);
                checkAndSetValues(STAGE_ONE, emailEditText);
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
                password = String.valueOf(charSequence);
                checkAndSetValues(STAGE_ONE, passwordEditText);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                   passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stageOfSignUp == STAGE_ONE && checkAndSetValues(STAGE_ONE, null)) {
                    hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
                    // Slide forms in and out of view
                    TransitionManager.beginDelayedTransition(stageOneContainer, getSlideTransitionToStart());
                    stageOneContainer.setVisibility(View.INVISIBLE);
                    TransitionManager.beginDelayedTransition(stageTwoContainer, getSlideTransitionToEndWithDelay());
                    stageTwoContainer.setVisibility(View.VISIBLE);
                    // Update button Text
                    TransitionManager.beginDelayedTransition(nextButton);
                    nextButton.setText(R.string.sign_up_caps);
                    stageOfSignUp = STAGE_TWO;
                    checkAndSetValues(STAGE_TWO, null);
                } else if(stageOfSignUp == STAGE_TWO && checkAndSetValues(STAGE_ONE, null) && checkAndSetValues(STAGE_TWO, null)) {
                    // Try to register account
                    auth.createUserEmail(user.getEmail(), user.getPassword(), user.getGender(), user.getDob(), signUpActivity);
                    setEnabledButton(nextButton, false);
                    // Now wait for callback
                    // if succeed : to waitForEmailVerification()
                    // if fail : to goToPreviousStage()
                }
            }
        });
        dobEditText.setOnKeyListener(null);
        dobEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    dobSpinnerPicker.show();
                }
                return false;
            }
        });
        genderEditText.setOnKeyListener(null);
        genderEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    genderSelector.show();
                }
                return false;
            }
        });
    }

    public void waitForEmailVerification(FirebaseUser user) {
        TransitionManager.beginDelayedTransition(stageTwoContainer, getSlideTransitionToStart());
        stageTwoContainer.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition(nextButton);
        nextButton.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition(stageThreeContainer, getSlideTransitionToEndWithDelay());
        stageThreeContainer.setVisibility(View.VISIBLE);
        // TimerTask check for email verification
        t = new Timer( );
        WaitForEmailVerificationTimerTask verificationWaiter = new WaitForEmailVerificationTimerTask(this, user);
        t.scheduleAtFixedRate(verificationWaiter, 1500, 1500);
    }

    public void killVerificationWaiter() {
        t.cancel();
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
        }
    }

    public void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            requestManualMail();
        } else {
            emailEditText.setText(account.getEmail());
            passwordEditText.requestFocus();
        }
    }

    private void requestManualMail() {
        showSnackbar(getString(R.string.request_manual_mail), baseView);
    }

    private boolean checkAndSetValues(int stage, EditText from) {
        if (stage == STAGE_ONE) {
            if (account != null) {
                if (!email.equals(account.getEmail())) {
                    googleEmailFetcher.signOut();
                }
            }
            if (from == emailEditText) validateEmail(email);
            if (from == passwordEditText) validatePassword(password);
            if (checkEmail(email) && checkPassword(password)) {
                user.setEmail(email);
                user.setPassword(password);
                setEnabledButton(nextButton, true);
                return true;
            } else {
                setEnabledButton(nextButton, false);
                return false;
            }
        } else if (stage == STAGE_TWO) {
            if (user.hasValidDob() && user.hasValidGender()) {
                setEnabledButton(nextButton, true);
                return true;
            } else {
                setEnabledButton(nextButton, false);
                return false;
            }
        }
        return false;
    }

    private void validatePassword(String password) {
        if (checkPassword(password)) {
            if (!passwordPreviouslyOK) {
                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, pwDoneCheckMark, null);
                pwDoneCheckMark.startTransition(500);
                passwordPreviouslyOK = true;
            }
        } else {
            pwDoneCheckMark.resetTransition();
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, pwDoneCheckMark, null);
            passwordPreviouslyOK = false;
        }
    }

    private void validateEmail(String email) {
        if (checkEmail(email)) {
            if (!emailPreviouslyOK) {
                emailEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, emailDoneCheckMark, null);
                emailDoneCheckMark.startTransition(500);
                emailPreviouslyOK = true;
            }
        } else {
            emailDoneCheckMark.resetTransition();
            emailEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, emailDoneCheckMark, null);
            emailPreviouslyOK = false;
        }
    }


    @Override
    public void onBackPressed() {
        if (!doNotGoBack) {
            if (stageOfSignUp == STAGE_ONE) {
                super.onBackPressed();
            } else {
                goToPreviousStage();
            }
        }
    }

    public void goToPreviousStage() {
        if (stageOfSignUp == STAGE_TWO) {
            TransitionManager.beginDelayedTransition(stageTwoContainer, getSlideTransitionToEnd());
            stageTwoContainer.setVisibility(View.INVISIBLE);
            TransitionManager.beginDelayedTransition(stageOneContainer, getSlideTransitionToStartWithDelay());
            stageOneContainer.setVisibility(View.VISIBLE);
            stageOfSignUp = STAGE_ONE;
            // Update button Text
            TransitionManager.beginDelayedTransition(nextButton);
            nextButton.setText(R.string.next_caps);
            setEnabledButton(nextButton, true);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dobEditText.setText(String.format((Locale) null, "%s/%s/%d", getMonthForInt(monthOfYear), String.valueOf(dayOfMonth), year));
        validateDob(year, monthOfYear, dayOfMonth);
        checkAndSetValues(stageOfSignUp, dobEditText);
    }

    private void validateDob(int year, int monthOfYear, int dayOfMonth) {
        dobCal = Calendar.getInstance();
        dobCal.setLenient(false);
        dobCal.set(Calendar.YEAR, year);
        dobCal.set(Calendar.MONTH, monthOfYear);
        dobCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        try {
            dobCal.getTime();
            // If here then valid date
            if (checkDob(dobCal)) {
                user.setDob(dobCal);
                if (!dobPreviouslyOK) {
                    dobEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, dobDoneCheckMark, null);
                    dobDoneCheckMark.startTransition(500);
                    dobPreviouslyOK = true;
                }
            } else {
                dobDoneCheckMark.resetTransition();
                dobEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, dobDoneCheckMark, null);
                dobPreviouslyOK = false;
            }
        }
        catch (Exception e) {
            System.out.println("Invalid date");
        }
    }

    // On Gender Selected
    @Override
    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
        switch (position) {
            case 0:
                genderEditText.setText(R.string.male);
                user.setGender(MALE);
                setCorrectGenderSet(R.mipmap.ic_boy);
                break;
            case 1:
                genderEditText.setText(R.string.female);
                user.setGender(FEMALE);
                setCorrectGenderSet(R.mipmap.ic_girl);
                break;
            case 2:
                genderEditText.setText(R.string.pref_not_say);
                user.setGender(PREF_NOT_SAY);
                setCorrectGenderSet(R.mipmap.ic_genderless);
                break;
            default:
                genderDoneCheckMark.resetTransition();
                genderEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, genderDoneCheckMark, null);
        }
        genderSelector.dismiss();
    }

    private void setCorrectGenderSet(int genderIconDrawableInt) {
        Drawable drawableToSet = getDrawable(genderIconDrawableInt);
        genderEditText.setCompoundDrawablesWithIntrinsicBounds(drawableToSet, null, genderDoneCheckMark, null);
        genderDoneCheckMark.startTransition(500);
        checkAndSetValues(stageOfSignUp, genderEditText);
    }


    public TextView getRemainingTimeTextView() {
        return remainingTimeTextView;
    }

    public void userJustGotVerified(FirebaseUser fUser) {
        auth.clearHistoryAndSignInIfAuthenticated(fUser, baseView);
    }

}
