-keepattributes SourceFile,LineNumberTable

-dontwarn com.google.**
-dontwarn com.squareup.okhttp.**
-keep class com.orm.** { *; }
-keep class uk.co.jakelee.blacksmith.model.** { *; }

-keepclassmembers enum * {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes JavascriptInterface
-keep class * extends java.util.ListResourceBundle {
protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
@com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}
-keep class com.google.android.gms.ads.identifier.** { *; }

-dontwarn org.apache.http.**
-dontwarn android.net.**
-dontwarn com.google.ads.**
-dontwarn com.vmax.android.ads.volley.toolbox.**
-dontwarn com.chartboost.sdk.impl.**

-keep public class com.ablar.android.common.* { public *; }
-dontwarn com.ablar.android.common.**

-keep class com.batch.** {
  *;
}
-keep class com.google.android.gms.** {
  *;
}

-dontwarn com.batch.android.mediation.**

-dontwarn com.batch.android.BatchPushService

-keep class com.moat.** { *; }
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-keep class * extends java.util.ListResourceBundle {
protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
@com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}
-keep class com.google.android.gms.ads.identifier.** { *; }
