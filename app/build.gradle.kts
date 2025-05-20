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
        // release ��ʹ���Զ���ǩ�����ã��� testBuildType ����Ϊ "release"
        getByName("release") {
            signingConfig = signingConfigs["android_debug"]
        }
        // debug ��Ĭ��ʹ�õ���ǩ��������������ã�
        getByName("debug") {
            // Ĭ��ʹ�õ���ǩ��
        }
    }
    // ָ������ APK ʹ�� release ��ǩ��
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
        kotlinCompilerExtensionVersion = "1.5.0"  // ����
    }

    externalNativeBuild{
        cmake{
            // �ڸ��ļ���������Ҫ��д��cԴ��λ�ã��Լ������so�ļ�������
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