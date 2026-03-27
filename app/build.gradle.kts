plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.pallisahayak.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pallisahayak.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000\"")
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://api.pallisahayak.org\"")
        }
    }

    flavorDimensions += "site"
    productFlavors {
        create("allSites") {
            dimension = "site"
            buildConfigField("String", "SITE_ID", "\"all\"")
        }
        create("cmcVellore") {
            dimension = "site"
            buildConfigField("String", "SITE_ID", "\"cmc_vellore\"")
        }
        create("kmcManipal") {
            dimension = "site"
            buildConfigField("String", "SITE_ID", "\"kmc_manipal\"")
        }
        create("ccfCoimbatore") {
            dimension = "site"
            buildConfigField("String", "SITE_ID", "\"ccf_coimbatore\"")
        }
        create("cchrcSilchar") {
            dimension = "site"
            buildConfigField("String", "SITE_ID", "\"cchrc_silchar\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-voice"))
    implementation(project(":feature:feature-onboarding"))
    implementation(project(":feature:feature-query"))
    implementation(project(":feature:feature-home"))
    implementation(project(":core:core-model"))
    implementation(project(":core:core-data"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-security"))
    implementation(project(":core:core-ui"))

    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.navigation)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.runtime)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.compose.ui.test)
}
