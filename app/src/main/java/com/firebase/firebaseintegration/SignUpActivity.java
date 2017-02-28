package com.firebase.firebaseintegration;

/**
 * Created by Foram Shah on 27/02/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import utils.Utils;

/***
 * Allow user to register on FireBase.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtEmail, edtPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        final Button btnSignIn = (Button) findViewById(R.id.activity_signUp_sign_in_button);
        final Button btnSignUp = (Button) findViewById(R.id.activity_signUp_btn_signup);
        edtEmail = (EditText) findViewById(R.id.activity_signUp_edtEmail);
        edtPassword = (EditText) findViewById(R.id.activity_signUp_edtPassword);
        progressBar = (ProgressBar) findViewById(R.id.activity_signUp_progressBar);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        Utils.hideKeyboard(this);
        switch (view.getId()) {
            case R.id.activity_signUp_sign_in_button:
                finish();
                break;
            case R.id.activity_signUp_btn_signup:
                registerUserToFireBase();
                break;
        }
    }

    /***
     * This method will register user to fireBase
     */
    private void registerUserToFireBase() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getString(R.string.enter_email));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(getString(R.string.enter_password));
            return;
        }
        if (password.length() < 5) {
            edtPassword.setError(getString(R.string.password_validation_message));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //Create new user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.something_went_wrong) + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, R.string.registration_done, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }
}
