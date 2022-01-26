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
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.FragmentArchivedBinding;
import com.mambo.rafiki.ui.adapters.PrivateAdapter;
import com.mambo.rafiki.ui.adapters.SwipeCallbacks;
import com.mambo.rafiki.ui.viewmodels.ArchivedViewModel;
import com.mambo.rafiki.utils.ConstantsUtil;
import com.mambo.rafiki.utils.RecyclerviewUtils;

public class ArchivedFragment extends Fragment implements PrivateAdapter.OnDecisionClickListener, SwipeCallbacks.OnItemSwipedListener {

    private FragmentArchivedBinding binding;
    private ArchivedViewModel viewModel;

    private RecyclerviewUtils recyclerviewUtils;
    private XRecyclerView recyclerView;

    private PrivateAdapter adapter;
    private NavController navController;

    public ArchivedFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_archived, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        NavigationUI.setupWithNavController(binding.toolbar, navController);

        recyclerviewUtils = RecyclerviewUtils.getInstance(binding.includeLayoutArchived);
        initRecyclerView();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ArchivedViewModel.class);
        viewModel.getDecisions().observe(getViewLifecycleOwner(), decisions -> {

            if (decisions == null || decisions.isEmpty()) {
                recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_EMPTY, "No archived decisions at the moment");
            } else {
                adapter.setData(decisions);

                recyclerView.refreshComplete();
                recyclerviewUtils.showData();
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.updateDecisions();
    }

    private void initRecyclerView() {

        recyclerView = recyclerviewUtils.getRecyclerView();
        adapter = new PrivateAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(false);

        enableSwipeCallbacks();

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                viewModel.updateDecisions();
            }

            @Override
            public void onLoadMore() {
                // TODO implement paging library from firebase
            }
        });

    }

    private void enableSwipeCallbacks() {
        SwipeCallbacks swipeToDeleteCallback =
                new SwipeCallbacks(getContext(),
                        R.drawable.ic_baseline_unarchive_24_green,
                        R.drawable.ic_baseline_delete_24,
                        this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null) {
            binding = null;
        }
    }

    @Override
    public void onDecisionClicked(Decision decision) {
        String json = new Gson().toJson(decision);

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtil.EXTRA, json);

        navController.navigate(R.id.action_archivedFragment_to_decisionFragment, bundle);

    }

    @Override
    public void onDecisionsCleared() {
        recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_EMPTY, "No archived decisions at the moment");
    }

    @Override
    public void onItemSwiped(int swipeDirection, int position) {

        int itemPosition = position - 1;

        final Decision item = adapter.getData().get(itemPosition);
        adapter.removeItem(itemPosition);

        if (swipeDirection == ItemTouchHelper.LEFT) {
            Snackbar snackbar = Snackbar.make(binding.layoutArchived, "Decision was deleted.", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    adapter.restoreItem(item, itemPosition);
                    recyclerView.scrollToPosition(itemPosition);

                }
            });

            // delete decision
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        // Snackbar closed on its own and deletes item
                        viewModel.delete(item);
                    }
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        } else {
            Snackbar snackbar = Snackbar.make(binding.layoutArchived, "Decision was unarchived.", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(item, itemPosition);
                    recyclerView.scrollToPosition(itemPosition);

                }
            });

            // unarchive decision
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        // Snackbar closed on its own and archives decision
                        item.setArchived(false);
                        viewModel.update(item);
                    }
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }
}