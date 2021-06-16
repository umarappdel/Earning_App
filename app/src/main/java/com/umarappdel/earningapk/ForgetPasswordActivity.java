package com.umarappdel.earningapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextView loginTv;
    private Button recoverBtn;
    private EditText emailEt;

    private FirebaseAuth auth;

    private ProgressBar progressBar;

    public static final String EMAIL_REGEX = "^(.+)@(.+)$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        init();

        clickListener();

    }

    private void init() {
        loginTv = findViewById(R.id.loginTV);
        emailEt = findViewById(R.id.emailET);
        recoverBtn = findViewById(R.id.recoverBtn);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
    }

    private void clickListener() {

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = emailEt.getText().toString();

                if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {

                    emailEt.setError("Input valid email");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()){

                                    Toast.makeText(ForgetPasswordActivity.this, "Password reset email send successfully",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ForgetPasswordActivity.this, Reset_Activity.class));
                                    finish();

                                }
                                else {

                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(ForgetPasswordActivity.this, "Error:"+errMsg, Toast.LENGTH_SHORT).show();

                                }

                                progressBar.setVisibility(View.GONE);

                            }

                        });

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
        finish();
    }
}