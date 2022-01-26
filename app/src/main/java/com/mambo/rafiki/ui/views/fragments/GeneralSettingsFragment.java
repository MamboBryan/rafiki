package com.mambo.rafiki.ui.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.mambo.rafiki.R;
import com.mambo.rafiki.databinding.FragmentGeneralSettingsBinding;
import com.mambo.rafiki.utils.SharedPrefsUtil;

public class GeneralSettingsFragment extends Fragment {

    private FragmentGeneralSettingsBinding binding;
    private SharedPrefsUtil prefsUtil;

    public GeneralSettingsFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_general_settings, container, false);
        prefsUtil = SharedPrefsUtil.getInstance(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        binding.switchDatabase.setOnCheckedChangeListener((view1, isChecked) -> {
            if (isChecked) {
                prefsUtil.saveOffline(true);
            } else {
                prefsUtil.saveOffline(false);
            }
        });
    }
}