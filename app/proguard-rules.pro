# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Model class
-keep class com.umarappdel.earningapk.model.ProfileModel { *;}
-keep class com.umarappdel.earningapk.model.AmazonModel { *;}
-keep class com.umarappdel.earningapk.model.HistoryModel { *;}


#Sweet Alert Dialog
-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
    public <init> (...);
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

#Aye T
-keep class com.ayetstudios.publishersdk.messages.** {*;}
-keep public class com.ayetstudios.publishersdk.AyetSdk
-keepclassmembers class com.ayetstudios.publishersdk.AyetSdk {
   public *;
}
-keep public interface com.ayetstudios.publishersdk.interfaces.UserBalanceCallback { *; }
-keep public interface com.ayetstudios.publishersdk.interfaces.DeductUserBalanceCallback { *; }

-keep class com.ayetstudios.publishersdk.models.VastTagReqData { *; }
