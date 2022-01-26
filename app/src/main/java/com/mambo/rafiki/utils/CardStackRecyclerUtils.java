package com.mambo.rafiki.utils;

import com.mambo.rafiki.R;
import com.mambo.rafiki.databinding.LayoutTemplateCardCompleteBinding;
import com.mambo.rafiki.databinding.LayoutTemplateCardStackBinding;
import com.mambo.rafiki.databinding.LayoutTemplateRecyclerFeedbackBinding;
import com.mambo.rafiki.databinding.LayoutTemplateRecyclerLoadingBinding;
import com.yuyakaido.android.cardstackview.CardStackView;

public class CardStackRecyclerUtils {

    //RECYCLERVIEW FEEDBACK IDs
    public static final int FEEDBACK_EMPTY = 402;
    public static final int FEEDBACK_NO_INTERNET = 403;
    public static final int FEEDBACK_ERROR = 404;

    private RecyclerviewUtils.OnRetryListener onRetryListener;

    private LayoutTemplateCardCompleteBinding completeBinding;
    private LayoutTemplateCardStackBinding cardStackBinding;
    private LayoutTemplateRecyclerFeedbackBinding feedbackBinding;

    public static CardStackRecyclerUtils getInstance(LayoutTemplateCardCompleteBinding completeBinding, RecyclerviewUtils.OnRetryListener onRetryListener) {
        return new CardStackRecyclerUtils(onRetryListener, completeBinding);
    }

    public static CardStackRecyclerUtils getInstance(LayoutTemplateCardCompleteBinding completeBinding) {
        return new CardStackRecyclerUtils(completeBinding);
    }

    private CardStackRecyclerUtils(RecyclerviewUtils.OnRetryListener onRetryListener, LayoutTemplateCardCompleteBinding completeBinding) {
        this.completeBinding = completeBinding;
        this.onRetryListener = onRetryListener;

        init();
    }

    public CardStackRecyclerUtils(LayoutTemplateCardCompleteBinding completeBinding) {
        this.completeBinding = completeBinding;

        init();
    }

    private void init() {

        completeBinding.setIsDataReady(false);
        completeBinding.setIsFeedbackRequired(false);
        completeBinding.setIsLoading(true);

        cardStackBinding = completeBinding.includeTemplateData;

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

    public void showDataAndLoading() {
        setItemVisibility(true, true, false);
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

    public CardStackView getCardStackView() {
        return cardStackBinding.cardStackView;
    }

}
