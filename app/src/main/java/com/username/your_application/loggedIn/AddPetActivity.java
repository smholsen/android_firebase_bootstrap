package com.username.your_application.loggedIn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.username.your_application.R;
import com.username.your_application.global.pet.Pet;
import com.username.your_application.global.customViews.OverriddenEditText;
import com.username.your_application.global.dialogPlus.AnimalGenderAdapter;
import com.username.your_application.global.dialogPlus.AnimalTypeAdapter;
import com.username.your_application.global.tools.AuthHelper;
import com.username.your_application.global.tools.DB;
import com.username.your_application.global.tools.Loader;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.username.your_application.entrance.tools.UserTools.getMinAnimalDobYear;
import static com.username.your_application.entrance.tools.UserTools.getMonthForInt;
import static com.username.your_application.global.pet.Pet.Constants.CAT;
import static com.username.your_application.global.pet.Pet.Constants.DOG;
import static com.username.your_application.global.tools.StaticTools.hideSoftKeyboard;
import static com.username.your_application.global.tools.StaticTools.setEnabledButton;
import static com.username.your_application.global.tools.StaticTools.showSnackbar;
import static com.username.your_application.loggedIn.AnimationStuff.LayoutTools.slideAndChangeToEnd;
import static com.username.your_application.loggedIn.AnimationStuff.LayoutTools.slideAndChangeToStart;
import static java.util.Calendar.YEAR;

public class AddPetActivity extends AppCompatActivity implements OnItemClickListener, DatePickerDialog.OnDateSetListener, DB.DBCallback {
    private static final int RESULT_LOAD_IMG = 123;
    private static final String TAG = "TEST";
    // Constants
    private static int STAGE_ONE = 0;
    private static int STAGE_TWO = 1;
    private static int STAGE_THREE = 2;
    private static int STAGE_FOUR= 3;

    private final AddPetActivity t = this;

    Pet pet;
    private EditText nameEditText;
    private MaterialFancyButton nextButton;
    private int stageOfPetAdd = 0;

