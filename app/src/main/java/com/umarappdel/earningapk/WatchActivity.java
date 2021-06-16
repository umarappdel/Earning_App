package com.umarappdel.earningapk;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.umarappdel.earningapk.model.ProfileModel;

import java.util.HashMap;

public class WatchActivity extends YouTubeBaseActivity implements RewardedVideoAdListener, YouTubePlayer.OnInitializedListener {

    private InterstitialAd interstitialAd;

    RewardedVideoAd rewardedVideoAd;
    AdView adView;

    private Button watchBtn1;
    private TextView coinsTv, timesTv;

    DatabaseReference reference;

    //YoutubePlayer

    YouTubePlayerView youTubePlayerView;

    MyPlayBackEventListener playBackEventListener;
    MyPlayerStateChangeListener playerStateChangeListener;

    public static final String API_KEY = "AIzaSyCLHiIJpRkxIMutN6J_JBjjQWvfup8Ablg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        init();

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(API_KEY, this);


        playerStateChangeListener = new MyPlayerStateChangeListener();
        playBackEventListener = new MyPlayBackEventListener();

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        MobileAds.initialize(this, getString(R.string.Admod_rewarded_id));
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        rewardedVideoAd.loadAd(getString(R.string.Admod_rewarded_id),
                new AdRequest.Builder().build());

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());


        loadData();

        loadInterstitialAd();
        // loadRewardedAds();

        clickListener();
        

    }

    private void clickListener() {

        watchBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchBtn1.setVisibility(View.GONE);
                if (rewardedVideoAd.isLoaded()) {
                    rewardedVideoAd.show();
                }
            }
        });

    }

  /*  private void showRewardVideo(final int i) {

        if (rewardedVideoAd.isAdLoaded()) {
            rewardedVideoAd.show();

            rewardedVideoAd.setAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoCompleted() {

                    if (i == 1) {
                        watchBtn1.setVisibility(View.GONE);
                        watchBtn2.setVisibility(View.VISIBLE);
                    }

                    if (i == 2) {
                        onBackPressed();
                    }

                    updateDataFirebase();

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onRewardedVideoClosed() {

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
            });

            rewardedVideoAd.loadAd();

            return;
        }

        if (rewardedVideoAd2.isAdLoaded()) {
            rewardedVideoAd2.show();

            rewardedVideoAd.setAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoCompleted() {

                    if (i == 1) {
                        watchBtn1.setVisibility(View.GONE);
                        watchBtn2.setVisibility(View.VISIBLE);
                    }

                    if (i == 2) {
                        onBackPressed();
                    }

                    updateDataFirebase();

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onRewardedVideoClosed() {

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
            });

            rewardedVideoAd2.loadAd();
        }

    }*/

    private void init() {

        //Admob
        adView = findViewById(R.id.banner_ad);

       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Watch and Earn");*/

        watchBtn1 = findViewById(R.id.watchBtn1);
        coinsTv = findViewById(R.id.coinsTv);
        timesTv = findViewById(R.id.time);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Youtube Video Player


        //getLifecycle().addObserver(youTubePlayerView);
    }

    /*private void loadRewardedAds() {

        rewardedVideoAd = new RewardedVideoAd(this, getString(R.string.fb_rewarded_id));
        rewardedVideoAd.loadAd();

        rewardedVideoAd2 = new RewardedVideoAd(this, getString(R.string.fb_rewarded_id_2));
        rewardedVideoAd2.loadAd();

    }*/

    private void loadInterstitialAd() {

        //admob init
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

    }

    @Override
    public void onBackPressed() {

        //admob
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    super.onAdClosed();

                }
            });

            return;
        }

        finish();

    }

    private void loadData() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ProfileModel model = snapshot.getValue(ProfileModel.class);
                coinsTv.setText(String.valueOf(model.getCoins()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WatchActivity.this, "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void updateDataFirebase() {

        int currentCoins = Integer.parseInt(coinsTv.getText().toString());
        int updatedCoin = currentCoins + 50;

        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updatedCoin);

        reference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(WatchActivity.this, "Coins added successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "Ad Loaded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        updateDataFirebase();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onResume() {

        rewardedVideoAd.resume(this);
        super.onResume();

    }


    @Override
    public void onPause() {

        super.onPause();
        rewardedVideoAd.pause(this);

    }

    @Override
    public void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

        MobileAds.initialize(this, getString(R.string.Admod_rewarded_id));
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        rewardedVideoAd.loadAd(getString(R.string.Admod_rewarded_id),
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoCompleted() {

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();

        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean b) {

        if (!b) {


            String videoId = getIntent().getStringExtra("Video_ID");
            timesTv.setText(videoId);
            youTubePlayer.loadVideo(videoId);

        }

        youTubePlayer.setPlaybackEventListener(playBackEventListener);
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {
            String error = String.format("Error initializing Youtube Player ", youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            getYoutubePlayerProvider().initialize(API_KEY, this);
        }

    }

    private YouTubePlayer.Provider getYoutubePlayerProvider() {

        return youTubePlayerView;
    }

    private final class MyPlayBackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

            showMessage("onSeekTo");

        }

    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

            updateDataFirebase();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

            showMessage("onError: " + errorReason);

        }

    }

    private void showMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

}