package com.mambo.rafiki.ui.views.fragments;

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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.databinding.FragmentDecisionsBinding;
import com.mambo.rafiki.ui.adapters.DecisionsAdapter;
import com.mambo.rafiki.ui.interfaces.OnDecisionClickListener;
import com.mambo.rafiki.ui.viewmodels.HomeViewModel;
import com.mambo.rafiki.utils.FirestoreUtils;
import com.mambo.rafiki.utils.RecyclerviewUtils;

public class DecisionsFragment extends Fragment implements RecyclerviewUtils.OnRetryListener {

    private FragmentDecisionsBinding binding;
    private HomeViewModel viewModel;
    private RecyclerviewUtils recyclerviewUtils;
    private XRecyclerView recyclerView;

    private DecisionsAdapter adapter;
    private OnDecisionClickListener mListener;

    private Query query;
    private FirestoreRecyclerOptions<Decision> options;


    public DecisionsFragment(OnDecisionClickListener decisionClickListener) {
        // Required empty public constructor
        this.mListener = decisionClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_decisions, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerviewUtils = RecyclerviewUtils.getInstance(binding.includeLayoutDecisions, this);

        recyclerView = recyclerviewUtils.getRecyclerView();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        createQuery();
        createOptions();

        initRecyclerView();

    }

    private void createOptions() {
        options = new FirestoreRecyclerOptions.Builder<Decision>()
                .setQuery(query, snapshot -> {
                    Decision decision = snapshot.toObject(Decision.class);
                    decision.setId(snapshot.getId());
                    return decision;
                })
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();
    }

    private void createQuery() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        query = db.collection(FirestoreUtils.COLLECTION_DECISIONS)
                .whereEqualTo(FirestoreUtils.FIELD_USER_ID, user.getUid())
                .whereEqualTo(FirestoreUtils.FIELD_ARCHIVED, false)
                .orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING)
                .limit(5);
    }

    private void initRecyclerView() {

        adapter = new DecisionsAdapter(options, mListener) {

            @Override
            public void isEmpty() {
                super.isEmpty();

                String message = "A Fresh Start" + "\n Click below to add your first decision ";
                recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_EMPTY, message);
            }

            @Override
            public void isNotEmpty() {
                super.isNotEmpty();

                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerviewUtils.showData();
                }
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                recyclerviewUtils.showFeedback(RecyclerviewUtils.FEEDBACK_ERROR, "Failed getting decisions");
            }
        };

        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerviewUtils.showData();

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