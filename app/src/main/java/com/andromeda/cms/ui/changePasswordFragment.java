package com.andromeda.cms.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.andromeda.cms.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePasswordFragment  extends Fragment{
    EditText mNewPass, mConfirmNewPass;
    Button mChangePassBtn;
    FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_changepassword, container, false);
        mNewPass = root.findViewById(R.id.newPasswordText);
        mConfirmNewPass = root.findViewById(R.id.confirmNewPasswordText);
        mChangePassBtn = root.findViewById(R.id.changePasswordBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mChangePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NewPass=mNewPass.getText().toString().trim();
                String ConfPass=mConfirmNewPass.getText().toString().trim();

                if (TextUtils.isEmpty(NewPass))
                    mNewPass.setError("New Password is Required!");
                else if (NewPass.length()<8)
                    mNewPass.setError("Password must be at least 8 characters!");
                else if (!NewPass.equals(ConfPass))
                    mConfirmNewPass.setError("Passwords do not match!");
                else if(user!=null)
                {
                    user.updatePassword(NewPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(),"Password Successfully Changed", Toast.LENGTH_SHORT).show();
                            mNewPass.setText("");
                            mConfirmNewPass.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
        return root;
    }
}