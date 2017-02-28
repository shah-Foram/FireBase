package com.firebase.firebaseintegration;
/**
 * Created by Foram Shah on 27/02/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import utils.Utils;

/***
 * Allow user to perform changePassword, Send reset email link and signOut from FireBase
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnReset, btnSend;
    private EditText etdEmail, etdNewPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private final int PASSWORD_MINIMUM_LENGTH = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        final Button btnChangePassword = (Button) findViewById(R.id.activity_main_btnChangePassword);
        final Button btnSendResetEmail = (Button) findViewById(R.id.activity_main_sending_pass_reset_button);
        final Button btnSignOut = (Button) findViewById(R.id.activity_main_btnSignOut);
        btnReset = (Button) findViewById(R.id.activity_main_btnReset);
        btnSend = (Button) findViewById(R.id.activity_main_btnSend);
        etdEmail = (EditText) findViewById(R.id.activity_main_edtEmail);
        etdNewPassword = (EditText) findViewById(R.id.activity_main_etdNewPassword);
        etdEmail.setVisibility(View.GONE);
        etdNewPassword.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
        btnSend.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.activity_main_progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        btnChangePassword.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSendResetEmail.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
    }
    /***
     * Signs out from fireBase account
     */
    private void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onClick(View view) {
        Utils.hideKeyboard(this);
        switch (view.getId()) {
            case R.id.activity_main_btnChangePassword:
                etdEmail.setVisibility(View.GONE);
                etdNewPassword.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                btnSend.setVisibility(View.GONE);
                break;
            case R.id.activity_main_btnReset:
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !etdNewPassword.getText().toString().trim().equals("")) {
                    if (etdNewPassword.getText().toString().trim().length() < PASSWORD_MINIMUM_LENGTH) {
                        etdNewPassword.setError(getString(R.string.password_validation_message));
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(etdNewPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, R.string.password_updated, Toast.LENGTH_SHORT).show();
                                            etdNewPassword.setText("");
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(MainActivity.this, R.string.failed_to_update_password, Toast.LENGTH_SHORT).show();
                                            etdNewPassword.setText("");
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (etdNewPassword.getText().toString().trim().equals("")) {
                    etdNewPassword.setError(getString(R.string.enter_password));
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case R.id.activity_main_sending_pass_reset_button:
                etdEmail.setVisibility(View.VISIBLE);
                etdNewPassword.setVisibility(View.GONE);
                btnReset.setVisibility(View.GONE);
                btnSend.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_main_btnSend:
                progressBar.setVisibility(View.VISIBLE);
                if (!etdEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(etdEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, R.string.reset_password_email_success, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        etdEmail.setText("");
                                        etdEmail.setVisibility(View.GONE);
                                        btnSend.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(MainActivity.this, R.string.failed_to_send_email, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    etdEmail.setError(getString(R.string.enter_email));
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case R.id.activity_main_btnSignOut:
                signOut();
                break;
        }
    }
}
