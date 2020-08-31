package com.example.lifeassistant_project.features_class;

import android.view.animation.Animation;

public class AnimPlayingListener implements Animation.AnimationListener {
    private boolean isPlaying=false;

    @Override
    public void onAnimationStart(Animation animation) {
        isPlaying=true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        isPlaying=false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public boolean getPlaying(){
        return isPlaying;
    }
}
