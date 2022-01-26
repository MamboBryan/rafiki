package com.mambo.rafiki.ui.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnSnapshotClickListener {
    void onSnapshotClick(DocumentSnapshot snapshot);
}
