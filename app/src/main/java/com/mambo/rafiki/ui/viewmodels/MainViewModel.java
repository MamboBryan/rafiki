package com.mambo.rafiki.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.repositories.DecisionRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private DecisionRepository repository;

    private LiveData<List<Decision>> archivedDecisions;

    public MainViewModel(@NonNull Application application) {
        super(application);

        repository = DecisionRepository.getInstance(application);
        archivedDecisions = repository.getArchivedDecisions();
    }

    public LiveData<List<Decision>> getArchivedDecisions() {
        return archivedDecisions;
    }

    public void update(Decision decision) {
        repository.update(decision);
    }

    public void delete(Decision decision) {
        repository.delete(decision);
    }


}
