@file:Suppress("DSL_SCOPE_VIOLATION")

import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("jacoco")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "com.example.spybrain"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.spybrain"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.spybrain.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    lint {
        abortOnError = false
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // AndroidX Core
    implementation(libs.core.ktx)
    implementation(libs.bundles.lifecycle)

    implementation(platform(libs.compose.bom))
    implementation("androidx.compose.animation:animation")
    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Splash Screen
    implementation(libs.core.splashscreen)
    // Material Components (XML темы)
    implementation("com.google.android.material:material:1.10.0")

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Media
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.session)
    implementation("androidx.media3:media3-common:1.2.0")
    implementation("androidx.media3:media3-datasource:1.2.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.0")

    // TTS
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    // Audio Effects
    implementation("androidx.media:media:1.7.0")

    // DataStore
    implementation(libs.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Testing
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("app.cash.turbine:turbine:0.12.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    // Debug
    debugImplementation(libs.bundles.compose.debug)

    // CameraX
    implementation("androidx.camera:camera-core:1.2.0")
    implementation("androidx.camera:camera-camera2:1.2.0")
    implementation("androidx.camera:camera-lifecycle:1.2.0")
    implementation("androidx.camera:camera-view:1.2.0")

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)
    androidTestImplementation("androidx.room:room-testing:2.6.1")

    implementation("androidx.navigation:navigation-compose:2.7.3")
}

// Detekt configuration
detekt {
    config.setFrom(files("$projectDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = true
    parallel = true
}

// KtLint configuration
ktlint {
    android.set(true)
    verbose.set(true)
    filter {
        exclude { element -> element.file.path.contains("build/") }
    }
}

// JaCoCo configuration
jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test> {
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
