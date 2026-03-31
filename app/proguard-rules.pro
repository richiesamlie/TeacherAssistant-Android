# Add project specific ProGuard rules here.

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keep class com.localattendance.teacherassistant.data.model.** { *; }
-keepattributes Signature
-keep class com.google.gson.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Keep generic signature of Call, Response
-keepattributes Signature

# Keep generic signature of Response
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations

# Keep annotation
-keepattributes *Annotation*

# Keep classes for Retrofit
-keep class retrofit2.** { *; }
-keepattributes Exceptions
