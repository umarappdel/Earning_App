package com.umarappdel.earningapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.umarappdel.earningapk.model.ProfileModel;

import java.util.HashMap;

public class InviteActivity extends AppCompatActivity {

    private FirebaseUser user;
    private String oppositeUID;

    private TextView referCodeTv;
    private Button shareBtn, redeemBtn;
    DatabaseReference reference;

    private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd mInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        init();

        loadInterstitialAd();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        loadData();
        redeemAvailability();
        clickListener();

    }

    private void redeemAvailability() {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists() && snapshot.hasChild("redeemed")) {

                            //Here to check if value is true then user already redeemed/
                            boolean isAvailable = snapshot.child("redeemed").getValue(Boolean.class);

                            if (isAvailable) {
                                redeemBtn.setVisibility(View.GONE);
                                redeemBtn.setEnabled(false);
                            } else {// if false user is new and not redeemed
                                redeemBtn.setEnabled(true);
                                redeemBtn.setVisibility(View.VISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        referCodeTv = findViewById(R.id.referCodeTv);
        shareBtn = findViewById(R.id.shareBtn);
        redeemBtn = findViewById(R.id.redeemBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadData() {

        reference.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String referCode = snapshot.child("referCode").getValue(String.class);
                        referCodeTv.setText(referCode);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(InviteActivity.this, "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });

    }

    private void clickListener() {

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String referCode = referCodeTv.getText().toString();

                String shareBody = "Hey, i'm using the best earning app. Join using my invite code to instantly get 100"
                        + " coins. My invite code is  " + referCode + "\n" +
                        "Download from Play Store\n" +
                        "https://drive.google.com/file/d/1cAFYoUj-StOVqC85YPdP4cJm93VDe9GF/view?usp=sharing" +
                        getPackageName();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(intent);
            }
        });


        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText = new EditText(InviteActivity.this);
                editText.setHint("abc12");

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                editText.setLayoutParams(layoutParams);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(InviteActivity.this);

                alertDialog.setTitle("Redeem Code");

                alertDialog.setView(editText);

                alertDialog.setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String inputCode = editText.getText().toString();

                        if (TextUtils.isEmpty(inputCode)) {
                            Toast.makeText(InviteActivity.this, "Input valid code", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (inputCode.equals(referCodeTv.getText().toString())) {
                            Toast.makeText(InviteActivity.this, "You can not input your own code",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        redeemQuery(inputCode, dialog);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();


            }
        });


    }

    private void redeemQuery(String inputCode, final DialogInterface dialog) {


        Query query = reference.orderByChild("referCode").equalTo(inputCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    oppositeUID = dataSnapshot.getKey();

                    reference
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ProfileModel model = snapshot.child(oppositeUID).getValue(ProfileModel.class);
                                    ProfileModel myModel = snapshot.child(user.getUid()).getValue(ProfileModel.class);

                                    assert myModel != null;
                                    assert model != null;

                                    int coins = model.getCoins();
                                    int updatedCoins = coins + 100;

                                    int myCoins = myModel.getCoins();
                                    int myUpdate = myCoins + 100;

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("coins", updatedCoins);

                                    HashMap<String, Object> myMap = new HashMap<>();
                                    myMap.put("coins", myUpdate);
                                    myMap.put("redeemed", true);

                                    reference.child(oppositeUID).updateChildren(map);
                                    reference.child(user.getUid()).updateChildren(myMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    dialog.dismiss();
                                                    Toast.makeText(InviteActivity.this, "Congrats", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InviteActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadInterstitialAd() {

        //admob init
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        //fb init
        mInterstitial = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_interstitial_id));
        mInterstitial.loadAd();

    }

    @Override
    public void onBackPressed() {

        //fb
        if (mInterstitial.isAdLoaded()) {
            mInterstitial.show();

            mInterstitial.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    finish();
                }

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });

            return;

        }

        //admob
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();

            interstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }
            });

            return;
        }

        //if ad not loaded then ....
        finish();

    }
}