package com.mambo.rafiki.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.repositories.DecisionRepository;

import java.util.List;

public class ArchivedViewModel extends AndroidViewModel {

    private DecisionRepository decisionRepository;

    private LiveData<List<Decision>> decisions;
    private LiveData<Response> response = new MutableLiveData<>();

    public ArchivedViewModel(@NonNull Application application) {
        super(application);

        decisionRepository = DecisionRepository.getInstance(application);

        decisions = decisionRepository.getArchivedDecisions();

    }

    public LiveData<List<Decision>> getDecisions() {
        return decisions;
    }

    public void updateDecisions() {
        decisions = decisionRepository.getArchivedDecisions();
    }

    public void update(Decision decision) {
        decisionRepository.update(decision);
    }

    public void delete(Decision decision) {
        decisionRepository.delete(decision);
    }
}
