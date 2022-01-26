package com.mambo.rafiki.ui.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.FragmentDiscoverBinding;
import com.mambo.rafiki.ui.adapters.DiscoverAdapter;
import com.mambo.rafiki.ui.interfaces.OnDecisionClickListener;
import com.mambo.rafiki.ui.viewmodels.HomeViewModel;
import com.mambo.rafiki.utils.CardStackRecyclerUtils;
import com.mambo.rafiki.utils.FirestoreUtils;
import com.mambo.rafiki.utils.RecyclerviewUtils;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment implements RecyclerviewUtils.OnRetryListener {

    private FragmentDiscoverBinding binding;
    private HomeViewModel viewModel;

    private FirestorePagingOptions<Decision> options;
    private PagedList.Config config;
    private Query query;

    private CardStackRecyclerUtils recyclerviewUtils;
    private CardStackView cardStackView;
    private CardStackLayoutManager manager;

    private DiscoverAdapter adapter;
    private OnDecisionClickListener mListener;

    public DiscoverFragment(OnDecisionClickListener decisionClickListener) {
        // Required empty public constructor
        mListener = decisionClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createPagedListConfig() {
        config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(5)
                .setPageSize(3)
                .build();
    }

    private void createOptions() {
        options = new FirestorePagingOptions.Builder<Decision>()
                .setQuery(query, config, new SnapshotParser<Decision>() {
                    @NonNull
                    @Override
                    public Decision parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Decision decision = snapshot.toObject(Decision.class);
                        decision.setId(snapshot.getId());
                        return decision;
                    }
                })
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();
    }

    private void createQuery() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        query = db.collection(FirestoreUtils.COLLECTION_DECISIONS)
                .whereEqualTo(FirestoreUtils.FIELD_PUBLIC, true)
                .whereEqualTo(FirestoreUtils.FIELD_ARCHIVED, false)
                .whereEqualTo(FirestoreUtils.FIELD_COMPLETED, false)
                .whereEqualTo(FirestoreUtils.FIELD_DECIDED, false)
                .orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING)
                .limit(20);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    return;
                }

                assert value != null;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    switch (documentChange.getType()) {
                        case ADDED:
                            Toast.makeText(getContext(), "Document added", Toast.LENGTH_SHORT).show();
                            break;

                        case MODIFIED:
                            Toast.makeText(getContext(), "Document updated", Toast.LENGTH_SHORT).show();
                            break;

                        case REMOVED:
                            Toast.makeText(getContext(), "Document removed", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            adapter.refresh();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false);
        createQuery();
        createPagedListConfig();
        createOptions();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerviewUtils = CardStackRecyclerUtils.getInstance(binding.includeLayoutPublished, this);

        hideRewindLayout();

        initRecyclerView();

        binding.constRewind.setOnClickListener(v -> rewind());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

//        viewModel.getRandomDecisions().observe(getViewLifecycleOwner(), decisions -> {
//            if (decisions == null || decisions.isEmpty()) {
//                recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_EMPTY, "No decisions to review at this time. \n Please try again later.");
//            } else {
////                adapter.setData(decisions);
//                showRewindLayout();
//                recyclerviewUtils.showData();
//            }
//        });

    }

    private void showRewindLayout() {
        binding.constReact.setVisibility(View.VISIBLE);
    }

    private void hideRewindLayout() {
        binding.constReact.setVisibility(View.GONE);
    }

    private void initRecyclerView() {

        cardStackView = recyclerviewUtils.getCardStackView();

        manager = new CardStackLayoutManager(requireContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                // do nothing
            }

            @Override
            public void onCardSwiped(Direction direction) {

                int topPosition = manager.getTopPosition();

                if (topPosition == adapter.getCurrentList().size()) {

                    if (viewModel.isLoadedAllDecisions)
                        recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_EMPTY, "Reached end of the list");
                    else
                        recyclerviewUtils.showLoading();

                } else {
                    recyclerviewUtils.showData();
                }

            }

            @Override
            public void onCardRewound() {
                // do nothing
                int topPosition = manager.getTopPosition();

                if (topPosition != adapter.getCurrentList().size()) {
                    recyclerviewUtils.showData();
                }
            }

            @Override
            public void onCardCanceled() {
                // do nothing
            }

            @Override
            public void onCardAppeared(View view, int position) {
                // do nothing
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                // do nothing
            }
        });

        List<Direction> directions = new ArrayList<>();

        directions.add(Direction.Left);
        directions.add(Direction.Right);
        directions.add(Direction.Bottom);

        manager.setStackFrom(StackFrom.Top);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.90f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(directions);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        manager.setOverlayInterpolator(new LinearInterpolator());

        adapter = new DiscoverAdapter(options, mListener) {
            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                switch (state) {
                    case LOADING_INITIAL:
                        recyclerviewUtils.showData();
                        showRewindLayout();
                        break;

                    case LOADING_MORE:
                        recyclerviewUtils.showDataAndLoading();
                        break;

                    case LOADED:
                        recyclerviewUtils.showData();
                        break;

                    case FINISHED:
                        viewModel.setIsDiscoverFinished(true);
                        recyclerviewUtils.showData();
                        break;

                    case ERROR:
                        recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_ERROR, "Failed getting decisions");
                        break;
                }
            }
        };

        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

    }

    private void rewind() {
        RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new DecelerateInterpolator())
                .build();
        manager.setRewindAnimationSetting(setting);
        cardStackView.rewind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null) {
            binding = null;
        }
    }

    @Override
    public void onRetryClicked() {
        recyclerviewUtils.showLoading();
        initRecyclerView();
    }

}