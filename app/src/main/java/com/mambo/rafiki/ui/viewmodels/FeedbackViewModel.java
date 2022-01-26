package com.mambo.rafiki.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mambo.rafiki.data.entities.Feedback;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.repositories.FeedbackRepository;

public class FeedbackViewModel extends AndroidViewModel {

    private FeedbackRepository repository;

    private Feedback feedback;
    public LiveData<Response> response = new MutableLiveData<>();

    public FeedbackViewModel(@NonNull Application application) {
        super(application);

        repository = FeedbackRepository.getInstance(application);
        response = repository.getResponse();
    }

    public void clearResponse() {
        this.response = new MutableLiveData<>();
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public void setContent(String content) {
        feedback.setContent(content);
    }

    public void sendFeedback() {
        response = repository.sendFeedback(feedback);
    }

}
