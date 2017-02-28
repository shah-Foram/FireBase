package com.firebase.firebaseintegration;

/**
 * Created by Foram Shah on 27/02/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail, edtPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private final int PASSWORD_MINIMUM_LENGTH = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get FireBase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);


        edtEmail = (EditText) findViewById(R.id.activity_login_edtEmail);
        edtPassword = (EditText) findViewById(R.id.activity_login_edtPassword);
        progressBar = (ProgressBar) findViewById(R.id.activity_login_progressBar);
        final Button btnSignUp = (Button) findViewById(R.id.activity_login_btn_signup);
        final Button btnLogin = (Button) findViewById(R.id.activity_login_btnLogin);
        final Button btnForgotPassword = (Button) findViewById(R.id.activity_login_btnForgotPassword);

        //Get FireBase auth instance
        auth = FirebaseAuth.getInstance();


        btnSignUp.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    /***
     * This method will hide keyboard
     * @param activity
     */
    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    @Override
    public void onClick(View view) {
        hideKeyboard(this);
        switch (view.getId()) {

            case R.id.activity_login_btn_signup:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            case R.id.activity_login_btnForgotPassword:
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                break;
            case R.id.activity_login_btnLogin:
                String email = edtEmail.getText().toString();
                final String password = edtPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError(getString(R.string.enter_email));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError(getString(R.string.enter_password));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    if (password.length() < PASSWORD_MINIMUM_LENGTH) {
                                        edtPassword.setError(getString(R.string.password_validation_message));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                break;
        }

    }
}
