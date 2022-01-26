package com.mambo.rafiki.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.mambo.rafiki.R;

public class LoadingDialog {

    private static LoadingDialog instance;
    private Dialog dialog;

    public static LoadingDialog getInstance() {

        if (instance == null) {
            instance = new LoadingDialog();
        }

        return instance;
    }

    //..we need the context else we can not create the dialog so get context in constructor
    private LoadingDialog() {
    }

    public void showDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.layout_loading);
        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog() {

        if (dialog != null && dialog.isShowing()) {

            dialog.dismiss();
            instance = null;

        }

    }
}
