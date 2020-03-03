# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in Z:\Android\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontskipnonpubliclibraryclassmembers
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-dontwarn kotlin.Unit
-dontwarn retrofit2.-KotlinExtensions
# crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
# 移除Log
-assumenosideeffects class tw.com.masterhand.gmorscrm.tools.Logger { *; }

#support design
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#RecyclerView
-keep public class * extends android.support.v7.widget.RecyclerView.ItemDecoration
-keep class android.support.v7.widget.RecyclerView

#GSON
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class tw.com.masterhand.cucumbervideo.model.** { *; }

#retrofit2
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#okhttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Android architecture components
-keepclassmembers class android.arch.** { *; }
-keep class android.arch.** { *; }
-dontwarn android.arch.**
-keep class tw.com.masterhand.gmorscrm.model.** { *; }
-keep class tw.com.masterhand.gmorscrm.room.record.** { *; }
-keep class tw.com.masterhand.gmorscrm.room.setting.** { *; }
-keep class tw.com.masterhand.gmorscrm.room.local.** { *; }

# butterknife
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# universal-image-loader
-dontwarn com.nostra13.universalimageloader.**
-keep class com.nostra13.universalimageloader.** { *; }

# SweetAlert
-keep class cn.pedant.SweetAlert.Rotate3dAnimation {*;}
-keep class cn.pedant.SweetAlert.** { *; }
-keep class cn.pedant.SweetAlert.Rotate3dAnimation

# 小米推送
-keep class ctw.com.masterhand.gmorscrm.receivers.MessageReceiver {*;}
-dontwarn com.xiaomi.push.**
-dontwarn com.xiaomi.xmpush.**
-keep class org.apache.**
