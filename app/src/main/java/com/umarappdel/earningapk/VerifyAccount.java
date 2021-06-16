package com.umarappdel.earningapk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class VerifyAccount extends AppCompatActivity {

    CircularProgressButton circularProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        circularProgressButton = findViewById(R.id.btn_verify_mail);

        circularProgressButton.setOnClickListener(v -> {

            startActivity(new Intent(VerifyAccount.this,LoginActivity.class));
            finish();
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(VerifyAccount.this, "There is no package available in android", Toast.LENGTH_LONG).show();
            }
            finish();

        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(VerifyAccount.this,LoginActivity.class));
        finish();
    }
}