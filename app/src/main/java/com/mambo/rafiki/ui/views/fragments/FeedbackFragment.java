package com.mambo.rafiki.ui.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Feedback;
import com.mambo.rafiki.databinding.FragmentFeedbackBinding;
import com.mambo.rafiki.ui.viewmodels.FeedbackViewModel;
import com.mambo.rafiki.ui.views.LoadingDialog;
import com.mambo.rafiki.utils.ConstantsUtil;
import com.mambo.rafiki.utils.FeedbackUtils;

public class FeedbackFragment extends Fragment {

    private String mFeedbackType;
    private FragmentFeedbackBinding binding;

    private Feedback feedback;
    private LoadingDialog loadingDialog;
    private FeedbackViewModel viewModel;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFeedbackType = requireArguments().getString(ConstantsUtil.EXTRA);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        NavigationUI.setupWithNavController(binding.toolbarFeedback, navController);

        loadingDialog = LoadingDialog.getInstance();

        String text;
        feedback = new Feedback();

        switch (mFeedbackType) {
            case FeedbackUtils.BUG:
                text = "What problem have you noticed the app has?";
                feedback.setType(FeedbackUtils.BUG);
                break;

            case FeedbackUtils.FEATURE:
                text = "What would you like to be added to help you make better decisions?";
                feedback.setType(FeedbackUtils.FEATURE);
                break;

            case FeedbackUtils.FEEDBACK:
            default:
                text = "what would you like us to know?";
                feedback.setType(FeedbackUtils.FEEDBACK);
                break;
        }

        binding.textView28.setText(text);
        binding.btnFeedback.setOnClickListener(v -> sendFeedback());
    }

    private void sendFeedback() {

        String content = binding.edtFeedback.getText().toString();

        if (!TextUtils.isEmpty(content)) {
            viewModel.setContent(content);

            loadingDialog.showDialog(requireContext());
            viewModel.sendFeedback();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FeedbackViewModel.class);
        viewModel.setFeedback(feedback);

        viewModel.response.observe(getViewLifecycleOwner(), response -> {
            if (response != null) {

                loadingDialog.hideDialog();

                if (response.isSuccessful()) {
                    binding.edtFeedback.getText().clear();
                    viewModel.clearResponse();
                    viewModel.setFeedback(null);
                }

                Toast.makeText(requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null)
            binding = null;
    }
}