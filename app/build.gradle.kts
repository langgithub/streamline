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

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Retrofit
//    api 'com.squareup.retrofit2:retrofit:2.9.0'

}

// 1. ע��һ�� Copy ���񣬰ѱ���������� assets
val copySendEvent by tasks.registering(Copy::class) {
    // ��Ŀ�ִ���ļ�·��
    val exeFile = file("$buildDir/intermediates/cxx/Debug/25a322m5/obj/arm64-v8a/send_event")

    // ������ھͿ���
    if (exeFile.exists()) {
        from(exeFile) {
            // ·����� abi ���ƣ�����Ӳ����Ϊ arm64-v8a
            into("arm64-v8a")
            // �����ļ�Ȩ��Ϊ 0755
            fileMode = 0b111101101  // ��ͬ�� 0755
        }
    }

    // ���տ����� module �µ� src/main/assets
    into("$projectDir/src/main/assets")
}

// 2. �� preBuild ����������������񣬱�֤ÿ�α���ǰ����ִ��
tasks.named("preBuild") {
    dependsOn(copySendEvent)
}