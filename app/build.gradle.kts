plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
 namespace = "co.cachapas.app"
 compileSdk = 35
 defaultConfig { applicationId = "co.cachapas.app"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0-academica"; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
 buildFeatures { viewBinding = true }
 lint { abortOnError = true; checkReleaseBuilds = false }
}
dependencies {
 implementation("androidx.core:core-ktx:1.15.0")
 implementation("androidx.appcompat:appcompat:1.7.0")
 implementation("androidx.fragment:fragment-ktx:1.8.5")
 implementation("com.google.android.material:material:1.12.0")
 implementation("androidx.biometric:biometric:1.1.0")
 testImplementation("junit:junit:4.13.2")
}
