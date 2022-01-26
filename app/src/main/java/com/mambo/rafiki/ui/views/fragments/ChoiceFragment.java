package com.mambo.rafiki.ui.views.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Reason;
import com.mambo.rafiki.data.entities.Test;
import com.mambo.rafiki.databinding.DialogReasonBinding;
import com.mambo.rafiki.databinding.FragmentChoiceBinding;
import com.mambo.rafiki.ui.adapters.PagerAdapter;
import com.mambo.rafiki.ui.viewmodels.ChoiceViewModel;
import com.mambo.rafiki.ui.views.ReasonBottomSheet;
import com.mambo.rafiki.utils.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

public class ChoiceFragment extends Fragment implements ReasonBottomSheet.ReasonListener {

    private FragmentChoiceBinding binding;
    private ChoiceViewModel viewModel;
    private Test test;
    private DialogReasonBinding reasonBinding;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    public ChoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String json = getArguments().getString(ConstantsUtil.EXTRA);
        test = new Gson().fromJson(json, Test.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choice, container, false);
        reasonBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_reason, null, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        NavigationUI.setupWithNavController(binding.toolbarChoice, navController);

        setViewPager();

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());

        binding.fabChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchReasonBottomSheet();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ChoiceViewModel.class);
//        viewModel.setShared(test);
        binding.setViewModel(viewModel);

        setFabVisibility();

    }

    private void setFabVisibility() {

    }

    private void setViewPager() {

        List<Fragment> mFragmentList = new ArrayList<>();

//        mFragmentList.add(new OverviewFragment());
        mFragmentList.add(new ProsFragment());
        mFragmentList.add(new ConsFragment());

        String[] titles = new String[]{"Overview", "Pros", "Cons"};

        PagerAdapter adapter = new PagerAdapter(requireActivity(), mFragmentList);

        binding.viewPagerChoice.setAdapter(adapter);
        binding.viewPagerChoice.setUserInputEnabled(true);
        new TabLayoutMediator(binding.tabsChoice, binding.viewPagerChoice,
                (tab, position) -> tab.setText(titles[position])
        ).attach();

        setFabColorAndIcon(R.color.secondaryColor, R.drawable.ic_baseline_check_24_black);

        binding.tabsChoice.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        setFabColorAndIcon(R.color.secondaryColor, R.drawable.ic_baseline_check_24_black);
                        setFabVisibility();
                        break;
                    case 1:
                        setFabColorAndIcon(R.color.pros, R.drawable.ic_baseline_add_24);
                        showFab();
                        break;
                    case 2:
                        setFabColorAndIcon(R.color.cons, R.drawable.ic_baseline_add_24);
                        showFab();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //do nothing
            }
        });
    }

    private void showFab() {
        binding.fabChoice.show();
    }

    private void setFabColorAndIcon(int color, int icon) {
        binding.fabChoice.
                setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
        binding.fabChoice.setImageDrawable(ContextCompat.getDrawable(requireContext(), icon));

    }

    private void launchReasonBottomSheet() {

//        ReasonBottomSheet bottomSheet = new ReasonBottomSheet(ReasonBottomSheet.PRO_ID, this);
//        bottomSheet.show(getParentFragmentManager(), "Reason");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null) {
            binding = null;
        }
    }

    @Override
    public void onReasonSaved(Reason reason) {

    }
}