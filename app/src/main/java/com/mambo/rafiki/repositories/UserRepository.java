package com.mambo.rafiki.repositories;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.data.entities.User;
import com.mambo.rafiki.utils.FirestoreUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

public class UserRepository {

    private static UserRepository instance;
    private final SharedPrefsUtil prefsUtil;

    private CollectionReference usersCollection;

    private MutableLiveData<Response> response = new MutableLiveData<>();


    private UserRepository(Application application) {

        prefsUtil = SharedPrefsUtil.getInstance(application);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usersCollection = db.collection(FirestoreUtils.COLLECTION_USERS);

    }

    public static UserRepository getInstance(Application application) {

        if (instance == null) {
            instance = new UserRepository(application);
        }

        return instance;
    }

    public MutableLiveData<Response> getResponse() {
        return response;
    }

    public MutableLiveData<Response> updateUser(User user) {

        updatePrefs(user);

        usersCollection
                .document(user.getId())
                .set(user)
                .addOnCompleteListener(task -> {

                    Response update = new Response();

                    if (task.isSuccessful()) {

                        update.setSuccessful(true);
                        update.setMessage("setup successful");

                        prefsUtil.setUpIsCompleted();

                    } else {

                        update.setSuccessful(false);
                        update.setMessage("setup failed");

                    }

                    response.setValue(update);

                });

        return response;
    }

    private void updatePrefs(User user) {

        prefsUtil.setUserId(user.getId());

    }

}
