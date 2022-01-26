package com.mambo.rafiki.repositories;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mambo.rafiki.data.entities.Feedback;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.utils.FirestoreUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

public class FeedbackRepository {

    private static FeedbackRepository instance;
    private final SharedPrefsUtil prefsUtil;
    private CollectionReference feedbackCollection;

    private MutableLiveData<Response> response = new MutableLiveData<>();

    private FeedbackRepository(Application application) {

        prefsUtil = SharedPrefsUtil.getInstance(application);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        feedbackCollection = db.collection(FirestoreUtils.COLLECTION_FEEDBACK);

    }

    public static FeedbackRepository getInstance(Application application) {

        if (instance == null) {
            instance = new FeedbackRepository(application);
        }

        return instance;
    }

    public MutableLiveData<Response> getResponse() {
        return response;
    }

    public MutableLiveData<Response> sendFeedback(Feedback feedback) {

        feedback.setGender(prefsUtil.getUserGender());
        feedback.setLocation(prefsUtil.getUserLocation());

        feedbackCollection.add(feedback).addOnCompleteListener(task -> {
            Response update = new Response();

            if (task.isSuccessful()) {

                update.setSuccessful(true);
                update.setMessage("feedback sent successfully");

            } else {

                update.setSuccessful(false);
                update.setMessage("Failed sending feedback");

            }

            response.setValue(update);
        });

        return response;
    }

}
