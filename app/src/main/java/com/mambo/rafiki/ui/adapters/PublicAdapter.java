package com.mambo.rafiki.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.ItemDecisionPublicBinding;
import com.mambo.rafiki.utils.DecisionUtils;

import java.util.ArrayList;
import java.util.List;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.DecisionViewHolder> {

    private OnDecisionClickListener mListener;
    private LayoutInflater mInflater;

    private List<Decision> decisions = new ArrayList<>();

    public PublicAdapter(Context context, OnDecisionClickListener mListener) {
        this.mListener = mListener;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public DecisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDecisionPublicBinding binding = DataBindingUtil.inflate(mInflater, R.layout.item_decision_public, parent, false);
        return new DecisionViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DecisionViewHolder holder, int position) {
        if (decisions != null) {
            Decision current = decisions.get(position);
            holder.binding.setDecision(current);

            Context context = holder.binding.getRoot().getContext();

//            holder.binding.cardComplete.setCardBackgroundColor(context.getResources().getColor(R.color.primary10));

            switch (current.getSuggestion()) {
                case DecisionUtils.YES:
                    holder.binding.cardDecisionOverview.setCardBackgroundColor(context.getResources().getColor(R.color.primary00));
                    break;
                case DecisionUtils.NO:
                    holder.binding.cardDecisionOverview.setCardBackgroundColor(context.getResources().getColor(R.color.primary00));
                    break;
                case DecisionUtils.UNCERTAIN:
                default:
                    holder.binding.cardDecisionOverview.setCardBackgroundColor(context.getResources().getColor(R.color.primary00));
                    break;
            }

        } else {
            // Covers the case of data not being ready yet.
            holder.binding.setDecision(new Decision());
        }
    }

    @Override
    public int getItemCount() {
        if (decisions != null)
            return decisions.size();
        else return 0;
    }

    public void setData(List<Decision> decisions) {
        this.decisions = decisions;
        notifyDataSetChanged();
    }

    public List<Decision> getData() {
        return decisions;
    }

    public Decision getItem(int position) {
        return decisions.get(position);
    }

    public void addItem(Decision item) {
        decisions.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(Decision item) {

        if (decisions.contains(item)) {
            decisions.remove(item);
            notifyDataSetChanged();
        }

//        if (decisions.isEmpty()) {
//            mListener.onDecisionsCleared();
//        }
    }

    public void updateItem(Decision item) {

        int position = -1;

        for (Decision reason : decisions) {

            if (reason.getId().equals(item.getId())) {
                position = decisions.indexOf(reason);
            }
        }

        if (position != -1) {
            decisions.set(position, item);
            notifyItemChanged(position);
        }
    }

    public void removeItem(int position) {
        decisions.remove(position);
        notifyDataSetChanged();
    }

    public void restoreItem(Decision item, int position) {
        decisions.add(position, item);
        notifyDataSetChanged();
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
            onDecisionClickListener.onDecisionClicked(decisions.get(getAdapterPosition()));
        }
    }

    public interface OnDecisionClickListener {
        void onDecisionClicked(Decision decision);
    }

}
