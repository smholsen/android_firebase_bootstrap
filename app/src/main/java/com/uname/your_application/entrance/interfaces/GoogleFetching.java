package com.uname.your_application.entrance.interfaces;


import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface GoogleFetching {
    void updateUI(GoogleSignInAccount account);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
