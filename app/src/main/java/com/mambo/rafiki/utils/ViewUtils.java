package com.mambo.rafiki.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.progressindicator.ProgressIndicatorSpec;
import com.mambo.rafiki.R;

import static com.google.android.material.progressindicator.ProgressIndicator.GROW_MODE_OUTGOING;

public class ViewUtils {

    public static int getSelectableBackground(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

        return outValue.resourceId;
    }

    private Drawable getProgressDialog(Context context) {
        ProgressIndicatorSpec progressIndicatorSpec = new ProgressIndicatorSpec();
        progressIndicatorSpec.loadFromAttributes(
                context, null, 0,
                R.style.Widget_MaterialComponents_ProgressIndicator_Circular_Indeterminate);

        progressIndicatorSpec.circularInset = 0; // Inset
        progressIndicatorSpec.circularRadius = (int) dpToPx(context, 10); // Circular radius is 10 dp.

        progressIndicatorSpec.indicatorColors = context.getResources().getIntArray(R.array.progress_colors);
        progressIndicatorSpec.growMode = GROW_MODE_OUTGOING;

        IndeterminateDrawable progressIndicatorDrawable =
                new IndeterminateDrawable(context, progressIndicatorSpec);

        return progressIndicatorDrawable;
    }

    public static float dpToPx(@NonNull Context context, @Dimension(unit = Dimension.DP) int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }


}
