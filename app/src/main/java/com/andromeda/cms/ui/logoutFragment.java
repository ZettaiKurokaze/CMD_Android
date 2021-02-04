package com.andromeda.cms.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.andromeda.cms.Login;
import com.google.firebase.auth.FirebaseAuth;


public class logoutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity().getApplicationContext(),Login.class));
        getActivity().finish();
    }
}