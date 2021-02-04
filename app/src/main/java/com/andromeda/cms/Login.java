package com.andromeda.cms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLoginBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private int userType=0;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String INT = "user type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.userEmailText);
        mPassword = findViewById(R.id.userPasswordText);
        mLoginBtn = findViewById(R.id.loginButton);
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        loadUser();
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=mEmail.getText().toString().trim();
                String Password=mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(Email))
                {
                    mEmail.setError("Email is Required!");
                    return;
                }
                if (TextUtils.isEmpty(Password))
                {
                    mPassword.setError("Password is Required!");
                    return;
                }
                if (Password.length()<8)
                {
                    mPassword.setError("Password must be at least 8 characters!");
                    return;
                }

                fAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Logged in", Toast.LENGTH_SHORT).show();
                            loadUserType();
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            loadUser();
                        }
                        else
                            Toast.makeText(Login.this,"Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });


    }

    public void loadUserType(){
        String userID= Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
        assert userID != null;
        fStore.collection("User").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userType= Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getLong("Type")).intValue();
                saveData();
            }
        });
    }

    public void loadUser(){
        if (user != null) {
            loadData();
            if(userType==1){
                startActivity(new Intent(getApplicationContext(), Student.class));
                finish();
            }

            else if(userType==2) {
                startActivity(new Intent(getApplicationContext(), CR.class));
                finish();
            }
            else if(userType==3) {
                startActivity(new Intent(getApplicationContext(), Staff.class));
                finish();
            }
        }
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INT,userType);
        editor.apply();
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userType = sharedPreferences.getInt(INT, 0);
    }
}