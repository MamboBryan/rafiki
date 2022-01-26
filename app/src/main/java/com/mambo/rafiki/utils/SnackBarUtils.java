package com.mambo.rafiki.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtils {

    private Snackbar snackbar;
    private static SnackBarUtils instance;

    public static SnackBarUtils getInstance() {

        if (instance == null) {
            instance = new SnackBarUtils();
        }

        return instance;
    }

    public void show() {
        snackbar.show();
    }

    public void dismiss() {

        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }

    }

    public Snackbar makeSnackBar(Context context, View mainLayout, String message) {
        snackbar = Snackbar.make(mainLayout, message, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        ProgressBar progressBar = new ProgressBar(context);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        params.setMargins(0, 40, 40, 0);

        progressBar.setLayoutParams(params);
        contentLay.addView(progressBar);

        return snackbar;
    }

}
