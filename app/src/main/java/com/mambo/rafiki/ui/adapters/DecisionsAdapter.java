package com.mambo.rafiki.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.ItemDecisionBinding;
import com.mambo.rafiki.ui.interfaces.DecisionsListener;
import com.mambo.rafiki.ui.interfaces.OnDecisionClickListener;
import com.mambo.rafiki.utils.DecisionUtils;

public class DecisionsAdapter extends FirestoreRecyclerAdapter<Decision, DecisionsAdapter.DecisionViewHolder> implements DecisionsListener {

    private OnDecisionClickListener mListener;

    public DecisionsAdapter(@NonNull FirestoreRecyclerOptions<Decision> options, OnDecisionClickListener mListener) {
        super(options);
        this.mListener = mListener;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();

        if (getItemCount() <= 0)
            isEmpty();
        else
            isNotEmpty();
    }


    @Override
    protected void onBindViewHolder(@NonNull DecisionViewHolder holder, int position, @NonNull Decision model) {

        Context context = holder.binding.getRoot().getContext();

        holder.itemView.setTag(model.getId());
        holder.binding.setDecision(model);

        switch (model.getSuggestion()) {
            case DecisionUtils.YES:
                holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.pros));
                break;
            case DecisionUtils.NO:
                holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.cons));
                break;
            case DecisionUtils.UNCERTAIN:
            default:
                holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.primary60));
                break;
        }

        if (model.isDecided()) {
            switch (model.getDecision()) {
                case DecisionUtils.YES:
                    holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.greenDark));
                    break;
                case DecisionUtils.NO:
                    holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.redDark));
                    break;
                case DecisionUtils.UNCERTAIN:
                default:
                    holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.primary80));
                    break;
            }
        }

        if (!model.isPublic()) {
            holder.binding.ivDecisionComment.setVisibility(View.GONE);
            holder.binding.tvDecisionComment.setVisibility(View.GONE);
        } else {
            holder.binding.ivDecisionComment.setVisibility(View.VISIBLE);
            holder.binding.tvDecisionComment.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public DecisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemDecisionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_decision, parent, false);
        return new DecisionViewHolder(binding, mListener);
    }

    @Override
    public void isEmpty() {

    }

    @Override
    public void isNotEmpty() {

    }

    class DecisionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemDecisionBinding binding;
        private OnDecisionClickListener onDecisionClickListener;

        public DecisionViewHolder(ItemDecisionBinding binding, OnDecisionClickListener onDecisionClickListener) {
            super(binding.getRoot());

            this.binding = binding;
            this.onDecisionClickListener = onDecisionClickListener;

            this.binding.layoutDecision.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDecisionClickListener.onDecisionClicked(getItem(getAdapterPosition() - 1));
        }
    }

}
