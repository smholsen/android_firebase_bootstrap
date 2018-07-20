package com.username.your_application.global.tools;

import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;

import static com.username.your_application.entrance.constants.Media.ANIM_DURATION;
import static com.username.your_application.entrance.constants.Media.START_DELAY;

public class AnimationTools {

    public static TransitionSet getSlideTransitionToEnd() {
        return new TransitionSet().addTransition(new Fade()).addTransition(new Slide(Gravity.END)).setDuration(ANIM_DURATION);
    }

    public static TransitionSet getSlideTransitionToEndWithDelay() {
        return new TransitionSet().addTransition(new Fade()).addTransition(new Slide(Gravity.END)).setDuration(ANIM_DURATION).setStartDelay(START_DELAY);
    }

    public static TransitionSet getSlideTransitionToStart() {
        return new TransitionSet().addTransition(new Fade()).addTransition(new Slide(Gravity.START)).setDuration(ANIM_DURATION);
    }

    public static TransitionSet getSlideTransitionToStartWithDelay() {
        return new TransitionSet().addTransition(new Fade()).addTransition(new Slide(Gravity.START)).setDuration(ANIM_DURATION).setStartDelay(START_DELAY);
    }

    public static TransitionSet getSlideTransitionToStartWithIntermediaryDelay() {
        return new TransitionSet().addTransition(new Fade()).addTransition(new Slide(Gravity.START)).setDuration(ANIM_DURATION).setStartDelay(START_DELAY * 2);
    }

    public static TransitionSet getSlideTransitionToEndAfterIntermediary() {
        return new TransitionSet().addTransition(new Fade()).addTransition(new Slide(Gravity.START)).setDuration(ANIM_DURATION).setStartDelay(START_DELAY * 3);
    }
}
