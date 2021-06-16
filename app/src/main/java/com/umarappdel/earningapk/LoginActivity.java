package com.umarappdel.earningapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEdit, passwordEdit;
    private Button loginBtn;
    private TextView ForgotPass;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private TextView signupTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);

        init();

        auth = FirebaseAuth.getInstance();

        clickListener();

    }

    private void init() {


        emailEdit = findViewById(R.id.emailET);
        passwordEdit = findViewById(R.id.passwordET);
        progressBar = findViewById(R.id.progressBar);
        signupTv = findViewById(R.id.signup_tv);
        ForgotPass = findViewById(R.id.btnforgot);
        loginBtn = findViewById(R.id.loginBtn);


    }

    private void clickListener() {

        ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                finish();
            }
        });

        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    emailEdit.setError("Input valid email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEdit.setError("Required");
                    return;
                }

                signIn(email, password);

            }
        });

    }

    private void signIn(String email, String password) {

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //login success

                            FirebaseUser user = auth.getCurrentUser();
                            //Check if user is verified

                            assert user != null;
                            if (user.isEmailVerified()) {

                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Error: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
        finish();
        System.exit(1);

    }

    public void onLoginClick(View view) {

        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }


}