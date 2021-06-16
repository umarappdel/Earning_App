package com.umarappdel.earningapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pollfish.classes.SurveyInfo;
import com.pollfish.constants.Position;
import com.pollfish.interfaces.PollfishClosedListener;
import com.pollfish.interfaces.PollfishCompletedSurveyListener;
import com.pollfish.interfaces.PollfishOpenedListener;
import com.pollfish.interfaces.PollfishReceivedSurveyListener;
import com.pollfish.main.PollFish;
import com.umarappdel.earningapk.model.ProfileModel;

import java.util.HashMap;

public class PollfishActivity extends AppCompatActivity implements PollfishCompletedSurveyListener,
        PollfishOpenedListener, PollfishReceivedSurveyListener, PollfishClosedListener {


    private TextView coinsTv;
    private FirebaseUser user;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pollfish);

        coinsTv = findViewById(R.id.coinsTv);

    }

    @Override
    public void onPollfishSurveyCompleted(SurveyInfo surveyInfo) {
        //survey completed

        int reward = surveyInfo.getRewardValue();

        updateData(reward);

    }

    private void updateData(int reward) {

        int currentCoins = Integer.parseInt(coinsTv.getText().toString());
        int updatedCoins = currentCoins + reward;

        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updatedCoins);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(PollfishActivity.this, "Coins added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onPollfishOpened() {
        Toast.makeText(this, "Survey opened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPollfishClosed() {
        Toast.makeText(this, "Survey closed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPollfishSurveyReceived(SurveyInfo surveyInfo) {
        Toast.makeText(this, "Survey received", Toast.LENGTH_SHORT).show();
    }

    protected void onResume() {
        super.onResume();

        //replace with your own api key
        PollFish.ParamsBuilder paramsBuilder = new PollFish.ParamsBuilder(getString(R.string.pollfish_api))
                .requestUUID(user.getUid())
                .releaseMode(false) /*set to false for testing*/
                .indicatorPosition(Position.MIDDLE_RIGHT) /*Poll fish indicator position to show on screen*/
                .indicatorPadding(12)
                .build();

        PollFish.initWith(this, paramsBuilder);

    }
}