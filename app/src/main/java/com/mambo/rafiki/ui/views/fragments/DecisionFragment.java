package com.mambo.rafiki.ui.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Comment;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.data.entities.Reason;
import com.mambo.rafiki.databinding.FragmentDecisionBinding;
import com.mambo.rafiki.ui.adapters.PagerAdapter;
import com.mambo.rafiki.ui.viewmodels.DecisionViewModel;
import com.mambo.rafiki.ui.views.CommentBottomSheet;
import com.mambo.rafiki.ui.views.LoadingDialog;
import com.mambo.rafiki.ui.views.ReasonBottomSheet;
import com.mambo.rafiki.utils.ConstantsUtil;
import com.mambo.rafiki.utils.SharedPrefsUtil;
import com.mambo.rafiki.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.List;

public class DecisionFragment extends Fragment implements ReasonBottomSheet.ReasonListener, CommentBottomSheet.CommentListener, OverviewFragment.OnDecisionListener {

    private FragmentDecisionBinding binding;
    private DecisionViewModel viewModel;
    private Decision decision;

    public boolean isAPro = false;
    public boolean isACon = false;

    public boolean isDeciding = false;
    public boolean isDeleting = false;
    public boolean isArchiving = false;
    public boolean isUpdating = false;

    private SnackBarUtils snackBarUtils;
    private LoadingDialog loadingDialog;
    private NavController navController;
    private SharedPrefsUtil prefsUtil;

