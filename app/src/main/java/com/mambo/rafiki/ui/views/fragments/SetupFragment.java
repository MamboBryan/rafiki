package com.mambo.rafiki.ui.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.User;
import com.mambo.rafiki.databinding.FragmentSetupBinding;
import com.mambo.rafiki.ui.viewmodels.SetupViewModel;
import com.mambo.rafiki.utils.ConstantsUtil;
import com.mambo.rafiki.utils.NetworkUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

public class SetupFragment extends Fragment {

    private User user;
    private String token;

    private FragmentSetupBinding binding;
    private SetupViewModel viewModel;
    private NavController navController;
    private SharedPrefsUtil prefsUtil;

    public SetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String json = requireArguments().getString(ConstantsUtil.EXTRA);
            user = new Gson().fromJson(json, User.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        prefsUtil = SharedPrefsUtil.getInstance(requireActivity().getApplication());

        if (user == null)
            user = prefsUtil.getUserData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);

        checkInternetConnectivity();

        viewModel.response.observe(getViewLifecycleOwner(), response -> {
            if (response != null) {

                if (response.isSuccessful()) {

                    prefsUtil.setUpIsCompleted();
                    navController.navigate(R.id.action_setupFragment_to_homeFragment);
                    navController.getGraph().setStartDestination(R.id.homeFragment);

                } else {

                    Snackbar snackbar = Snackbar.make(binding.layoutSetup, response.getMessage(), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("RETRY", view -> {
                        viewModel.updateUser();
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });

        binding.btnRetry.setOnClickListener(v -> viewModel.updateUser());

    }

    private void checkInternetConnectivity() {
        if (NetworkUtils.isConnected(requireContext())) {
            loadUserDetails();
            updateUserToken();
        } else {
            openNetworkDialog();
        }
    }

    private void openNetworkDialog() {


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        builder.setCancelable(false)
                .setTitle("No Internet connection")
                .setMessage("Please check your connection again, or connect  to WiFi to continue with the setup process")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("ok", (dialog, which) -> {
                    checkInternetConnectivity();
                })

                .show();

    }

    private void loadUserDetails() {
        viewModel.setUser(user);
        updateUserToken();
    }

    private void updateUserToken() {
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    token = task.getResult();
                    viewModel.updateUserToken(token);
                    viewModel.updateUser();
                } else {
                    binding.btnRetry.setVisibility(View.VISIBLE);
                }
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