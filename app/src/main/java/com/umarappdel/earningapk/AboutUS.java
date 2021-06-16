package com.umarappdel.earningapk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_u_s);


        String versionName = androidx.multidex.BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;

        String version = "Version "+versionName+"."+versionCode;


        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(" Add descrition about your app") // for next line  use = \n
                .addItem(new Element()
                .setTitle(version))
                .addGroup("CONNECT WITH US!")
                .addEmail("umarappdel@gmail.com")
                .addWebsite("https://mobileappsdotcom.wordpress.com/")
                .addYoutube("https://www.youtube.com/channel/UCDuBSyS3JcWZb8d3xZhzEQw")   //Enter your youtube link here (replace with my channel link)
                .addPlayStore("com.umarappdel.earningapk")   //Replace all this with your package name
                .addInstagram("https://www.instagram.com/")    //Your instagram idhttps://www.instagram.com/umar11_22/
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %dby Click to Earn",
                Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        // copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUS.this,copyrightString, Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}