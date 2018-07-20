package com.username.your_application.loggedIn.AnimationStuff;

import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static android.view.View.INVISIBLE;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToEnd;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToEndWithDelay;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToStart;
import static com.username.your_application.global.tools.AnimationTools.getSlideTransitionToStartWithDelay;

public class LayoutTools {

    public static void slideAndChangeToEnd(LinearLayout currentView, LinearLayout previousView) {
        TransitionManager.beginDelayedTransition(previousView, getSlideTransitionToEnd());
        previousView.setVisibility(INVISIBLE);
        TransitionManager.beginDelayedTransition(currentView, getSlideTransitionToStartWithDelay());
        currentView.setVisibility(View.VISIBLE);
    }

    public static void slideAndChangeToStart(LinearLayout currentView, LinearLayout previousView) {
        TransitionManager.beginDelayedTransition(previousView, getSlideTransitionToStart());
        previousView.setVisibility(INVISIBLE);
        TransitionManager.beginDelayedTransition(currentView, getSlideTransitionToEndWithDelay());
        currentView.setVisibility(View.VISIBLE);
    }

    public static void slideAndFadeToStart(ViewGroup btn) {
        TransitionManager.beginDelayedTransition(btn, getSlideTransitionToStart());
        btn.setVisibility(INVISIBLE);
    }
}
