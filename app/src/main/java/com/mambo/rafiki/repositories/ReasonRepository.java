package com.mambo.rafiki.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.data.entities.Reason;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.utils.FirestoreUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.List;

public class ReasonRepository {

    private static ReasonRepository instance;

    private String userID;

    private CollectionReference prosCollection;
    private CollectionReference consCollection;

    private MutableLiveData<Response> response = new MutableLiveData<>();

    private MutableLiveData<List<Reason>> pros = new MutableLiveData<>();
    private MutableLiveData<List<Reason>> cons = new MutableLiveData<>();

    private ReasonRepository(Application application) {

        SharedPrefsUtil prefsUtil = SharedPrefsUtil.getInstance(application);
        userID = prefsUtil.getUserId();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        prosCollection = db.collection(FirestoreUtils.COLLECTION_PROS);
        consCollection = db.collection(FirestoreUtils.COLLECTION_CONS);

    }

    public static ReasonRepository getInstance(Application application) {

        if (instance == null) {
            instance = new ReasonRepository(application);
        }

        return instance;
    }

    public MutableLiveData<Response> getResponse() {
        return response;
    }

    public MutableLiveData<Response> addPro(Reason reason) {

        reason.setUserID(userID);

        prosCollection
                .add(reason)
                .addOnCompleteListener(task -> {

                    Response update = new Response();

                    if (task.isSuccessful()) {

                        update.setSuccessful(true);
                        update.setMessage("Pro added successfully");

                    } else {

                        update.setSuccessful(false);
                        update.setMessage("Failed saving pro");

                    }

                    response.setValue(update);

                });

        return response;
    }

    public MutableLiveData<Response> updatePro(Reason reason) {

        DocumentReference reference = prosCollection.document(reason.getId());
        reference.set(reason).addOnCompleteListener(task -> {

            Response response = new Response();

            if (task.isSuccessful()) {

                response.setSuccessful(true);
                response.setMessage("Pro updated successfully");


            } else {

                response.setSuccessful(false);
                response.setMessage("Failed updating pro");

            }

            this.response.setValue(response);

        });

        return response;
    }

    public MutableLiveData<Response> deletePro(Reason reason) {

        DocumentReference reference = prosCollection.document(reason.getId());
        reference.delete().addOnCompleteListener(task -> {

            Response response = new Response();

            if (task.isSuccessful()) {

                response.setSuccessful(true);
                response.setMessage("Pro deleted successfully");


            } else {

                response.setSuccessful(false);
                response.setMessage("Failed deleting pro");

            }

            this.response.setValue(response);

        });

        return response;
    }

    public MutableLiveData<Response> addCon(Reason reason) {

        reason.setUserID(userID);

        consCollection
                .add(reason)
                .addOnCompleteListener(task -> {

                    Response response = new Response();

                    if (task.isSuccessful()) {

                        response.setSuccessful(true);
                        response.setMessage("Con added successfully");

                    } else {

                        response.setSuccessful(false);
                        response.setMessage("Failed adding con");

                    }

                    this.response.setValue(response);
                });

        return response;
    }

    public MutableLiveData<Response> updateCon(Reason reason) {

        DocumentReference reference = consCollection.document(reason.getId());
        reference.set(reason).addOnCompleteListener(task -> {

            Response response = new Response();

            if (task.isSuccessful()) {

                response.setSuccessful(true);
                response.setMessage("Con updated successfully");

            } else {

                response.setSuccessful(false);
                response.setMessage("Failed updating con");

            }

            this.response.setValue(response);
        });

        return response;
    }

    public MutableLiveData<Response> deleteCon(Reason reason) {

        DocumentReference reference = consCollection.document(reason.getId());
        reference.delete().addOnCompleteListener(task -> {
            Response response = new Response();

            if (task.isSuccessful()) {

                response.setSuccessful(true);
                response.setMessage("con deleted successfully");

            } else {

                response.setSuccessful(false);
                response.setMessage("failed deleting con");

            }

            this.response.setValue(response);
        });

        return response;

    }

    public MutableLiveData<List<Reason>> getPros(Decision decision) {

        prosCollection
                .whereEqualTo(FirestoreUtils.FIELD_DECISION_ID, decision.getId())
                .orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    List<Reason> mReasons = new ArrayList<>();

                    if (task.isSuccessful() && task.getResult() != null) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Reason reason = document.toObject(Reason.class);
                            reason.setId(document.getId());

                            mReasons.add(reason);

                        }

                        pros.setValue(mReasons);

                    } else {

                        pros.setValue(null);

                    }

                });

        return pros;

    }

    public MutableLiveData<List<Reason>> getCons(Decision decision) {

        consCollection
                .whereEqualTo(FirestoreUtils.FIELD_DECISION_ID, decision.getId())
                .orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Reason> mReasons = new ArrayList<>();

                        if (task.isSuccessful() && task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Reason reason = document.toObject(Reason.class);
                                reason.setId(document.getId());

                                mReasons.add(reason);

                            }

                            cons.setValue(mReasons);

                        } else {

                            cons.setValue(null);

                        }

                    }
                });

        return cons;

    }

}
