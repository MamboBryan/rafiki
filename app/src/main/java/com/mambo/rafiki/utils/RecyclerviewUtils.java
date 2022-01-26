package com.mambo.rafiki.utils;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mambo.rafiki.R;
import com.mambo.rafiki.databinding.LayoutTemplateRecyclerCompleteBinding;
import com.mambo.rafiki.databinding.LayoutTemplateRecyclerDataBinding;
import com.mambo.rafiki.databinding.LayoutTemplateRecyclerFeedbackBinding;
import com.mambo.rafiki.databinding.LayoutTemplateRecyclerLoadingBinding;

public class RecyclerviewUtils {

    //RECYCLERVIEW FEEDBACK IDs
    public static final int FEEDBACK_EMPTY = 402;
    public static final int FEEDBACK_NO_INTERNET = 403;
    public static final int FEEDBACK_ERROR = 404;

    private OnRetryListener onRetryListener;

    private LayoutTemplateRecyclerCompleteBinding completeBinding;
    private LayoutTemplateRecyclerDataBinding recyclerDataBinding;
    private LayoutTemplateRecyclerFeedbackBinding feedbackBinding;

    public static RecyclerviewUtils getInstance(LayoutTemplateRecyclerCompleteBinding completeBinding, OnRetryListener onRetryListener) {
        return new RecyclerviewUtils(onRetryListener, completeBinding);
    }

    public static RecyclerviewUtils getInstance(LayoutTemplateRecyclerCompleteBinding completeBinding) {
        return new RecyclerviewUtils(completeBinding);
    }

    private RecyclerviewUtils(OnRetryListener onRetryListener, LayoutTemplateRecyclerCompleteBinding completeBinding) {
        this.completeBinding = completeBinding;
        this.onRetryListener = onRetryListener;

        init();
    }

    public RecyclerviewUtils(LayoutTemplateRecyclerCompleteBinding completeBinding) {
        this.completeBinding = completeBinding;

        init();
    }

    private void init() {

        completeBinding.setIsDataReady(false);
        completeBinding.setIsFeedbackRequired(false);
        completeBinding.setIsLoading(true);

        recyclerDataBinding = completeBinding.includeTemplateData;

        feedbackBinding = completeBinding.includeTemplateFeedback;
        LayoutTemplateRecyclerLoadingBinding loadingBinding = completeBinding.includeTemplateLoading;

        loadingBinding.lavTemplateRecyclerLoading.setAnimation(R.raw.loading);

        feedbackBinding.setAnimationId(R.raw.loading);

        feedbackBinding.setOnRetryListener(onRetryListener);

        showLoading();

    }

    private void setItemVisibility(boolean isLoading, boolean isDataReady, boolean isFeedbackRequired) {
        completeBinding.setIsLoading(isLoading);
        completeBinding.setIsDataReady(isDataReady);
        completeBinding.setIsFeedbackRequired(isFeedbackRequired);
    }


    public void showLoading() {
        setItemVisibility(true, false, false);
    }

    public void showFeedback(int feedbackId, String message) {
        setItemVisibility(false, false, true);

        switch (feedbackId) {

            case FEEDBACK_EMPTY:

                feedbackBinding.lavTemplateRecyclerFeedback.setAnimation(R.raw.empty);
                feedbackBinding.setFeedbackText(message);

                break;

            case FEEDBACK_NO_INTERNET:
                feedbackBinding.setIsButtonVisible(true);

                feedbackBinding.lavTemplateRecyclerFeedback.setAnimation(R.raw.network);
                feedbackBinding.setFeedbackText("Your phone is not connected to the internet");

                break;

            case FEEDBACK_ERROR:
                feedbackBinding.setIsButtonVisible(true);

                feedbackBinding.lavTemplateRecyclerFeedback.setAnimation(R.raw.error_emoji);
                feedbackBinding.setFeedbackText(message);

                break;

            default:
                break;

        }

    }

    public void showData() {
        setItemVisibility(false, true, false);
    }

    public XRecyclerView getRecyclerView() {
        return recyclerDataBinding.rvTemplate;
    }

    public interface OnRetryListener {

        void onRetryClicked();

    }
}
