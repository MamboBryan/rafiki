package com.mambo.rafiki.ui.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeCallbacks extends ItemTouchHelper.Callback {

    private Context mContext;
    private Paint mClearPaint;
    private ColorDrawable mBackground;

    @DrawableRes
    int leftDrawable;

    @DrawableRes
    int rightDrawable;

    private OnItemSwipedListener onItemSwipedListener;

    public SwipeCallbacks(Context context, OnItemSwipedListener onItemSwipedListener) {

        mContext = context;
        mBackground = new ColorDrawable();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        this.onItemSwipedListener = onItemSwipedListener;

    }

    public SwipeCallbacks(Context mContext, int leftDrawable, int rightDrawable, OnItemSwipedListener onItemSwipedListener) {
        this.mContext = mContext;
        this.leftDrawable = leftDrawable;
        this.rightDrawable = rightDrawable;
        this.onItemSwipedListener = onItemSwipedListener;

        mBackground = new ColorDrawable();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {

            //Logic to do when swipe left
            onItemSwipedListener.onItemSwiped(ItemTouchHelper.LEFT, viewHolder.getAdapterPosition());

        } else {

            //Logic to do when swipe right
            onItemSwipedListener.onItemSwiped(ItemTouchHelper.RIGHT, viewHolder.getAdapterPosition());

        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        float height = (float) itemView.getBottom() - (float) itemView.getTop();
        float width = height / 3;

        int backgroundTop;
        int backgroundLeft;
        int backgroundRight;
        int backgroundBottom;

        int iconMargin;
        int iconTop;
        int iconLeft;
        int iconRight;
        int iconBottom;

        Drawable iconDrawable;
        int backgroundColor;

        if (dX > 0) {

            backgroundColor = Color.parseColor("#EBF0FF");
            iconDrawable = ContextCompat.getDrawable(mContext, leftDrawable);

            backgroundLeft = itemView.getLeft();
            backgroundTop = itemView.getTop();
            backgroundRight = (int) dX;
            backgroundBottom = itemView.getBottom();

            iconLeft = (int) (itemView.getLeft() + width);
            iconTop = (int) (itemView.getTop() + width);
            iconRight = (int) (itemView.getLeft() + 2 * width);

        } else {

            backgroundColor = Color.parseColor("#EBF0FF");
            iconDrawable = ContextCompat.getDrawable(mContext, rightDrawable);

            backgroundLeft = (int) (itemView.getRight() + dX);
            backgroundTop = itemView.getTop();
            backgroundRight = itemView.getRight();
            backgroundBottom = itemView.getBottom();

            iconLeft = (int) (itemView.getRight() - 2 * width);
            iconTop = (int) (itemView.getTop() + width);
            iconRight = (int) (itemView.getRight() - width);

        }

        iconBottom = (int) (itemView.getBottom() - width);

        mBackground.setColor(backgroundColor);
        mBackground.setBounds(backgroundLeft, backgroundTop, backgroundRight, backgroundBottom);
        mBackground.draw(c);

        iconDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        iconDrawable.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }

    public interface OnItemSwipedListener {
        void onItemSwiped(int swipeDirection, int position);
    }
}
