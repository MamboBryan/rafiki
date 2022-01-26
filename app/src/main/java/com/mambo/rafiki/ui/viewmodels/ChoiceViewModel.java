package com.mambo.rafiki.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.repositories.FeedbackRepository;

public class ChoiceViewModel extends AndroidViewModel {

    private FeedbackRepository repository;

    private MutableLiveData<Decision> decision = new MutableLiveData<>();

    public ChoiceViewModel(@NonNull Application application) {
        super(application);

        repository = FeedbackRepository.getInstance(application);
    }

    public LiveData<Decision> getDecision() {
        return decision;
    }

    public void setShared(Decision decision) {
        this.decision.setValue(decision);
    }
}
