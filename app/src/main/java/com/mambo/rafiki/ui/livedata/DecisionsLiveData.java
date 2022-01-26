package com.mambo.rafiki.ui.livedata;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.data.entities.Operation;
import com.mambo.rafiki.utils.FirestoreUtils;

public class DecisionsLiveData extends LiveData<Operation> implements EventListener<QuerySnapshot> {

    private Query query;
    private ListenerRegistration listenerRegistration;
    private OnLastVisibleItemCallback onLastVisibleItemCallback;
    private OnLastItemReachedCallback onLastItemReachedCallback;

    public DecisionsLiveData(Query query, OnLastVisibleItemCallback onLastVisibleItemCallback, OnLastItemReachedCallback onLastItemReachedCallback) {
        this.query = query;
        this.onLastVisibleItemCallback = onLastVisibleItemCallback;
        this.onLastItemReachedCallback = onLastItemReachedCallback;
    }

    @Override
    protected void onActive() {
        listenerRegistration = query.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        listenerRegistration.remove();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) return;

        for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {

            switch (documentChange.getType()) {

                case ADDED:
                    Decision added = documentChange.getDocument().toObject(Decision.class);

                    Operation addOperation = new Operation(added, FirestoreUtils.OPERATION_ADDED);
                    setValue(addOperation);
                    break;

                case MODIFIED:
                    Decision modified = documentChange.getDocument().toObject(Decision.class);

                    Operation modifyOperation = new Operation(modified, FirestoreUtils.OPERATION_MODIFIED);
                    setValue(modifyOperation);
                    break;

                case REMOVED:
                    Decision removed = documentChange.getDocument().toObject(Decision.class);

                    Operation removeOperation = new Operation(removed, FirestoreUtils.OPERATION_REMOVED);
                    setValue(removeOperation);
            }

        }

        int querySnapshotSize = querySnapshot.size();

        if (querySnapshotSize < 20) {
            onLastItemReachedCallback.setLastItemReached(true);
        } else {
            DocumentSnapshot lastVisibleProduct = querySnapshot.getDocuments().get(querySnapshotSize - 1);
            onLastVisibleItemCallback.setLastVisibleItem(lastVisibleProduct);
        }
    }

    public interface OnLastVisibleItemCallback {
        void setLastVisibleItem(DocumentSnapshot lastVisibleItem);
    }

    public interface OnLastItemReachedCallback {
        void setLastItemReached(boolean isLastItemReached);
    }

}
