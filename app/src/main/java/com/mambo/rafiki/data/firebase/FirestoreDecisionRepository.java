package com.mambo.rafiki.data.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mambo.rafiki.ui.livedata.DecisionsLiveData;
import com.mambo.rafiki.ui.viewmodels.HomeViewModel;
import com.mambo.rafiki.utils.FirestoreUtils;

public class FirestoreDecisionRepository implements HomeViewModel.DecisionsRepository,
        DecisionsLiveData.OnLastVisibleItemCallback, DecisionsLiveData.OnLastItemReachedCallback {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private CollectionReference prosCollection = firebaseFirestore.collection(FirestoreUtils.COLLECTION_PROS);
    private Query query = prosCollection.orderBy(FirestoreUtils.FIELD_DATE, Query.Direction.DESCENDING).limit(50);

    private DocumentSnapshot lastVisibleProduct;
    private boolean isLastProductReached;

    @Override
    public DecisionsLiveData getDecisionsLiveData() {
        if (isLastProductReached) {
            return null;
        }

        if (lastVisibleProduct != null) {
            query = query.startAfter(lastVisibleProduct);
        }

        return new DecisionsLiveData(query, this, this);
    }

    @Override
    public void setLastVisibleItem(DocumentSnapshot lastVisibleItem) {
        this.lastVisibleProduct = lastVisibleProduct;

    }

    @Override
    public void setLastItemReached(boolean isLastItemReached) {
        this.isLastProductReached = isLastProductReached;
    }
}
