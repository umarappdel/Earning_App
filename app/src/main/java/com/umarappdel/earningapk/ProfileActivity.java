package com.umarappdel.earningapk;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.umarappdel.earningapk.menu.DrawerAdapter;
import com.umarappdel.earningapk.menu.DrawerItem;
import com.umarappdel.earningapk.menu.SimpleItem;
import com.umarappdel.earningapk.menu.SpaceItem;
import com.umarappdel.earningapk.model.ProfileModel;
import com.unity3d.ads.UnityAds;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    private CircleImageView profileImage;
    private TextView nameTv, emailTv, coinsTv;
    private ImageView imageEditBtn, onBackPress;
    /*private TextView updateBtn;*/
    private CardView shareTv, redeemHistoryTv, logoutTv , Withdraw ;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private LinearLayout linearLayout_Update_Image;

    private static final int IMAGE_PICKER = 1;
    private Uri photoUri;
    private String imageUrl;
    Dialog dialog;
    private ProgressDialog progressDialog;
    private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd mInterstitial;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        loadInterstitialAd();
        loadDataFromDatabase();
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

    private void init() {

        profileImage = findViewById(R.id.profileImage);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        shareTv = findViewById(R.id.shareTv);
        redeemHistoryTv = findViewById(R.id.redeemHistoryTv);
        logoutTv = findViewById(R.id.logoutTv);
        coinsTv = findViewById(R.id.coinsTv);
        imageEditBtn = findViewById(R.id.editImage);
        linearLayout_Update_Image = findViewById(R.id.linearlayout_updateBtn);
        Withdraw = findViewById(R.id.withdraw);
        /*onBackPress = findViewById(R.id.Back_press);*/

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);

    }

    private void loadDataFromDatabase() {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }

    private void clickListener() {

        Withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ProfileActivity.this,RedeemActivity.class));
                finish();

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child(user.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                ProfileModel model = snapshot.getValue(ProfileModel.class);
                                ImageView ImageView = dialog.findViewById(R.id.custom_profile_image);

                                Glide.with(getApplicationContext())
                                        .load(model.getImage())
                                        .timeout(6000)
                                        .placeholder(R.drawable.profile)
                                        .into(ImageView);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.custom_profile_dialogbox);
                dialog.dismiss();
                dialog.show();

            }
        });

        redeemHistoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HistoryActivity.class));
                finish();
            }
        });

        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();

            }
        });

        shareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shareBody = "Check out the best earning app. Download " + getString(R.string.app_name) +
                        " from Play Store\n" +
                        "https://drive.google.com/file/d/1cAFYoUj-StOVqC85YPdP4cJm93VDe9GF/view?usp=sharing" +
                        getPackageName();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                intent.setType("text/plain");
                startActivity(intent);

            }
        });

        imageEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(ProfileActivity.this)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, IMAGE_PICKER);

                                } else {
                                    Toast.makeText(ProfileActivity.this, "Please allow permission", Toast.LENGTH_SHORT)
                                            .show();
                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        }).check();

            }
        });

        linearLayout_Update_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK && data!= null) {

           Uri photoUri  = data.getData();
            linearLayout_Update_Image.setVisibility(View.VISIBLE);
            CropImage.activity(photoUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setActivityTitle("Crop Image")
                    .setFixAspectRatio(true)
                    .setCropMenuCropButtonTitle("Done")
                    .start(this);


            /*if (data != null) {
                photoUri = data.getData();
                updateBtn.setVisibility(View.VISIBLE);

            }*/
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photoUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void uploadImage() {

        if (photoUri == null) {
            return;
        }

        String fileName = user.getUid() + ".jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference().child("Images/" + fileName);


        progressDialog.show();

        storageReference.putFile(photoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = uri.toString();

                                uploadImageUrlToDatabase();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                long totalSi = taskSnapshot.getTotalByteCount();
                long transferS = taskSnapshot.getBytesTransferred();

                long totalSize = (totalSi / 1024);
                long transferSize = transferS / 1024;

                progressDialog.setMessage("Uploaded " + ((int) transferSize) + "KB / " + ((int) totalSize) + "KB");

            }
        });


    }

    private void uploadImageUrlToDatabase() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("image", imageUrl);

        reference.child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        linearLayout_Update_Image.setVisibility(View.GONE);
                        progressDialog.dismiss();
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
            startActivity(new Intent(ProfileActivity.this, useAppActivity.class));
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
            UnityAds.show(ProfileActivity.this,(getResources().getString(R.string.unity_InterstitialAdsId_id)));
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

        finish();
        //fb
        if (mInterstitial.isAdLoaded()) {
            mInterstitial.show();

            mInterstitial.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

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

        //admob: // if fb ad not loaded then show admob ad
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

        finish();

    }


}