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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Reason;
import com.mambo.rafiki.databinding.FragmentTemplateBinding;
import com.mambo.rafiki.ui.adapters.ReasonAdapter;
import com.mambo.rafiki.ui.viewmodels.DecisionViewModel;
import com.mambo.rafiki.ui.views.ReasonBottomSheet;
import com.mambo.rafiki.utils.RecyclerviewUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;
import com.mambo.rafiki.utils.SnackBarUtils;

public class ProsFragment extends Fragment implements RecyclerviewUtils.OnRetryListener, ReasonAdapter.ReasonClickListener, ReasonBottomSheet.ReasonListener {

    private FragmentTemplateBinding binding;
    private RecyclerviewUtils recyclerviewUtils;
    private DecisionViewModel viewModel;
    private XRecyclerView recyclerView;
    private ReasonAdapter adapter;
    private SharedPrefsUtil prefsUtil;

    private boolean isUpdating = false;
    private boolean isDeleting = false;
    private SnackBarUtils snackBarUtils;

    public ProsFragment() {
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_template, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsUtil = SharedPrefsUtil.getInstance(requireActivity().getApplication());
        snackBarUtils = SnackBarUtils.getInstance();
        recyclerviewUtils = RecyclerviewUtils.getInstance(binding.includeTemplate, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(DecisionViewModel.class);

        initRecycler();

        viewModel.pros.observe(getViewLifecycleOwner(), reasons -> {
            if (reasons.isEmpty()) {
                if (viewModel.decision.getValue().isArchived() || viewModel.decision.getValue().isDecided())
                    recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_EMPTY, "Decision has no pros.");
                else
                    recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_EMPTY, "Decision has no pros. \n Please add one below");

            } else {

                adapter.setReasons(reasons);

                recyclerView.refreshComplete();
                recyclerviewUtils.showData();
            }
        });

        viewModel.reasonResponse.observe(getViewLifecycleOwner(), response -> {
            if (response != null) {

                snackBarUtils.dismiss();

                if (response.isSuccessful()) {

                    viewModel.setReason(null);
                    viewModel.refreshPros();

                    isUpdating = false;
                    isDeleting = false;

                } else {
                    // archive decision
                    Snackbar snackbar = Snackbar.make(binding.constTemplate, response.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (isUpdating) {
                                viewModel.updatePro();
                            } else if (isDeleting) {
                                viewModel.deletePro();
                            }

                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });
    }

    private void initRecycler() {
        recyclerView = recyclerviewUtils.getRecyclerView();
        adapter = new ReasonAdapter(ReasonAdapter.PRO, prefsUtil.getUserId(), viewModel.decision.getValue().isDecided(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(false);

//        enableSwipeCallbacks();

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshPros();
            }

            @Override
            public void onLoadMore() {
                // TODO implement paging library from firebase
            }
        });
    }

    @Override
    public void onRetryClicked() {
        recyclerviewUtils.showLoading();
        viewModel.refreshPros();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null)
            binding = null;
    }

    @Override
    public void onReasonClicked(int MENU_ITEM, int position) {

        final Reason item = adapter.getData().get(position);

        if (MENU_ITEM == R.id.menu_reason_update) {

            ReasonBottomSheet bottomSheet = new ReasonBottomSheet(ReasonBottomSheet.PRO_ID, item, this);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());

        } else if (MENU_ITEM == R.id.menu_reason_delete) {

            adapter.removeItem(position);
            viewModel.setReason(item);

            // archive decision
            Snackbar snackbar = Snackbar.make(binding.constTemplate, "pro was deleted.", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    adapter.restoreItem(item, position);
                    recyclerView.scrollToPosition(position);

                    viewModel.setReason(null);

                }
            });
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        // Snackbar closed on its own and archives decision

                        isDeleting = true;
                        viewModel.deletePro();
                    }
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onReasonsCleared() {
        recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_EMPTY, "There are no pros. Please add one below");
    }

    @Override
    public void onReasonSaved(Reason reason) {
        viewModel.setReason(reason);
        snackBarUtils.makeSnackBar(requireContext(), binding.constTemplate, "updating pro");

        isUpdating = true;
        viewModel.updatePro();

        snackBarUtils.show();

    }
}