    private LinearLayout previousView;
    private LinearLayout stageOneLayout;
    private LinearLayout stageTwoLayout;
    private LinearLayout stageThreeLayout;
    private HashMap<Integer, LinearLayout> layouts;
    private OverriddenEditText animalTypeEditText;
    private OverriddenEditText animalGenderEditText;
    private OverriddenEditText animalBirthDayEditText;
    private DialogPlus animalTypePicker;
    private DialogPlus animalGenderPicker;
    private DatePickerDialog animalBirthDayPicker;
    private ImageView animalProfilePictureView;
    private TextView animalTypeStaticAnimalName;
    private TextView animalGenderStaticHeSheForBirthday;
    // CheckMarks on EditTexts if value is OK
    private TransitionDrawable nameDoneCheckMark;
    private TransitionDrawable animalTypeDoneCheckMark;
    private TransitionDrawable animalGenderDoneCheckMark;
    private TransitionDrawable animalBirthDoneCheckMark;
    // Bools for CheckMarks
    private boolean namePreviouslyOK = false;
    private boolean dobPreviouslyOK;
    private View baseView;
    private boolean okDOB = false;
    // DB & Auth
    private AuthHelper auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        pet = new Pet();
        auth = new AuthHelper(this);
        assignViews();
        setEventListeners();
        animalTypePicker = buildAnimalTypePicker();
        animalGenderPicker = buildAnimalGenderPicker();
        animalBirthDayPicker = buildAnimalDobPicker();
    }

    private DialogPlus buildAnimalTypePicker() {
        return DialogPlus.newDialog(this)
                .setAdapter(new AnimalTypeAdapter(this, true))
                .setGravity(Gravity.CENTER).setContentHolder(new GridHolder(2))
                .setOnItemClickListener(this).setExpanded(false)
                .create();
    }

    private DialogPlus buildAnimalGenderPicker() {
        return DialogPlus.newDialog(this)
                .setAdapter(new AnimalGenderAdapter(this, true))
                .setGravity(Gravity.CENTER)
                .setOnItemClickListener(this).setExpanded(false)
                .create();
    }

    private DatePickerDialog buildAnimalDobPicker() {
        return new SpinnerDatePickerDialogBuilder()
                .context(AddPetActivity.this)
                .callback(AddPetActivity.this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .defaultDate(Calendar.getInstance().get(YEAR) - 1, 0, 1)
                .maxDate(Calendar.getInstance().get(YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .minDate(getMinAnimalDobYear(), 0, 1)
                .build();
    }

    private void assignViews() {
        baseView = findViewById(R.id.add_pet_root_view);
        // Stage 1
        nameEditText = findViewById(R.id.add_pet_name_edittext);
        nextButton = findViewById(R.id.button_next_add_pet);
        setEnabledButton(nextButton, false);
        stageOneLayout = findViewById(R.id.add_pet_stage_one);
        stageTwoLayout = findViewById(R.id.add_pet_stage_two);
        stageThreeLayout = findViewById(R.id.add_pet_stage_three);
        layouts = new HashMap<>();
        prepareLayoutIndexes();
        LinearLayout currentView = stageOneLayout;
        previousView = null;
        // Stage 2
        animalTypeStaticAnimalName = findViewById(R.id.add_pet_name_static_animal_type);
        animalTypeEditText = findViewById(R.id.add_pet_animal_type_edit_text);
        animalTypeEditText.setFocusable(false);
        animalGenderEditText = findViewById(R.id.add_pet_gender_edit_text);
        animalGenderEditText.setFocusable(false);
        // Stage 3
        animalGenderStaticHeSheForBirthday = findViewById(R.id.add_pet_gender_static_animal_birth);
        animalBirthDayEditText = findViewById(R.id.add_pet_animal_birth_edit_text);
        animalBirthDayEditText.setFocusable(false);
        animalProfilePictureView = findViewById(R.id.add_pet_picture_imageview);

        // CheckMarks
        nameDoneCheckMark = new TransitionDrawable(new Drawable[]{getDrawable(R.mipmap.transparent), getDrawable(R.mipmap.ic_done_green)});
        animalTypeDoneCheckMark = new TransitionDrawable(new Drawable[]{getDrawable(R.mipmap.transparent), getDrawable(R.mipmap.ic_done_green)});
        animalGenderDoneCheckMark = new TransitionDrawable(new Drawable[]{getDrawable(R.mipmap.transparent), getDrawable(R.mipmap.ic_done_green)});
        animalBirthDoneCheckMark = new TransitionDrawable(new Drawable[]{getDrawable(R.mipmap.transparent), getDrawable(R.mipmap.ic_done_green)});
    }

    private void prepareLayoutIndexes() {
        layouts.put(0, stageOneLayout);
        layouts.put(1, stageTwoLayout);
        layouts.put(2, stageThreeLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEventListeners() {
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (pet.checkName(String.valueOf(charSequence))) {
                    pet.setName(String.valueOf(charSequence));
                    animalTypeStaticAnimalName.setText(pet.getName());
                    setEnabledButton(nextButton, true);
                    if (!namePreviouslyOK) {
                        nameEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, nameDoneCheckMark, null);
                        nameDoneCheckMark.startTransition(500);
                        namePreviouslyOK = true;
                    }
                } else {
                    setEnabledButton(nextButton, false);
                    nameDoneCheckMark.resetTransition();
                    nameEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, nameDoneCheckMark, null);
                    namePreviouslyOK = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkStage(stageOfPetAdd)) {
                    hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
                    if (stageOfPetAdd < STAGE_THREE) {
                        goToNextStage();
                    } else {
                        Log.d(TAG, "onClick: AddPet");
                        Loader loader = new Loader(t, getString(R.string.adding_pet));
                        PetsController.addPet(pet, t);
                    }
                }
            }
        });
        animalTypeEditText.setOnKeyListener(null);
        animalTypeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    animalTypePicker.show();
                }
                return false;
            }
        });
        animalGenderEditText.setOnKeyListener(null);
        animalGenderEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    animalGenderPicker.show();
                }
                return false;
            }
        });
        animalBirthDayEditText.setOnKeyListener(null);
        animalBirthDayEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    animalBirthDayPicker.show();
                }
                return false;
            }
        });
        animalProfilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
    }

    private void goToNextStage() {
        slideAndChangeToStart(layouts.get(stageOfPetAdd + 1), layouts.get(stageOfPetAdd));
        stageOfPetAdd++;
        Log.d(TAG, "goToNextStage: " + stageOfPetAdd);
        if (stageOfPetAdd == STAGE_ONE) {
            TransitionManager.beginDelayedTransition(nextButton);
            nextButton.setText(R.string.add_pet_caps);
            RelativeLayout.LayoutParams tmp = (RelativeLayout.LayoutParams) nextButton.getLayoutParams();
            tmp.addRule(RelativeLayout.BELOW, R.id.add_pet_stage_three);
        }
        if (stageOfPetAdd ==  STAGE_TWO) {
            TransitionManager.beginDelayedTransition(nextButton);
            RelativeLayout.LayoutParams tmp = (RelativeLayout.LayoutParams) nextButton.getLayoutParams();
            tmp.addRule(RelativeLayout.BELOW, R.id.add_pet_stage_three);
            nextButton.setLayoutParams(tmp);
        }
        checkStage(stageOfPetAdd);
    }

    private boolean checkStage(int stage) {
        boolean b = false;
        if (stage == STAGE_ONE) {
            b = pet.checkName(pet.getName());
            setEnabledButton(nextButton, b);
        } else if (stage == STAGE_TWO) {
            b = pet.checkAnimalType(pet.getAnimalType()) && pet.checkGender(pet.getGender());
            setEnabledButton(nextButton, b);
        } else if (stage == STAGE_THREE) {
            b = okDOB;
            setEnabledButton(nextButton, b);
        }
        return b;
    }


    // On DialogPlus Selected
    @Override
    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
        if (dialog.equals(animalTypePicker)) {
            switch (position) {
                case 0:
                    animalTypeEditText.setText(R.string.dog);
                    pet.setAnimalType(DOG);
                    setCorrectAnimalTypeSet(R.drawable.ic_dog);
                    break;
                case 1:
                    animalTypeEditText.setText(R.string.cat);
                    pet.setAnimalType(CAT);
                    setCorrectAnimalTypeSet(R.drawable.ic_cat);
                    break;
                default:
                    animalTypeDoneCheckMark.resetTransition();
                    animalTypeEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, animalTypeDoneCheckMark, null);
            }
        } else if (dialog.equals(animalGenderPicker)) {
            switch (position) {
                case 0:
                    animalGenderEditText.setText(R.string.male);
                    pet.setGender(Pet.Constants.MALE);
                    setCorrectAnimalGenderSet(R.drawable.ic_mars);
                    animalGenderStaticHeSheForBirthday.setText(R.string.he);
                    break;
                case 1:
                    animalGenderEditText.setText(R.string.female);
                    pet.setGender(Pet.Constants.FEMALE);
                    setCorrectAnimalGenderSet(R.drawable.ic_venus);
                    animalGenderStaticHeSheForBirthday.setText(R.string.she);
                    break;
                default:
                    animalGenderDoneCheckMark.resetTransition();
                    animalTypeEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, animalGenderDoneCheckMark, null);
            }
        }
        dialog.dismiss();
        checkStage(STAGE_TWO);
    }

    private void setCorrectAnimalTypeSet(int icon) {
        Drawable drawableToSet = getDrawable(icon);
        animalTypeEditText.setCompoundDrawablesWithIntrinsicBounds(drawableToSet, null, animalTypeDoneCheckMark, null);
        animalTypeDoneCheckMark.startTransition(500);
        animalGenderEditText.requestFocus();
    }

    private void setCorrectAnimalGenderSet(int icon) {
        Drawable drawableToSet = getDrawable(icon);
        animalGenderEditText.setCompoundDrawablesWithIntrinsicBounds(drawableToSet, null, animalGenderDoneCheckMark, null);
        animalGenderDoneCheckMark.startTransition(500);
    }

    @Override
    public void onBackPressed() {
        if (stageOfPetAdd == STAGE_ONE) super.onBackPressed();
        else {
            if (stageOfPetAdd == STAGE_THREE) {
                TransitionManager.beginDelayedTransition(nextButton);
                nextButton.setText(R.string.next_caps);
            }
            goBackOneStep();
            checkStage(stageOfPetAdd);
        }
    }

    private void goBackOneStep() {
        slideAndChangeToEnd(layouts.get(stageOfPetAdd - 1), layouts.get(stageOfPetAdd));
        stageOfPetAdd--;
        if (stageOfPetAdd ==  STAGE_ONE) {
            TransitionManager.beginDelayedTransition(nextButton);
            RelativeLayout.LayoutParams tmp = (RelativeLayout.LayoutParams) nextButton.getLayoutParams();
            tmp.addRule(RelativeLayout.BELOW, R.id.add_pet_stage_one);
            nextButton.setLayoutParams(tmp);
        }
    }

    // Birthday set
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        animalBirthDayEditText.setText(String.format((Locale) null, "%s/%s/%d", getMonthForInt(monthOfYear), String.valueOf(dayOfMonth), year));
        validateAnimalDob(year, monthOfYear, dayOfMonth);
    }

    private void validateAnimalDob(int year, int monthOfYear, int dayOfMonth) {
        Calendar dobCal = Calendar.getInstance();
        dobCal.setLenient(false);
        dobCal.set(Calendar.YEAR, year);
        dobCal.set(Calendar.MONTH, monthOfYear);
        dobCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        try {
            dobCal.getTime();
            // If here then valid date
            pet.setBirth(dobCal);
            if (pet.checkBirth()) {
                okDOB = true;
                Log.d(TAG, "validateAnimalDob: checkdob");
                if (!dobPreviouslyOK) {
                    animalBirthDayEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, animalBirthDoneCheckMark, null);
                    animalBirthDoneCheckMark.startTransition(500);
                }
            } else {
                animalBirthDoneCheckMark.resetTransition();
                animalBirthDayEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, animalBirthDoneCheckMark, null);
                dobPreviouslyOK = false;
                okDOB = false;
                Log.d(TAG, "validateAnimalDob: !checkdob");
            }
        }
        catch (Exception e) {
            Log.d(TAG, "validateAnimalDob: except");
            okDOB = false;
        }
        checkStage(stageOfPetAdd);
        Log.d(TAG, "validateAnimalDob: " + okDOB + " " + stageOfPetAdd);
    }

    // Image loading
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream;
                if (imageUri != null) {
                    imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    animalProfilePictureView.setImageBitmap(selectedImage);
                    animalProfilePictureView.setPadding(0, 0, 0, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showSnackbar("Something went wrong", baseView);
            }

        }else {
            showSnackbar("You did not select a picture", baseView);
        }
    }

    @Override
    public void onDBUpdateCompleted(boolean completed) {
        // Check if db was ok
        finish();
    }
}
