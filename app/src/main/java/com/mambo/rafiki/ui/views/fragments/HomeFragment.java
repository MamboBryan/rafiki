package com.mambo.rafiki.ui.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.FragmentMainBinding;
import com.mambo.rafiki.ui.adapters.PagerAdapter;
import com.mambo.rafiki.ui.adapters.PrivateAdapter;
import com.mambo.rafiki.ui.interfaces.OnDecisionClickListener;
import com.mambo.rafiki.ui.viewmodels.HomeViewModel;
import com.mambo.rafiki.ui.views.DecisionBottomSheet;
import com.mambo.rafiki.utils.ConstantsUtil;
import com.mambo.rafiki.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PrivateAdapter.OnDecisionClickListener, OnDecisionClickListener, DecisionBottomSheet.DecisionListener {

    private FragmentMainBinding binding;

    private HomeViewModel viewModel;
    private NavController navController;

    private SnackBarUtils snackBarUtils;

    public HomeFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        snackBarUtils = SnackBarUtils.getInstance();

        binding.toolbarMain.inflateMenu(R.menu.menu_main);
        binding.toolbarMain.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.menu_item_refresh:
                    updateDecisions();
                    return true;

                case R.id.archivedFragment:
                    navController.navigate(R.id.action_homeFragment_to_archivedFragment);
                    return true;

                case R.id.menu_item_setting:
                    navController.navigate(R.id.action_homeFragment_to_settingsFragment);
                    return true;
            }
            return false;
        });
        binding.fabAddDecision.setOnClickListener(v -> {
            DecisionBottomSheet bottomSheet = new DecisionBottomSheet(this);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
        });

        NavigationUI.setupWithNavController(binding.toolbarMain, navController);

        setViewPager();
        setFabVisibility();

    }

    private void updateDecisions() {
        if (binding.tabLayoutMain.getSelectedTabPosition() == 0)
            viewModel.updateRandomDecisions();
        else
            viewModel.updateDecisions();
    }

    private void setViewPager() {

        List<Fragment> mFragmentList = new ArrayList<>();

        mFragmentList.add(new DiscoverFragment(this));
        mFragmentList.add(new DecisionsFragment(this));

        String[] titles = new String[]{"Discover", "Decisions"};

        PagerAdapter adapter = new PagerAdapter(requireActivity(), mFragmentList);

        binding.viewPagerMain.setAdapter(adapter);
        binding.viewPagerMain.setUserInputEnabled(false);
        new TabLayoutMediator(binding.tabLayoutMain, binding.viewPagerMain,
                (tab, position) -> tab.setText(titles[position])
        ).attach();

        binding.tabLayoutMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setFabVisibility();
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

    private void setFabVisibility() {
        if (binding.tabLayoutMain.getSelectedTabPosition() == 0) {
            binding.fabAddDecision.hide();
        } else {
            binding.fabAddDecision.show();
        }
    }

    private void showAddingDecision(boolean isComplete) {
        if (isComplete) {
            snackBarUtils.dismiss();
            binding.fabAddDecision.setEnabled(true);
        } else {
            snackBarUtils.makeSnackBar(requireContext(), binding.layoutCoordinatorMain, "Adding decision");
            snackBarUtils.show();
            binding.fabAddDecision.setEnabled(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toggleScrollingCoordinatorLayout();

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewModel.getResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                if (response.isSuccessful()) {
                    showAddingDecision(response.isSuccessful());
                }

            }
        });

    }

    private void toggleScrollingCoordinatorLayout() {

        Toolbar toolbar = binding.toolbarMain;
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        if (binding.tabLayoutMain.getSelectedTabPosition() == 0) {
            params.setScrollFlags(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.updateDecisions();
    }

    @Override
    public void onDecisionClicked(Decision decision) {

        String json = new Gson().toJson(decision);

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtil.EXTRA, json);

        navController.navigate(R.id.action_homeFragment_to_decisionFragment, bundle);

    }

    @Override
    public void onDecisionsCleared() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null) {
            binding = null;
        }
    }

    @Override
    public void onDecisionSaved(Decision decision) {

        showAddingDecision(false);
        viewModel.insert(decision);

    }

    @Override
    public void onDecisionClicked(DocumentSnapshot snapshot) {

        Decision decision = snapshot.toObject(Decision.class);
        decision.setId(snapshot.getId());

        String json = new Gson().toJson(decision);

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtil.EXTRA, json);

        navController.navigate(R.id.action_homeFragment_to_decisionFragment, bundle);
    }
}