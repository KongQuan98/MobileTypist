# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class org.example.project.data.**$$serializer { *; }
-keepclassmembers class org.example.project.data.** {
    *** Companion;
}
-keepclasseswithmembers class org.example.project.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep data classes
-keep class org.example.project.data.** { *; }

