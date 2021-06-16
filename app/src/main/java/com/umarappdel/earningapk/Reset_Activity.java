package com.umarappdel.earningapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class Reset_Activity extends AppCompatActivity {

    CircularProgressButton  circularProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_actvity);

        circularProgressButton = findViewById(R.id.btn_reset_mail);

        circularProgressButton.setOnClickListener(v -> {

            startActivity(new Intent(Reset_Activity.this,LoginActivity.class));
            finish();
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(Reset_Activity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
            }
            finish();

        });

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(Reset_Activity.this,LoginActivity.class));
        finish();

    }
}