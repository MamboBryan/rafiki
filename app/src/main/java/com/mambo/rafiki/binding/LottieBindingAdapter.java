package com.mambo.rafiki.binding;

import androidx.databinding.BindingAdapter;

import com.airbnb.lottie.LottieAnimationView;

public class LottieBindingAdapter {

    @BindingAdapter("setAnimationFromDrawable")
    public static void setAnimationFromDrawable(LottieAnimationView lottieAnimationView, int drawableId) {

        lottieAnimationView.setAnimation(drawableId);
        lottieAnimationView.animate();

    }

}
