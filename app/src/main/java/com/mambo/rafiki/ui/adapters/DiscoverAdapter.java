package com.mambo.rafiki.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.ItemDecisionPublicBinding;
import com.mambo.rafiki.ui.interfaces.OnDecisionClickListener;
import com.mambo.rafiki.utils.DecisionUtils;

public class DiscoverAdapter extends FirestorePagingAdapter<Decision, DiscoverAdapter.DecisionViewHolder> {
    private OnDecisionClickListener mListener;

    public DiscoverAdapter(@NonNull FirestorePagingOptions<Decision> options, OnDecisionClickListener mListener) {
        super(options);

        this.mListener = mListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull DecisionViewHolder holder, int position, @NonNull Decision model) {
        Context context = holder.binding.getRoot().getContext();

        holder.binding.setDecision(model);

        switch (model.getSuggestion()) {
            case DecisionUtils.YES:
            case DecisionUtils.NO:
            case DecisionUtils.UNCERTAIN:
                holder.binding.cardDecisionOverview.setCardBackgroundColor(context.getResources().getColor(R.color.primary00));
                break;
            default:
                holder.binding.cardDecisionOverview.setCardBackgroundColor(context.getResources().getColor(R.color.primary10));
                break;
        }
    }

    @NonNull
    @Override
    public DecisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        ItemDecisionPublicBinding binding = DataBindingUtil.inflate(mInflater, R.layout.item_decision_public, parent, false);
        return new DecisionViewHolder(binding, mListener);
    }

    class DecisionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemDecisionPublicBinding binding;
        private OnDecisionClickListener onDecisionClickListener;

        public DecisionViewHolder(ItemDecisionPublicBinding binding, OnDecisionClickListener onDecisionClickListener) {
            super(binding.getRoot());

            this.binding = binding;
            this.onDecisionClickListener = onDecisionClickListener;

            this.binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDecisionClickListener.onDecisionClicked(getItem(getAdapterPosition()));
        }
    }

}
