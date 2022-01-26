package com.mambo.rafiki.binding;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.mambo.rafiki.R;

public class GlideBindingAdapter {

    @BindingAdapter("setImageFromUrl")
    public static void setImageFromUrl(ImageView imageView, String url) {

        Context context = imageView.getContext();

        Glide.with(context)
                .load(url)
                .centerCrop()
                .error((R.color.primaryColor))
                .into(imageView);
    }

    @BindingAdapter("setImageFromDrawable")
    public static void setImageFromDrawable(ImageView imageView, int drawableId) {

        Context context = imageView.getContext();

        Glide.with(context)
                .load(drawableId)
                .centerCrop()
                .into(imageView);
    }

    @BindingAdapter("setImageFromUri")
    public static void setImageFromUri(ImageView imageView, Uri uri) {

        Context context = imageView.getContext();

        Glide.with(context)
                .load(uri)
                .centerCrop()
                .into(imageView);
    }

}
