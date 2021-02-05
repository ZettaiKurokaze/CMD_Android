package com.andromeda.cms.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.andromeda.cms.Login;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;


public class logoutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth.getInstance().signOut();
        saveData();
        startActivity(new Intent(getActivity().getApplicationContext(),Login.class));
        getActivity().finish();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user type",0);
        editor.apply();
    }
}