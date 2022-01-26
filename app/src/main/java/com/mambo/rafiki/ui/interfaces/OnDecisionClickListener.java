package com.mambo.rafiki.ui.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mambo.rafiki.data.entities.Decision;

public abstract interface OnDecisionClickListener {
    void onDecisionClicked(DocumentSnapshot snapshot);
    void onDecisionClicked(Decision decision);
}
