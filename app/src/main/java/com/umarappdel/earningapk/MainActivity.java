package com.umarappdel.earningapk;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayetstudios.publishersdk.AyetSdk;
import com.ayetstudios.publishersdk.interfaces.UserBalanceCallback;
import com.ayetstudios.publishersdk.messages.SdkUserBalance;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.umarappdel.earningapk.fragment.FragmentReplacerActivity;
import com.umarappdel.earningapk.menu.DrawerAdapter;
import com.umarappdel.earningapk.menu.DrawerItem;
import com.umarappdel.earningapk.menu.SimpleItem;
import com.umarappdel.earningapk.menu.SpaceItem;
import com.umarappdel.earningapk.model.ProfileModel;
import com.pollfish.classes.SurveyInfo;
import com.pollfish.constants.Position;
import com.pollfish.interfaces.PollfishClosedListener;
import com.pollfish.interfaces.PollfishCompletedSurveyListener;
import com.pollfish.interfaces.PollfishOpenedListener;
import com.pollfish.interfaces.PollfishReceivedSurveyListener;
import com.pollfish.main.PollFish;
import com.unity3d.ads.UnityAds;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements PollfishCompletedSurveyListener,
        PollfishOpenedListener, PollfishReceivedSurveyListener,
        PollfishClosedListener, DrawerAdapter.OnItemSelectedListener {

    private CardView dailyCheckCard, luckyCard,
            taskCard, redeemCard, referCard, watchCard, PrivacyCard, aboutCard;

    private CircleImageView profileImage;
    private TextView coinsTv, nameTv, emailTv;
    Toolbar toolbar;

    private static final int POS_HOME = 0;
    private static final int POS_PROFILE = 1;
    private static final int POS_ACCOUNT = 2;
    private static final int POS_HELP = 3;
    private static final int POS_RATES = 4;
    private static final int POS_LOGOUT = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    DatabaseReference reference;
    private FirebaseUser user;
    private Dialog dialog;
    Internet internet;

    //Admob
    AdView adView;
    InterstitialAd interstitialAd;

    // TestMode
    private boolean TestMode = false;

    // Unity Ad
    private final String GameId = "4076311";
    private final String InterstitialAdsId = "Interstitial_Android";


    //facebook
    com.facebook.ads.InterstitialAd mInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        taskSdkInit();

        setContentView(R.layout.activity_main);

        init();

        internet = new Internet(MainActivity.this);

        //AdSettings.isTestMode(this); //for testing

        //admob banner ads
        adView = findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        UnityAds.initialize(MainActivity.this,getResources().getString(R.string.unity_app_id),TestMode);

        //admob inters ads
        loadInterstitialAd();


        checkInternetConnection();
        setSupportActionBar(toolbar);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        getDataFromDatabase();

        clickListener();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_PROFILE),
                createItemFor(POS_ACCOUNT),
                createItemFor(POS_HELP),
                createItemFor(POS_RATES),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_HOME);

    }


    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.purple))
                .withTextTint(color(R.color.purple))
                .withSelectedIconTint(color(R.color.gray))
                .withSelectedTextTint(color(R.color.gray));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);

    }

    private void clickListener() {

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd(1);
                ShowUnityInterstitialAd();
            }
        });

        referCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd(2);
                ShowUnityInterstitialAd();
            }
        });

        dailyCheckCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyCheck();
                ShowUnityInterstitialAd();
            }
        });

        redeemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd(3);
                ShowUnityInterstitialAd();
            }
        });

        luckyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd(4);
                ShowUnityInterstitialAd();
            }
        });

        aboutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd(5);
                ShowUnityInterstitialAd();
            }
        });

        PrivacyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Privacy.class));
                ShowUnityInterstitialAd();

            }
        });

        watchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd(6);
                ShowUnityInterstitialAd();
            }
        });

        taskCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowUnityInterstitialAd();

                if (AyetSdk.isInitialized()){
                    //sdk initialized

                    //Replace with your own ad slot(Create account to get adSlot and api key)
                    AyetSdk.showOfferwall(getApplication(), getString(R.string.ayet_adslot));

                }else {
                    Log.e("error", "Failed to init sdk");
                    //Todo: we need to add ad slot name and api key
                }

            }
        });

    }

    private void init() {

        dailyCheckCard = findViewById(R.id.dailyCheckCard);
        luckyCard = findViewById(R.id.luckySpinCard);
        taskCard = findViewById(R.id.taskCard);
        redeemCard = findViewById(R.id.redeemCard);
        watchCard = findViewById(R.id.watchCard);
        aboutCard = findViewById(R.id.aboutCard);
        PrivacyCard = findViewById(R.id.PrivacyCard);
        referCard = findViewById(R.id.referCard);
        coinsTv = findViewById(R.id.coinsTv);
        profileImage = findViewById(R.id.profileImage);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        toolbar = findViewById(R.id.toolbar);


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

    }

    private void getDataFromDatabase() {

        dialog.show();

        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ProfileModel model = snapshot.getValue(ProfileModel.class);
                nameTv.setText(model.getName());
                emailTv.setText(model.getEmail());
                coinsTv.setText(String.valueOf(model.getCoins()));

                Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .timeout(6000)
                        .placeholder(R.drawable.profile)
                        .into(profileImage);

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void checkInternetConnection() {

        if (internet.isConnected()) {
            new isInternetActive().execute();
        } else {
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void dailyCheck() {

        if (internet.isConnected()) {

            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("Please Wait");
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();


            final Date currentDate = Calendar.getInstance().getTime();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            reference.child("Daily Check").child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                String dbDateString = snapshot.child("date").getValue(String.class);

                                try {
                                    assert dbDateString != null;
                                    Date dbDate = dateFormat.parse(dbDateString);

                                    String xDate = dateFormat.format(currentDate);
                                    Date date = dateFormat.parse(xDate);

                                    if (date.after(dbDate) && date.compareTo(dbDate) != 0) {
                                        //reward available

                                        reference.child("Users").child(user.getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        ProfileModel model = snapshot.getValue(ProfileModel.class);

                                                        int currentCoins = model.getCoins();
                                                        int update = currentCoins + 10;

                                                        int spinC = model.getSpins();
                                                        int updatedSpins = spinC + 2;

                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("coins", update);
                                                        map.put("spins", updatedSpins);

                                                        reference.child("Users").child(user.getUid())
                                                                .updateChildren(map);

                                                        Date newDate = Calendar.getInstance().getTime();
                                                        String newDateString = dateFormat.format(newDate);

                                                        HashMap<String, String> dateMap = new HashMap<>();
                                                        dateMap.put("date", newDateString);

                                                        reference.child("Daily Check").child(user.getUid()).setValue(dateMap)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                                        sweetAlertDialog.setTitleText("Success");
                                                                        sweetAlertDialog.setContentText("Coins added to your account successfully");
                                                                        sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                                sweetAlertDialog.dismissWithAnimation();
                                                                            }
                                                                        }).show();

                                                                    }
                                                                });


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {

                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setTitleText("Failed");
                                        sweetAlertDialog.setContentText("You have already rewarded, come back tomorrow");
                                        sweetAlertDialog.setConfirmButton("Dismiss", null);
                                        sweetAlertDialog.show();

                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();

                                    sweetAlertDialog.dismissWithAnimation();

                                }


                            } else {

                                sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                sweetAlertDialog.setTitleText("System Busy");
                                sweetAlertDialog.setContentText("System is busy, please try again later!");
                                sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                                sweetAlertDialog.show();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });

        } else {
            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
            finish();
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
                            Toast.makeText(MainActivity.this, "Coins added", Toast.LENGTH_SHORT).show();
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

    class isInternetActive extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {

            InputStream inputStream = null;
            String json = "";

            try {
                //Url contain small android icon to check internet access
                // replace with your own url or with icon url
                //For more icons go to: https://icons.iconarchive.com/
                String strURL = "https://icons.iconarchive.com/";
                URL url = new URL(strURL);

                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoOutput(true);
                inputStream = urlConnection.getInputStream();
                json = "success";


            } catch (Exception e) {
                e.printStackTrace();
                json = "failed";
            }

            return json;

        }

        @Override
        protected void onPostExecute(String s) {

            if (s != null) {

                if (s.equals("success")) {
                    Toast.makeText(MainActivity.this, "Internet Connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No internet access", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(MainActivity.this, "No internet", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Validating internet", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }
    }

    private void loadInterstitialAd() {

        //admob init
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        //fb init
        mInterstitial = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_interstitial_id));
        mInterstitial.loadAd();

        UnityAds.initialize(MainActivity.this,getResources().getString(R.string.unity_app_id));

    }

    private void showInterstitialAd(final int i) {

        /*if (UnityAds.isReady(InterstitialAdsId)){
            UnityAds.show(MainActivity.this,InterstitialAdsId);
        }*/

        /*if (i == 1) {  //Todo:   that means user click on profile button
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }

        if (i == 2) { //refer code
            startActivity(new Intent(MainActivity.this, InviteActivity.class));
        }

        if (i == 3) { //redeem card
            startActivity(new Intent(MainActivity.this, RedeemActivity.class));
        }

        if (i == 4) { //lucky spin
            Intent intent = new Intent(MainActivity.this, FragmentReplacerActivity.class);
            intent.putExtra("position", 2);
            startActivity(intent);
        }

        if (i == 5) { //about card
            Intent intent = new Intent(MainActivity.this, AboutUS.class);
            intent.putExtra("position", 3);
            startActivity(intent);
        }

        if (i == 6) {
            startActivity(new Intent(MainActivity.this, Youtube_VideoList.class));
        }*/


        //facebook ad
        if (mInterstitial.isAdLoaded()) {
            mInterstitial.show();

            mInterstitial.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {


                    if (i == 1) {  //Todo:   that means user click on profile button
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }

                    if (i == 2) { //refer code
                        startActivity(new Intent(MainActivity.this, InviteActivity.class));
                    }

                    if (i == 3) { //redeem card
                        startActivity(new Intent(MainActivity.this, RedeemActivity.class));
                    }

                    if (i == 4) { //lucky spin
                        Intent intent = new Intent(MainActivity.this, FragmentReplacerActivity.class);
                        intent.putExtra("position", 2);
                        startActivity(intent);
                    }

                    if (i == 5) { //about card
                        Intent intent = new Intent(MainActivity.this, AboutUS.class);
                        intent.putExtra("position", 3);
                        startActivity(intent);
                    }

                    if (i == 6) {
                        startActivity(new Intent(MainActivity.this, Youtube_VideoList.class));
                    }

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

                    if (i == 1) {  //Todo:   that means user click on profile button
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }

                    if (i == 2) { //refer code
                        startActivity(new Intent(MainActivity.this, InviteActivity.class));
                    }

                    if (i == 3) { //redeem card
                        startActivity(new Intent(MainActivity.this, RedeemActivity.class));
                    }

                    if (i == 4) { //lucky spin
                        Intent intent = new Intent(MainActivity.this, FragmentReplacerActivity.class);
                        intent.putExtra("position", 2);
                        startActivity(intent);
                    }

                    if (i == 5) { //about card
                        Intent intent = new Intent(MainActivity.this, AboutUS.class);
                        intent.putExtra("position", 3);
                        startActivity(intent);
                    }

                    if (i == 6) {
                        startActivity(new Intent(MainActivity.this, Youtube_VideoList.class));
                    }

                }
            });

            return;

        }

        //if ads not loaded then startActivity without showing ad
        if (i == 1) {  //Todo:   that means user click on profile button
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }

        if (i == 2) { //refer code
            startActivity(new Intent(MainActivity.this, InviteActivity.class));
        }

        if (i == 3) { //redeem card
            startActivity(new Intent(MainActivity.this, RedeemActivity.class));
        }

        if (i == 4) { //lucky spin
            Intent intent = new Intent(MainActivity.this, FragmentReplacerActivity.class);
            intent.putExtra("position", 2);
            startActivity(intent);
        }

        if (i == 5) { //about card
            Intent intent = new Intent(MainActivity.this, AboutUS.class);
            intent.putExtra("position", 3);
            startActivity(intent);
        }

        if (i == 6) {
            startActivity(new Intent(MainActivity.this, Youtube_VideoList.class));
        }

    }

    @Override
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

    private void taskSdkInit(){

        String identifier = "user"+user.getUid();

        AyetSdk.init(getApplication(), identifier, new UserBalanceCallback() {
            @Override
            public void userBalanceChanged(SdkUserBalance sdkUserBalance) {

                int reward = sdkUserBalance.getAvailableBalance();

                updateData(reward);

            }

            @Override
            public void userBalanceInitialized(SdkUserBalance sdkUserBalance) {

            }

            @Override
            public void initializationFailed() {

            }
        });

    }

    @Override
    public void onItemSelected(int position) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (position == POS_LOGOUT){
            finish();
        }

        else if (position == POS_PROFILE){
            showInterstitialAd(1);
            ShowUnityInterstitialAd();
        }

        else if (position == POS_HELP){
            startActivity(new Intent(MainActivity.this, useAppActivity.class));
            ShowUnityInterstitialAd();
        }else if (position == POS_RATES){

            RateMe();
        }

        slidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void ShowUnityInterstitialAd(){

        if (UnityAds.isReady(getResources().getString(R.string.unity_InterstitialAdsId_id))){
            UnityAds.show(MainActivity.this,(getResources().getString(R.string.unity_InterstitialAdsId_id)));
        }

    }

    public void RateMe() {
        /*try {
            startActivity(new Intent(Intent.ACTION_VIEW,*//* App url Replace *//*
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }*/

        Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())));
        }

    }
}