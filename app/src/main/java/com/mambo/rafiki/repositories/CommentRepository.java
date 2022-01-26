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
import com.mambo.rafiki.data.entities.Comment;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.utils.FirestoreUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.List;

public class CommentRepository {

    private static CommentRepository instance;

    private String userID;

    private CollectionReference reviewsCollection;

    private MutableLiveData<Response> response = new MutableLiveData<>();
    private MutableLiveData<List<Comment>> reviews = new MutableLiveData<>();

    private CommentRepository(Application application) {

        SharedPrefsUtil prefsUtil = SharedPrefsUtil.getInstance(application);
        userID = prefsUtil.getUserId();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reviewsCollection = db.collection(FirestoreUtils.COLLECTION_REVIEWS);

    }

    public static CommentRepository getInstance(Application application) {

        if (instance == null) {
            instance = new CommentRepository(application);
        }

        return instance;
    }

    public MutableLiveData<Response> getResponse() {
        return response;
    }

    public MutableLiveData<Response> addComment(Comment comment) {

        comment.setUserID(userID);

        reviewsCollection
                .add(comment)
                .addOnCompleteListener(task -> {

                    Response update = new Response();

                    if (task.isSuccessful()) {

                        update.setSuccessful(true);
                        update.setMessage("Comment added successfully");

                    } else {

                        update.setSuccessful(false);
                        update.setMessage("Failed saving comment");

                    }

                    response.setValue(update);

                });

        return response;
    }

    public MutableLiveData<Response> updateComment(Comment comment) {

        DocumentReference reference = reviewsCollection.document(comment.getId());
        reference.set(comment).addOnCompleteListener(task -> {

            Response response = new Response();

            if (task.isSuccessful()) {

                response.setSuccessful(true);
                response.setMessage("Comment updated successfully");


            } else {

                response.setSuccessful(false);
                response.setMessage("Failed updating comment");

            }

            this.response.setValue(response);

        });

        return response;
    }

    public MutableLiveData<Response> deleteComment(Comment comment) {

        DocumentReference reference = reviewsCollection.document(comment.getId());
        reference.delete().addOnCompleteListener(task -> {

            Response response = new Response();

            if (task.isSuccessful()) {

                response.setSuccessful(true);
                response.setMessage("Comment deleted successfully");

            } else {

                response.setSuccessful(false);
                response.setMessage("Failed deleting comment");

            }

            this.response.setValue(response);

        });

        return response;
    }

    public MutableLiveData<List<Comment>> getComments(Decision decision) {

        reviewsCollection
                .whereEqualTo(FirestoreUtils.FIELD_DECISION_ID, decision.getId())
                .orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Comment> mComments = new ArrayList<>();

                        if (task.isSuccessful() && task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Comment comment = document.toObject(Comment.class);
                                comment.setId(document.getId());

                                mComments.add(comment);

                            }

                            reviews.setValue(mComments);

                        } else {

                            reviews.setValue(null);

                        }

                    }
                });

        return reviews;

    }

}
