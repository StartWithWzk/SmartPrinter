# Proguard rules that are applied to your test apk/code.

# Some methods are only called from tests, so make sure the shrinker keeps them.
-keep class com.qg.smartprinter.** { *; }

-keep class android.support.test.espresso.IdlingResource { *; }
-keep class com.qg.common.base.Preconditions { *; }

-ignorewarnings

-keepattributes *Annotation*

-dontnote junit.framework.**
-dontnote junit.runner.**

-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn com.squareup.javawriter.JavaWriter
# Uncomment this if you use Mockito
-dontwarn org.mockito.**