package com.mambo.rafiki.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Choice;
import com.mambo.rafiki.databinding.ItemChoiceBinding;
import com.mambo.rafiki.utils.DecisionUtils;

import java.util.List;

public class ChoiceAdapter extends RecyclerView.Adapter<ChoiceAdapter.ChoiceViewHolder> {

    private OnChoiceClickListener mListener;
    private LayoutInflater mInflater;
    private List<Choice> mDecisions;

    public ChoiceAdapter(Context context, OnChoiceClickListener mListener) {
        this.mListener = mListener;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChoiceBinding binding = DataBindingUtil.inflate(mInflater, R.layout.item_choice, parent, false);
        return new ChoiceViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceViewHolder holder, int position) {
        if (mDecisions != null) {
            Choice current = mDecisions.get(position);
            Context context = holder.binding.getRoot().getContext();

            holder.binding.setChoice(current);

            switch (current.getSuggestion()) {
                case DecisionUtils.YES:
                    holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.pros));
                    break;
                case DecisionUtils.NO:
                    holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.cons));
                    break;
                case DecisionUtils.UNCERTAIN:
                default:
                    holder.binding.cardViewFeedback.setCardBackgroundColor(context.getResources().getColor(R.color.primary80));
                    break;
            }

        } else {
            // Covers the case of data not being ready yet.
            holder.binding.setChoice(new Choice());
        }
    }

    @Override
    public int getItemCount() {
        if (mDecisions != null)
            return mDecisions.size();
        else return 0;
    }

    public void setChoices(List<Choice> choices) {
        mDecisions = choices;
        notifyDataSetChanged();
    }

    public List<Choice> getData() {
        return mDecisions;
    }

    public void removeItem(int position) {
        mDecisions.remove(position);
        notifyDataSetChanged();
    }

    public void restoreItem(Choice item, int position) {
        mDecisions.add(position, item);
        notifyDataSetChanged();
    }


    class ChoiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemChoiceBinding binding;
        OnChoiceClickListener onChoiceClickListener;

        public ChoiceViewHolder(ItemChoiceBinding binding, OnChoiceClickListener onChoiceClickListener) {
            super(binding.getRoot());

            this.binding = binding;
            this.onChoiceClickListener = onChoiceClickListener;
            this.binding.getRoot().setOnClickListener(this);
            this.binding.layoutDecision.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onChoiceClickListener.onChoiceClicked(mDecisions.get(getAdapterPosition() - 1));
        }
    }

    public interface OnChoiceClickListener {
        void onChoiceClicked(Choice choice);
    }

}
