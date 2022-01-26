package com.mambo.rafiki.binding;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class ViewBindingAdapter {

    @BindingAdapter("app:goneUnless")
    public static void goneUnless(View view, Boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("app:visibleUnless")
    public static void visibleUnless(View view, Boolean visible) {
        view.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter("app:invisibleUnless")
    public static void invisibleUnless(View view, Boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter("app:visibleUnlessIsPremium")
    public static void visibleUnlessIsPremium(View view, Boolean isPremium) {
        view.setVisibility(isPremium ? View.GONE : View.VISIBLE);
    }

}
