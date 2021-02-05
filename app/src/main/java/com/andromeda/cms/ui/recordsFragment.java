package com.andromeda.cms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.cms.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class recordsFragment extends Fragment {
    private FirebaseAuth fAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private String userID=fAuth.getCurrentUser().getEmail();
    private RecyclerView recyclerView;
    private ArrayList<recordsModel> records_list;
    private FloatingActionButton deleteButton;
    CollectionReference recRef=fStore.collection("Record");
    recordsAdapter recAdapt;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_records, container, false);
        deleteButton=root.findViewById(R.id.delete_floatingActionButton);
        recyclerView=root.findViewById(R.id.rec_RecyclerView);
        records_list= new ArrayList<>();
        recAdapt= new recordsAdapter(getContext(), records_list);
        recyclerView.setAdapter(recAdapt);
        recyclerView.setHasFixedSize(true);
        loadRecords(root);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recAdapt.getPos()!=RecyclerView.NO_POSITION) {
                    deleteRecord(v);
                    Snackbar.make(root, "Record deleted", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        return root;
    }

    public void loadRecords(View v){
        recRef.whereEqualTo("UserEmail",userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            recordsModel records = documentSnapshot.toObject(recordsModel.class);
                            records.setDocumentId(documentSnapshot.getId());
                            records_list.add(records);
                        }
                        recAdapt.notifyDataSetChanged();
                    }
                });
    }
    public void deleteRecord(View v) {
        int pos=recAdapt.getPos();
        records_list.get(pos).generateDate();
        Calendar currentDate=Calendar.getInstance();
        if(currentDate.after(records_list.get(pos).getCurrentDate()))
        {
            Toast.makeText(getActivity(),"Cannot Delete Past Records" , Toast.LENGTH_SHORT).show();
            return;
        }
        recRef.document(records_list.get(pos).getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                records_list.remove(pos);
                recAdapt.setPos(RecyclerView.NO_POSITION);
                recAdapt.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}