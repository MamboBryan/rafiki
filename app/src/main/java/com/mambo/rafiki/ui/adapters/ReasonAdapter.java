package com.mambo.rafiki.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Reason;
import com.mambo.rafiki.databinding.ItemReasonBinding;

import java.util.List;

public class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ReasonViewHolder> {

    public static final int PRO = 1001;
    public static final int CON = 1002;

    private int REASON_TYPE;

    private ReasonClickListener mListener;
    private List<Reason> reasons;

    private String userId;
    private boolean isDecided;

    public ReasonAdapter(int REASON_TYPE, String userId, boolean isDecided, ReasonClickListener reasonClickListener) {
        this.REASON_TYPE = REASON_TYPE;
        this.userId = userId;
        this.isDecided = isDecided;
        this.mListener = reasonClickListener;
    }

    @NonNull
    @Override
    public ReasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        ItemReasonBinding binding = DataBindingUtil.inflate(mInflater, R.layout.item_reason, parent, false);
        return new ReasonViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReasonViewHolder holder, int position) {
        if (reasons != null) {
            Reason current = reasons.get(position);

            Context context = holder.binding.getRoot().getContext();

            holder.binding.setReason(current);

            CardView cardView = holder.binding.cardIntensity;

            switch (current.getWeight()) {
                case 1:
                    if (REASON_TYPE == PRO)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pros01));
                    else if (REASON_TYPE == CON)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.con01));
                    break;

                case 2:
                    if (REASON_TYPE == PRO)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pros02));
                    else if (REASON_TYPE == CON)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.con02));
                    break;

                case 3:
                    if (REASON_TYPE == PRO)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pros03));
                    else if (REASON_TYPE == CON)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.con03));
                    break;

                case 4:
                    if (REASON_TYPE == PRO)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pros04));
                    else if (REASON_TYPE == CON)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.con04));
                    break;

                case 5:
                    if (REASON_TYPE == PRO)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pros05));
                    else if (REASON_TYPE == CON)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.con05));
                    break;
                default:
                    if (REASON_TYPE == PRO)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.pros));
                    else if (REASON_TYPE == CON)
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cons));
                    break;
            }

            if (!userId.equals(current.getUserID()) || isDecided) {
                holder.binding.ivReasonMenu.setVisibility(View.GONE);
            }

            holder.binding.ivReasonMenu.setOnClickListener(v -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.binding.ivReasonMenu);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_reason);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_reason_update:

                                mListener.onReasonClicked(R.id.menu_reason_update, position);
                                return true;
                            case R.id.menu_reason_delete:
                                mListener.onReasonClicked(R.id.menu_reason_delete, position);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            });

        } else {
            // Covers the case of data not being ready yet.
            holder.binding.setReason(new Reason());
        }
    }

    @Override
    public int getItemCount() {
        if (reasons != null)
            return reasons.size();
        else return 0;
    }

    public void setReasons(List<Reason> reasons) {
        this.reasons = reasons;
        notifyDataSetChanged();
    }

    public List<Reason> getData() {
        return reasons;
    }

    public Reason getItem(int position) {
        return reasons.get(position);
    }

    public void removeItem(int position) {
        reasons.remove(position);
        notifyDataSetChanged();

        if (reasons.isEmpty()) {
            mListener.onReasonsCleared();
        }
    }

    public void restoreItem(Reason item, int position) {
        reasons.add(position, item);
        notifyDataSetChanged();
    }

    public void addItem(Reason item) {
        reasons.add(item);
        notifyDataSetChanged();
    }

    class ReasonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemReasonBinding binding;
        private ReasonClickListener reasonClickListener;

        public ReasonViewHolder(ItemReasonBinding binding, ReasonClickListener reasonClickListener) {
            super(binding.getRoot());

            this.binding = binding;

            this.reasonClickListener = reasonClickListener;
//            this.binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            reasonClickListener.onReasonClicked(reasons.get(getAdapterPosition() - 1));
        }
    }

    public interface ReasonClickListener {

        void onReasonClicked(int MENU_ITEM, int position);

        void onReasonsCleared();
    }

}
