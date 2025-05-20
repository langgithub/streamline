plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.lang.streamline"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lang.streamline"
        testApplicationId = "com.gtihub.streamline"

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
    signingConfigs {
        create("android_debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    buildTypes {
        // release 版使用自定义签名配置，而 testBuildType 设置为 "release"
        getByName("release") {
            signingConfig = signingConfigs["android_debug"]
        }
        // debug 版默认使用调试签名（无需额外配置）
        getByName("debug") {
            // 默认使用调试签名
        }
    }
    // 指定测试 APK 使用 release 版签名
//    testBuildType = "release"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"  // 举例
    }

    externalNativeBuild{
        cmake{
            // 在该文件种设置所要编写的c源码位置，以及编译后so文件的名字
            path = file("src/main/jni/CMakeLists.txt")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.nanohttpd)
    implementation(libs.androidx.jsonrpc4j)
    implementation(libs.androidx.jackson.core)
    implementation(libs.androidx.jackson.annotations)
    implementation(libs.androidx.jackson.databind)
    implementation(libs.androidx.uiautomator)
    implementation(libs.commons.lang3)
    implementation(project(":permission"))


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

}