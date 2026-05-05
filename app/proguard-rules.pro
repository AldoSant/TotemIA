# Retrofit
-keepattributes Signature, InnerClasses, AnnotationDefault
-keepclassmembers class retrofit2.Retrofit { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# OkHttp
-keepattributes Signature, InnerClasses, AnnotationDefault
-keepclassmembers class okhttp3.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson
-keepattributes Signature, InnerClasses, AnnotationDefault
-keep class com.google.gson.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class com.google.dagger.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManager { *; }
