package com.umarappdel.earningapk;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.umarappdel.earningapk.fragment.FragmentReplacerActivity;
import com.umarappdel.earningapk.menu.DrawerAdapter;
import com.umarappdel.earningapk.menu.DrawerItem;
import com.umarappdel.earningapk.menu.SimpleItem;
import com.umarappdel.earningapk.menu.SpaceItem;
import com.unity3d.ads.UnityAds;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import static com.umarappdel.earningapk.model.Veriables.Payoneer_ImageURl;
import static com.umarappdel.earningapk.model.Veriables.JazzCashImageURL;
import static com.umarappdel.earningapk.model.Veriables.Easypaisa_ImageURL;
import static com.umarappdel.earningapk.model.Veriables.Paypal_ImageURl;

public class RedeemActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    private ImageView JazzCashImage, EasypaisaImage, PayoneerImage,PaypalImage;
    private CardView JazzCashCard, EasypaisaCard, PayoneerCard, PaypalCard;

    private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd mInterstitial;
    //Admob
    AdView adView,adView2;

    private static final int POS_HOME = 0;
    private static final int POS_PROFILE = 1;
    private static final int POS_ACCOUNT = 2;
    private static final int POS_HELP = 3;
    private static final int POS_RATES = 4;
    private static final int POS_LOGOUT = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        init();

        loadInterstitialAd();

        loadImages();
        clickListener();

        adView = findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView2 = findViewById(R.id.banner_ad2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        adView2.loadAd(adRequest2);

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

    private void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        JazzCashImage = findViewById(R.id.JazzCash_Imageurl);
        JazzCashCard = findViewById(R.id.JazzCash_Account);

        EasypaisaCard = findViewById(R.id.Easypaisa_Account);
        EasypaisaImage = findViewById(R.id.Easypaisa_Imageurl);

        PayoneerCard = findViewById(R.id.Payoneer_Account);
        PayoneerImage = findViewById(R.id.Payoneer_Imageurl);

        PaypalCard = findViewById(R.id.Paypal_Account);
        PaypalImage = findViewById(R.id.Paypal_Imageurl);

    }

    private void loadImages() {


        Glide.with(RedeemActivity.this)
                .load(JazzCashImageURL)
                .into(JazzCashImage);

        Glide.with(RedeemActivity.this)
                .load(Easypaisa_ImageURL)
                .into(EasypaisaImage);

        Glide.with(RedeemActivity.this)
                .load(Payoneer_ImageURl)
                .into(PayoneerImage);

        Glide.with(RedeemActivity.this)
                .load(Paypal_ImageURl)
                .into(PaypalImage);

    }

    private void clickListener() {

        JazzCashCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RedeemActivity.this, FragmentReplacerActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);

            }
        });

        EasypaisaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RedeemActivity.this, FragmentReplacerActivity.class);
                intent.putExtra("position", 3);
                startActivity(intent);

            }
        });

        PayoneerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RedeemActivity.this, FragmentReplacerActivity.class);
                intent.putExtra("position", 4);
                startActivity(intent);

            }
        });

        PaypalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedeemActivity.this, FragmentReplacerActivity.class);
                intent.putExtra("position", 6);
                startActivity(intent);
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

    public void onItemSelected(int position) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (position == POS_LOGOUT){
            finish();
        }

        else if (position == POS_PROFILE){
            /*showInterstitialAd(1);*/
            ShowUnityInterstitialAd();
        }

        else if (position == POS_HELP){
            startActivity(new Intent(RedeemActivity.this, useAppActivity.class));
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
            UnityAds.show(RedeemActivity.this,(getResources().getString(R.string.unity_InterstitialAdsId_id)));
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

        //
        finish();


    }

}