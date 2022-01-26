package com.mambo.rafiki.ui.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.FragmentOverviewBinding;
import com.mambo.rafiki.ui.viewmodels.DecisionViewModel;
import com.mambo.rafiki.ui.views.DescriptionBottomSheet;
import com.mambo.rafiki.utils.DecisionUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

public class OverviewFragment extends Fragment implements DescriptionBottomSheet.DescriptionListener {

    private FragmentOverviewBinding binding;
    private DecisionViewModel viewModel;

    private boolean isDeciding;
    private boolean isContentEditing;
    private boolean isEditingDescription;
    private SharedPrefsUtil prefsUtil;

    private OnDecisionListener mListener;

    public OverviewFragment(OnDecisionListener onDecisionListener) {
        // Required empty public constructor
        this.mListener = onDecisionListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.
                inflate(inflater, R.layout.fragment_overview, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isDeciding = false;
        isContentEditing = false;
        isEditingDescription = false;

        binding.toggleEditButton.bind(binding.tvToggleContent);
        binding.toggleEditButtonDescription.bind(binding.tvDecisionDescription);

//        binding.tvToggleContent.setOnClickListener(v -> toggleContentEditing(false));
//        binding.toggleEditButton.setOnClickListener(v -> toggleContentEditing(true));
//
//        binding.tvDecisionDescription.setOnClickListener(v -> toggleDescriptionEditing(false));
//        binding.toggleEditButtonDescription.setOnClickListener(v -> toggleDescriptionEditing(true));

        binding.ivDescriptionView.setOnClickListener(v -> toggleDescriptionVisibility());
        binding.constDecisionDescription.setVisibility(View.GONE);

        binding.constDescriptionAdd.setOnClickListener(v -> {

            DescriptionBottomSheet descriptionBottomSheet = new DescriptionBottomSheet(this);
            descriptionBottomSheet.show(requireActivity().getSupportFragmentManager(), descriptionBottomSheet.getTag());

        });

        binding.btnDecide.setOnClickListener(v -> {
            openDecideDialog();
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefsUtil = SharedPrefsUtil.getInstance(requireActivity().getApplication());

        viewModel = new ViewModelProvider(requireActivity()).get(DecisionViewModel.class);
        binding.setViewModel(viewModel);

        viewModel.decision.observe(getViewLifecycleOwner(), decision -> {
            if (decision != null)
                update();
        });

        updateViews();
        updateEmoji();
        updateDescriptionVisibility();

    }

    private void updateViews() {

        Decision decision = viewModel.decision.getValue();

        if (decision != null) {

            if (!decision.isPublic() && decision.getComments() == 0) {
                binding.cardComments.setVisibility(View.GONE);
            }

            if (decision.isDecided() || decision.isArchived()) {
                binding.btnDecide.setVisibility(View.GONE);
            }

            if (!prefsUtil.isUser(decision.getUserID())) {
                binding.toggleEditButton.setVisibility(View.GONE);
                binding.btnDecide.setVisibility(View.GONE);
            } else {
                binding.tvToggleContent.setOnClickListener(v -> toggleContentEditing(false));
                binding.toggleEditButton.setOnClickListener(v -> toggleContentEditing(true));

                binding.tvDecisionDescription.setOnClickListener(v -> toggleDescriptionEditing(false));
                binding.toggleEditButtonDescription.setOnClickListener(v -> toggleDescriptionEditing(true));
            }

            String description = decision.getDescription();

            if (!decision.isPublic()) {
                binding.cardDescriptionView.setVisibility(View.GONE);
            } else {

                if (!prefsUtil.isUser(decision.getUserID()) || decision.isDecided() || decision.isArchived()) {
                    binding.ivDescriptionDelete.setVisibility(View.GONE);
                    binding.toggleEditButtonDescription.setVisibility(View.GONE);
                } else {
                    if (description == null || TextUtils.isEmpty(description)) {
                        binding.cardDescriptionView.setVisibility(View.GONE);
                    }
                }
            }

            if (!prefsUtil.isUser(decision.getUserID())) {
                binding.cardDescriptionAdd.setVisibility(View.GONE);
            } else {
                if (decision.isPublic()) {
                    if (description == null || TextUtils.isEmpty(description))
                        binding.cardDescriptionAdd.setVisibility(View.VISIBLE);
                    else
                        binding.cardDescriptionAdd.setVisibility(View.GONE);
                } else {
                    binding.cardDescriptionAdd.setVisibility(View.GONE);
                }
            }

            if (!decision.isDecided()) {
                binding.cardDecisionDecided.setVisibility(View.GONE);
            } else {
                showDecidedViews();
            }

            if (!decision.isArchived()) {
                binding.cardDecisionArchived.setVisibility(View.GONE);
            } else {
                binding.cardDecisionArchived.setVisibility(View.VISIBLE);
                binding.cardDescriptionAdd.setVisibility(View.GONE);
            }

            if (decision.isDecided() || decision.isArchived()) {
                binding.toggleEditButton.unbind(binding.tvToggleContent);
                binding.toggleEditButtonDescription.unbind(binding.tvDecisionDescription);
            }
        }

    }

    private void showDecidedViews() {
        binding.cardDecisionDecided.setVisibility(View.VISIBLE);
        binding.cardDescriptionAdd.setVisibility(View.GONE);

        if (viewModel.decision.getValue().getDecision() == DecisionUtils.YES) {
            Glide.with(requireContext()).load(R.drawable.ic_baseline_thumb_up_24).centerCrop().into(binding.ivDecisionDecidedIcon);
            binding.ivDecisionDecidedIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.greenDark), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.cardDecisionDecided.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greenDark));
        } else {
            Glide.with(requireContext()).load(R.drawable.ic_baseline_thumb_down_24).centerCrop().into(binding.ivDecisionDecidedIcon);
            binding.ivDecisionDecidedIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.redDark), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.cardDecisionDecided.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.redDark));
        }
    }

    private void openDecideDialog() {

        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Decide");
        builder.setMessage("Are you going to do it?");
        builder.setPositiveButton("YES", (dialog, which) -> {
            // decision has been decided to be done
            mListener.onDecisionCompleted(DecisionUtils.YES);

        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            // decision has been decided not to be done
            mListener.onDecisionCompleted(DecisionUtils.NO);
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void toggleDescriptionVisibility() {

        if (binding.constDecisionDescription.getVisibility() == View.GONE) {

            binding.constDecisionDescription.setVisibility(View.VISIBLE);
            Glide.with(requireContext()).load(R.drawable.ic_baseline_keyboard_arrow_up_24).into(binding.ivDescriptionView);

        } else {

            binding.constDecisionDescription.setVisibility(View.GONE);
            Glide.with(requireContext()).load(R.drawable.ic_baseline_keyboard_arrow_down_24).into(binding.ivDescriptionView);

        }

    }

    private void toggleDescriptionEditing(boolean isButton) {

        if (!isButton)
            binding.toggleEditButtonDescription.performClick();

        isEditingDescription = !isEditingDescription;

        if (!isEditingDescription)
            updateDecisionDescription();

    }

    private void toggleContentEditing(boolean isButton) {

        if (!isButton)
            binding.toggleEditButton.performClick();

        isContentEditing = !isContentEditing;

        if (!isContentEditing)
            updateDecisionContent();

    }

    private void updateDecisionContent() {

        String update = binding.tvToggleContent.getEditText().getText().toString();
        String content = viewModel.decision.getValue().getContent();

        if (!content.equals(update)) {

            viewModel.updateContent(update);

        }

    }

    private void updateDecisionDescription() {

        String updatedDescription = binding.tvDecisionDescription.getEditText().getText().toString();
        String currentDescription = viewModel.decision.getValue().getDescription();

        if (currentDescription != null && !currentDescription.equals(updatedDescription)) {

            viewModel.updateDescription(updatedDescription);

        }

    }

    private void hideDescriptionEditVisibility() {

        binding.ivDescriptionDelete.setVisibility(View.GONE);
        binding.toggleEditButtonDescription.setVisibility(View.GONE);

    }

    private void update() {

        binding.setViewModel(viewModel);

        updateViews();
        updateEmoji();
//        updateDescriptionVisibility();

    }

    private void updateEmoji() {
        binding.tvSuggestion.setText(DecisionUtils.getEmojiWithDifference(viewModel.decision.getValue().getDifference()));
    }

    private void updateDescriptionVisibility() {

        if (viewModel.decision.getValue().isPublic()) {

            String description = viewModel.decision.getValue().getDescription();

            if (description == null || TextUtils.isEmpty(description)) {

                if (prefsUtil.isUser(viewModel.decision.getValue().getUserID())) {
                    binding.cardDescriptionView.setVisibility(View.GONE);

                    if (!viewModel.decision.getValue().isArchived() || !viewModel.decision.getValue().isDecided())
                        binding.cardDescriptionAdd.setVisibility(View.VISIBLE);
                } else {
                    binding.cardDescriptionView.setVisibility(View.VISIBLE);
                    binding.cardDescriptionAdd.setVisibility(View.GONE);
                }

            } else {

                binding.cardDescriptionView.setVisibility(View.VISIBLE);
                binding.cardDescriptionAdd.setVisibility(View.GONE);

            }
        }

    }

    @Override
    public void onDescriptionSaved(String description) {
        viewModel.updateDescription(description);
    }

    public interface OnDecisionListener {
        void onDecisionCompleted(int decision);
    }
}