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

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Retrofit
//    api 'com.squareup.retrofit2:retrofit:2.9.0'

}

// 1. 注册一个 Copy 任务，把编译产物拷贝到 assets
val copySendEvent by tasks.registering(Copy::class) {
    // 你的可执行文件路径
    val exeFile = file("$buildDir/intermediates/cxx/Debug/25a322m5/obj/arm64-v8a/send_event")

    // 如果存在就拷贝
    if (exeFile.exists()) {
        from(exeFile) {
            // 路径里的 abi 名称，这里硬编码为 arm64-v8a
            into("arm64-v8a")
            // 设置文件权限为 0755
            fileMode = 0b111101101  // 等同于 0755
        }
    }

    // 最终拷贝到 module 下的 src/main/assets
    into("$projectDir/src/main/assets")
}

// 2. 让 preBuild 依赖于这个拷贝任务，保证每次编译前都会执行
tasks.named("preBuild") {
    dependsOn(copySendEvent)
}