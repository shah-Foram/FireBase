package com.firebase.firebaseintegration;

/**
 * Created by Foram Shah on 27/02/17.
 */

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
import com.google.firebase.auth.FirebaseAuth;

import utils.Utils;

/***
 * Send password reset email to registered email id
 */
public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtEmail;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        auth = FirebaseAuth.getInstance();
        edtEmail = (EditText) findViewById(R.id.activity_reset_password_edtEmail);
        final Button btnforgotPassword = (Button) findViewById(R.id.activity_reset_password_btnForgotPassword);
        final Button btnBack = (Button) findViewById(R.id.activity_reset_password_btn_back);
        progressBar = (ProgressBar) findViewById(R.id.activity_reset_password_ProgressBar);
        btnBack.setOnClickListener(this);
        btnforgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Utils.hideKeyboard(this);
        switch (view.getId()) {
            case R.id.activity_reset_password_btn_back:
                finish();
                break;
            case R.id.activity_reset_password_btnForgotPassword:
                String email = edtEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError(getString(R.string.enter_registerd_email));
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, R.string.reset_email_sent, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, R.string.failed_to_send_reset_email, Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                                edtEmail.setText("");
                            }
                        });
                break;
        }
    }
}