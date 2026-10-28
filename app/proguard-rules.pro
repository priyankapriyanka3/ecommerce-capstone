# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-dontwarn com.google.gson.**
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
