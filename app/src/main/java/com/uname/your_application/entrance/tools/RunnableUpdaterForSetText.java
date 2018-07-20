package com.uname.your_application.entrance.tools;

import android.widget.TextView;

public class RunnableUpdaterForSetText implements Runnable {

    private TextView textView;
    private String text;

    RunnableUpdaterForSetText(TextView textView, String text) {
        this.textView = textView;
        this.text = text;
    }

    @Override
    public void run() {
        textView.setText(text);
    }
}
