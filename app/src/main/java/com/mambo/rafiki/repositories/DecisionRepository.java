package com.mambo.rafiki.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.utils.FirestoreUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.List;

public class DecisionRepository {

    private static DecisionRepository instance;

    private String userID;

    private MutableLiveData<Response> response = new MutableLiveData<>();

    private MutableLiveData<Decision> decision = new MutableLiveData<>();
    private MutableLiveData<List<Decision>> decisions = new MutableLiveData<>();

    private MutableLiveData<List<Decision>> randomDecisions = new MutableLiveData<>();
    private MutableLiveData<List<Decision>> archivedDecisions = new MutableLiveData<>();

    private CollectionReference decisionsCollection;
    private Query personalDecisionsQuery;
    private Query publicDecisionsQuery;


    private DecisionRepository(Application application) {

        SharedPrefsUtil prefsUtil = SharedPrefsUtil.getInstance(application);
        userID = prefsUtil.getUserId();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        decisionsCollection = db.collection(FirestoreUtils.COLLECTION_DECISIONS);

        personalDecisionsQuery = decisionsCollection
                .whereEqualTo(FirestoreUtils.FIELD_USER_ID, userID)
                .whereEqualTo(FirestoreUtils.FIELD_ARCHIVED, false)
                .orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING)
                .limit(20);

        publicDecisionsQuery = decisionsCollection
                .whereEqualTo(FirestoreUtils.FIELD_PUBLIC, true)
                .whereEqualTo(FirestoreUtils.FIELD_ARCHIVED, false)
                .whereEqualTo(FirestoreUtils.FIELD_COMPLETED, false)
                .whereEqualTo(FirestoreUtils.FIELD_DECIDED, false)
                .orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING)
                .limit(20);
    }

    public MutableLiveData<Decision> getDecision() {
        return decision;
    }

    public static DecisionRepository getInstance(Application application) {

        if (instance == null) {
            instance = new DecisionRepository(application);
        }

        return instance;
    }

    public MutableLiveData<Response> getResponse() {
        return response;
    }

    public MutableLiveData<Decision> getDecision(Decision item) {

        DocumentReference decisionRef = decisionsCollection.document(item.getId());

        decisionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful() && task.getResult() != null) {

                            DocumentSnapshot document = task.getResult();

                            Decision newDecision = document.toObject(Decision.class);
                            newDecision.setId(document.getId());

                            decision.setValue(newDecision);
                        }

                    }
                });

        return decision;

    }

    public MutableLiveData<List<Decision>> getDecisions() {

        personalDecisionsQuery
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Decision> mChoices = new ArrayList<>();

                        if (task.isSuccessful() && task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Decision decision = document.toObject(Decision.class);
                                decision.setId(document.getId());

                                mChoices.add(decision);

                            }

                            decisions.setValue(mChoices);

                        } else {

                            decisions.setValue(null);

                        }

                    }
                });

        return decisions;

    }

    public MutableLiveData<List<Decision>> getPublicDecisions() {

        publicDecisionsQuery
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Decision> mChoices = new ArrayList<>();

                        if (task.isSuccessful() && task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Decision choice = document.toObject(Decision.class);
                                choice.setId(document.getId());

                                mChoices.add(choice);

                            }

                            randomDecisions.setValue(mChoices);

                        } else {

                            randomDecisions.setValue(null);

                        }

                    }
                });

        return randomDecisions;

    }

    public MutableLiveData<List<Decision>> getArchivedDecisions() {

        decisionsCollection
                .whereEqualTo(FirestoreUtils.FIELD_USER_ID, userID)
                .whereEqualTo(FirestoreUtils.FIELD_ARCHIVED, true)
                .orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Decision> mChoices = new ArrayList<>();

                        if (task.isSuccessful() && task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Decision choice = document.toObject(Decision.class);
                                choice.setId(document.getId());

                                mChoices.add(choice);

                            }

                            archivedDecisions.setValue(mChoices);

                        } else {

                            archivedDecisions.setValue(null);

                        }

                    }
                });

        return archivedDecisions;

    }

    public void setDecisionListener(Decision item) {
        DocumentReference decisionRef = decisionsCollection.document(item.getId());

        decisionRef.addSnapshotListener((value, error) -> {

            if (error != null) {
                return;
            }

            if (value != null)
                decision.setValue(value.toObject(Decision.class));
        });
    }

    public void insert(Decision decision) {

        decision.setUserID(userID);

        decisionsCollection.add(decision).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                Response insertResponse = new Response();

                if (task.isSuccessful()) {

                    insertResponse.setSuccessful(true);
                    insertResponse.setMessage("Decision saved");

                    response.setValue(insertResponse);

                    getDecisions();
                } else {

                    insertResponse.setSuccessful(false);
                    insertResponse.setMessage("Decision not saved");

                    response.setValue(insertResponse);
                }
            }
        });
    }

    public void update(Decision decision) {

        DocumentReference decisionRef = decisionsCollection.document(decision.getId());

        decisionRef
                .set(decision)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        getDecisions();
                    }

                });
    }

    public MutableLiveData<Response> updateDecision(Decision decision) {

        DocumentReference decisionRef = decisionsCollection.document(decision.getId());

        decisionRef
                .set(decision)
                .addOnCompleteListener(task -> {

                    Response update = new Response();

                    if (task.isSuccessful()) {

                        update.setSuccessful(true);
                        update.setMessage("decision updated successfully");

                    } else {

                        update.setSuccessful(false);
                        update.setMessage("Failed updating decision");

                    }

                    response.setValue(update);
                });

        return response;
    }

    public MutableLiveData<Response> delete(Decision decision) {
        decisionsCollection.document(decision.getId())
                .delete()
                .addOnCompleteListener(task -> {

                    Response update = new Response();

                    if (task.isSuccessful()) {

                        update.setSuccessful(true);
                        update.setMessage("decision deleted successfully");

                    } else {

                        update.setSuccessful(false);
                        update.setMessage("Failed deleting decision");

                    }

                    response.setValue(update);
                });

        return response;
    }


}
