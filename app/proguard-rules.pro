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

# For Room
#-keepclassmembers class * extends androidx.room.RoomDatabase {
#  public static <T extends androidx.room.RoomDatabase> T createSENDFILE(android.content.Context, java.lang.Class<T>, java.lang.String);
#}
#-keep class * extends androidx.room.TypeConverter
#-keepclassmembers class * {
#    @androidx.room.Entity <fields>;
#    @androidx.room.PrimaryKey <fields>;
#    @androidx.room.ColumnInfo <fields>;
#    @androidx.room.Embedded <fields>;
#    @androidx.room.Relation <fields>;
#    @androidx.room.Query <methods>;
#    @androidx.room.Insert <methods>;
#    @androidx.room.Update <methods>;
#    @androidx.room.Delete <methods>;
#    @androidx.room.Transaction <methods>;
#}