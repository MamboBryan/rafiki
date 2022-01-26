package com.mambo.rafiki.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Comment;
import com.mambo.rafiki.databinding.ItemCommentBinding;
import com.mambo.rafiki.utils.ViewUtils;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private OnCommentClickListener mListener;
    private List<Comment> comments;

    private String userId;
    private boolean isDecided;

    public CommentAdapter(String userId, boolean isDecided, OnCommentClickListener onCommentClickListener) {
        this.userId = userId;
        this.isDecided = isDecided;
        this.mListener = onCommentClickListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ItemCommentBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_comment, parent, false);
        return new CommentViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        if (comments != null) {

            Comment current = comments.get(position);
            Context context = holder.binding.getRoot().getContext();

            holder.binding.setComment(current);

            if (current.isUpdated())
                holder.binding.layoutEdited.setVisibility(View.VISIBLE);
            else
                holder.binding.layoutEdited.setVisibility(View.GONE);

            if (userId.equals(current.getUserID()) && !isDecided) {

                holder.binding.ivOptions.setVisibility(View.VISIBLE);
                holder.binding.ivOptions.setBackgroundResource(ViewUtils.getSelectableBackground(context));

                holder.binding.ivOptions.setOnClickListener(v -> {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, holder.binding.ivOptions);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_reason);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_reason_update:
                                    mListener.onCommentClicked(R.id.menu_reason_update, position);
                                    return true;

                                case R.id.menu_reason_delete:
                                    mListener.onCommentClicked(R.id.menu_reason_delete, position);
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                });
            }

        } else {
            // Covers the case of data not being ready yet.
            holder.binding.setComment(new Comment());
        }
    }

    @Override
    public int getItemCount() {
        if (comments != null)
            return comments.size();
        else return 0;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public List<Comment> getData() {
        return comments;
    }

    public void removeItem(int position) {
        comments.remove(position);
        notifyDataSetChanged();

        if (comments.isEmpty()) {
            mListener.onCommentsCleared();
        }
    }

    public void restoreItem(Comment item, int position) {
        comments.add(position, item);
        notifyDataSetChanged();
    }


    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private ItemCommentBinding binding;
        private OnCommentClickListener onCommentClickListener;

        public CommentViewHolder(ItemCommentBinding binding, OnCommentClickListener onCommentClickListener) {
            super(binding.getRoot());

            this.binding = binding;
            this.onCommentClickListener = onCommentClickListener;

            this.binding.getRoot().setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
//            onCommentClickListener.onCommentClicked(comments.get(getAdapterPosition() - 1));
            return true;
        }

    }

    public interface OnCommentClickListener {
        void onCommentClicked(int MENU_ITEM, int position);

        void onCommentsCleared();
    }

}
