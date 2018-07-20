package com.uname.your_application.global.tools;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.uname.your_application.R;

public class Loader {
    public Loader(AppCompatActivity act, String statement){
            act.setContentView(R.layout.loading);
            TextView t = act.findViewById(R.id.customLoadingText);
            t.setText(statement);
    }

}
