package com.mambo.rafiki.ui.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.mambo.rafiki.R;
import com.mambo.rafiki.databinding.FragmentSettingsBinding;
import com.mambo.rafiki.ui.views.LoadingDialog;
import com.mambo.rafiki.utils.ConstantsUtil;
import com.mambo.rafiki.utils.FeedbackUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private NavController navController;
    private LoadingDialog loadingDialog;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        NavigationUI.setupWithNavController(binding.toolbarSettings, navController);

        loadingDialog = LoadingDialog.getInstance();

        binding.layoutFeature.setOnClickListener(v -> openFeatureFeedback(FeedbackUtils.FEATURE));
        binding.layoutBug.setOnClickListener(v -> openFeatureFeedback(FeedbackUtils.BUG));
        binding.layoutFeedback.setOnClickListener(v -> openFeatureFeedback(FeedbackUtils.FEEDBACK));
        binding.layoutLogOut.setOnClickListener(v -> openLogOutDialog());

    }

    private void openLogOutDialog() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        builder.setCancelable(false)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to logout?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("yes", (dialog, which) -> {
                    loadingDialog.showDialog(requireContext());
                    logOutUser();
                })
                .setNegativeButton("no", (dialog, which) -> {

                })
                .show();

    }

    private void logOutUser() {

        SharedPrefsUtil prefsUtil = SharedPrefsUtil.getInstance(requireActivity().getApplication());
        prefsUtil.clearPreferences();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        googleSignInClient.signOut();

        navController.navigate(R.id.action_settingsFragment_to_authenticationFragment);
        loadingDialog.hideDialog();

    }

    private void openFeatureFeedback(String feedback) {

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtil.EXTRA, feedback);

        navController.navigate(R.id.action_settingsFragment_to_feedbackFragment, bundle);
    }

}