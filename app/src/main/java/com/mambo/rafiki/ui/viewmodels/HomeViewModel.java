package com.mambo.rafiki.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.data.entities.Reason;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.repositories.DecisionRepository;
import com.mambo.rafiki.repositories.ReasonRepository;
import com.mambo.rafiki.ui.livedata.DecisionsLiveData;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private DecisionRepository decisionRepository;
    private ReasonRepository reasonRepository;

    private Decision decision;
    private Reason reason;

    public boolean isAddingPro = false;
    public boolean isAddingCon = false;

    public boolean isLoadedAllDecisions = false;

    public MutableLiveData<Response> reasonResponse = new MutableLiveData<>();

    private LiveData<List<Decision>> decisions = new MutableLiveData<>();
    private LiveData<List<Decision>> randomDecisions = new MutableLiveData<>();
    private LiveData<Response> response = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);

        decisionRepository = DecisionRepository.getInstance(application);
        reasonRepository = ReasonRepository.getInstance(application);

        decisions = decisionRepository.getDecisions();
        randomDecisions = decisionRepository.getPublicDecisions();
        response = decisionRepository.getResponse();

    }

    public LiveData<Response> getResponse() {
        return response;
    }

    public LiveData<List<Decision>> getDecisions() {
        return decisions;
    }

    public LiveData<List<Decision>> getRandomDecisions() {
        return randomDecisions;
    }

    public void setIsDiscoverFinished(boolean isTrue) {
        isLoadedAllDecisions = isTrue;
    }

    //decisions

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public void insert(Decision decision) {
        decisionRepository.insert(decision);
    }

    public void update(Decision decision) {
        decisionRepository.update(decision);
    }

    public void updateDecisions() {
        decisions = decisionRepository.getDecisions();
    }

    public void updateRandomDecisions() {
        randomDecisions = decisionRepository.getPublicDecisions();
    }

    public void delete(Decision decision) {
        response = decisionRepository.delete(decision);
    }

    // pros and cons
    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public void setUpdatingToFalse() {
        isAddingPro = false;
        isAddingCon = false;
    }

    public void addPro() {

        isAddingPro = true;

        reason.setDecisionID(decision.getId());
        reasonResponse = reasonRepository.addPro(reason);

        Decision updatedDecision = decision;
        updatedDecision.addPro(reason);

        update();
    }

    public void addCon() {

        isAddingCon = true;

        reason.setDecisionID(decision.getId());
        reasonResponse = reasonRepository.addCon(reason);

        Decision updatedDecision = decision;
        updatedDecision.addCon(reason);

        update();
    }

    public void update() {
        if (decision != null)
            decisionRepository.updateDecision(decision);
    }

    //new methods
    public DecisionsLiveData getDecisionsLiveData() {
//        return decisionRepository.getDecisionsLiveData();
        return null;
    }

    public interface DecisionsRepository {
        DecisionsLiveData getDecisionsLiveData();
    }

}
