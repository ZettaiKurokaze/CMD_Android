package com.andromeda.cms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.andromeda.cms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class adminDashboardFragment extends Fragment {
    TextView mName, mEmail, mPhone, mRole;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admindashboard, container, false);
        mName= root.findViewById(R.id.dnameTextView);
        mEmail= root.findViewById(R.id.demailTextView);
        mPhone= root.findViewById(R.id.dphoneTextView);
        mRole=root.findViewById(R.id.droleTextView);


        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userID= Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
        DocumentReference dRef=fStore.collection("User").document(userID);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    if (doc.exists()) {
                        mName.setText(doc.getString("Name"));
                        mEmail.setText(userID);
                        mPhone.setText(doc.getString("PhoneNo"));
                        mRole.setText(doc.getString("Role"));
                    }
                }
            }
        });
        return root;
    }
}