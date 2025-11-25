import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.hilt.android)
            val work_version = "2.9.0"
            implementation("androidx.work:work-runtime-ktx:$work_version")
            implementation(libs.sqldelight.android.driver) // ❗ Add Android driver
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.materialIconsExtended)
            implementation(libs.coroutines.extensions)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.sqldelight.runtime) // ❗ SQLDelight runtime
            implementation(libs.kotlinx.datetime)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver) // ❗ Add iOS driver
        }
    }
}

sqldelight {
    database("AppDatabase") {
        packageName = "com.desktop.lumi.db"
    }
}

android {
    namespace = "com.desktop.lumi"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.desktop.lumi"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
