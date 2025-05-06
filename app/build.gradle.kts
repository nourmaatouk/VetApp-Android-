plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.vetapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vetapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.room:room-runtime:2.5.0")
    annotationProcessor("androidx.room:room-compiler:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("org.osmdroid:osmdroid-android:6.1.16")
    implementation ("androidx.core:core:1.7.0'")  // For NotificationCompat and NotificationManagerCompat
    implementation ("androidx.appcompat:appcompat:1.3.1")
}