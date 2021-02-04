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

public class staffDashboardFragment extends Fragment {
    TextView mStaffID, mName, mEmail, mDepartment, mPhone, mDesignation;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.staffdashboard, container, false);
        initDashboard(root);
        return root;
    }
    public void initDashboard(View root)
    {
        mStaffID=root.findViewById(R.id.dstaffIdTextView);
        mName= root.findViewById(R.id.dnameTextView);
        mEmail= root.findViewById(R.id.demailTextView);
        mPhone= root.findViewById(R.id.dphoneTextView);
        mDepartment=  root.findViewById(R.id.ddepartmentTextView);
        mDesignation= root.findViewById(R.id.ddesignationTextView);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userID=fAuth.getCurrentUser().getEmail();
        DocumentReference dRef=fStore.collection("User").document(userID);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        mStaffID.setText(doc.getString("ID"));
                        mName.setText(doc.getString("Name"));
                        mEmail.setText(userID);
                        mDepartment.setText(doc.getString("Department"));
                        mPhone.setText(doc.getString("PhoneNo"));
                        mDesignation.setText(doc.getString("Designation"));
                    }
                }
            }
        });
    }
}