    public DecisionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String json = requireArguments().getString(ConstantsUtil.EXTRA);
        decision = new Gson().fromJson(json, Decision.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_decision, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsUtil = SharedPrefsUtil.getInstance(requireActivity().getApplication());
        snackBarUtils = SnackBarUtils.getInstance();

        loadingDialog = LoadingDialog.getInstance();

        navController = Navigation.findNavController(view);

        NavigationUI.setupWithNavController(binding.toolbarDecision, navController);

        binding.fabDecision.setOnClickListener(v -> addReasons());

        makeSnackBar();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(DecisionViewModel.class);
        viewModel.init(decision);

        viewModel.reasonResponse.observe(getViewLifecycleOwner(), response -> {
            if (response != null) {

                snackBarUtils.dismiss();

                if (response.isSuccessful()) {

                    viewModel.setReason(null);

                    if (isAPro || isACon) {

                        viewModel.update();

                        isAPro = false;
                        isACon = false;

                    }


                } else {

                    Snackbar snackbar = Snackbar.make(binding.layoutDecisionMain, response.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.setAction("RETRY", view -> {

                        if (isAPro) {

                            viewModel.addPro();

                        } else if (isACon) {

                            viewModel.addCon();

                        }

                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });

        viewModel.decisionResponse.observe(getViewLifecycleOwner(), response -> {
            if (response != null) {

                if (loadingDialog != null)
                    loadingDialog.hideDialog();

                if (response.isSuccessful()) {

                    if (isDeleting) {

                        isDeleting = false;
                        Toast.makeText(requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.action_decisionFragment_to_homeFragment);

                    } else if (isArchiving) {

                        isArchiving = false;
                        updateMenu();
                        setViewPager();
                        Toast.makeText(requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();

                    } else if (isUpdating) {

                        isUpdating = false;
                        setViewPager();
                        updateMenu();

                    } else if (isDeciding) {

                        isDeciding = false;
                        setViewPager();
                        updateMenu();

                    }

                } else {

                    Snackbar snackbar = Snackbar.make(binding.layoutDecisionMain, response.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.setAction("RETRY", view -> {

                        if (isDeleting) {

                            viewModel.deleteDecision();

                        } else if (isArchiving) {

                            viewModel.archiveDecision();

                        } else if (isDeciding) {

                            viewModel.retryDeciding();

                        }

                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });

        setUpMenu();
        setViewPager();

    }

    private void setUpMenu() {

        if (prefsUtil.getUserId().equals(viewModel.decision.getValue().getUserID()))
            binding.toolbarDecision.inflateMenu(R.menu.menu_decision);

        updateMenu();

        binding.toolbarDecision.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.menu_item_unarchive:
                case R.id.menu_item_archive:

                    isArchiving = true;
                    loadingDialog.showDialog(requireContext());
                    viewModel.archiveDecision();

                    return true;

                case R.id.menu_item_private:
                case R.id.menu_item_public:

                    isUpdating = true;
                    loadingDialog.showDialog(requireContext());
                    viewModel.changeDecisionVisibility();

                    return true;

                case R.id.menu_item_delete:

                    isDeleting = true;
                    loadingDialog.showDialog(requireContext());
                    viewModel.deleteDecision();

                    return true;
            }

            return false;
        });
    }

    private void updateMenu() {
        if (prefsUtil.getUserId().equals(viewModel.decision.getValue().getUserID())) {

            MenuItem archive = binding.toolbarDecision.getMenu().findItem(R.id.menu_item_archive);
            MenuItem unarchive = binding.toolbarDecision.getMenu().findItem(R.id.menu_item_unarchive);

            archive.setVisible(false);
            unarchive.setVisible(false);

            MenuItem disclose = binding.toolbarDecision.getMenu().findItem(R.id.menu_item_public);
            MenuItem conceal = binding.toolbarDecision.getMenu().findItem(R.id.menu_item_private);

            disclose.setVisible(false);
            conceal.setVisible(false);

            if (viewModel.decision.getValue().isArchived()) {

                archive.setVisible(false);
                unarchive.setVisible(true);

            } else {

                archive.setVisible(true);
                unarchive.setVisible(false);

                if (!viewModel.decision.getValue().isDecided())
                    if (viewModel.decision.getValue().isPublic()) {
                        disclose.setVisible(false);
                        conceal.setVisible(true);
                    } else {
                        disclose.setVisible(true);
                        conceal.setVisible(false);
                    }
            }

        }
    }

    private void makeSnackBar() {
        snackBarUtils.makeSnackBar(requireContext(), binding.layoutDecisionMain, "saving");
    }

    private void addReasons() {

        int position = binding.tabLayoutDecision.getSelectedTabPosition();

        switch (position) {

            case 1:
                addReason(ReasonBottomSheet.PRO_ID);
                break;

            case 2:
                addReason(ReasonBottomSheet.CON_ID);
                break;

            case 3:
                CommentBottomSheet commentBottomSheet = new CommentBottomSheet(this);
                commentBottomSheet.show(getChildFragmentManager(), commentBottomSheet.getTag());
                break;

            default:
                Toast.makeText(requireContext(), "Yeah working on it", Toast.LENGTH_SHORT).show();
                break;


        }

    }

    private void addReason(int reasonID) {
        ReasonBottomSheet bottomSheet = new ReasonBottomSheet(reasonID, this);
        bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
    }

    private void setViewPager() {

        List<Fragment> mFragmentList = new ArrayList<>();

        mFragmentList.add(new OverviewFragment(this));
        mFragmentList.add(new ProsFragment());
        mFragmentList.add(new ConsFragment());

        if (viewModel.decision.getValue().isPublic()) {
            mFragmentList.add(new CommentsFragment());
        } else {
            if (viewModel.decision.getValue().getComments() != 0)
                mFragmentList.add(new CommentsFragment());
        }

        String[] titles = new String[]{"Overview", "Pros", "Cons", "Reviews"};
        Integer[] icons = new Integer[]{
                R.drawable.ic_baseline_home_24,
                R.drawable.ic_baseline_thumb_up_24,
                R.drawable.ic_baseline_thumb_down_24,
                R.drawable.ic_baseline_mode_comment_24
        };

        PagerAdapter adapter = new PagerAdapter(requireActivity(), mFragmentList);

        binding.viewPagerDecision.setAdapter(adapter);
        binding.viewPagerDecision.setUserInputEnabled(true);

        new TabLayoutMediator(binding.tabLayoutDecision, binding.viewPagerDecision, true, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                tab.setText(titles[position]);
                tab.setIcon(icons[position]);
            }
        }).attach();

        hideFab();
        binding.tabLayoutDecision.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 1:
                    case 2:
                    case 3:
                        showFab();
                        break;

                    case 0:
                    default:
                        hideFab();
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });

    }

    private void hideFab() {
        binding.fabDecision.hide();
    }

    private void showFab() {
        if (viewModel.decision.getValue() != null)
            if (viewModel.decision.getValue().isArchived() || viewModel.decision.getValue().isDecided())
                binding.fabDecision.hide();
            else
                binding.fabDecision.show();
    }

    @Override
    public void onReasonSaved(Reason reason) {

        viewModel.setReason(reason);

        int tabPosition = binding.tabLayoutDecision.getSelectedTabPosition();
        snackBarUtils.show();

        switch (tabPosition) {

            case 1:
                isAPro = true;
                viewModel.addPro();
                break;

            case 2:
                isACon = true;
                viewModel.addCon();

                break;

            case 3:
            default:
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null) {
            binding = null;
        }
    }

    @Override
    public void onCommentSaved(Comment comment) {

        viewModel.setComment(comment);

        snackBarUtils.show();

        viewModel.addComment();

    }

    @Override
    public void onDecisionCompleted(int decision) {
        isDeciding = true;
        loadingDialog.showDialog(requireContext());
        viewModel.decide(decision);

    }
}