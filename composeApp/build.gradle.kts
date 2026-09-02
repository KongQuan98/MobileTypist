import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
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
            implementation(libs.ktor.client.okhttp)
            implementation(libs.backdrop)
            implementation(libs.capsule)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.feather)
            implementation(libs.kamel.image.default)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.test)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.kt)
            implementation(libs.supabase.auth)
            implementation(libs.ktor.client.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.keyboardwarrior.typing"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

fun String.escapeForKotlinString(): String =
    replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\$", "\${'\$'}")
        .replace("\n", " ")

val supabaseUrl = (
        System.getenv("SUPABASE_URL")
            ?: localProperties.getProperty("SUPABASE_URL")
            ?: ""
        ).trim().escapeForKotlinString()

val supabasePublishableKey = (
        System.getenv("SUPABASE_PUBLISHABLE_KEY")
            ?: localProperties.getProperty("SUPABASE_PUBLISHABLE_KEY")
            ?: ""
        ).trim().escapeForKotlinString()

val generateSupabaseConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/supabase/kotlin").get().asFile
    val url = supabaseUrl
    val key = supabasePublishableKey

    outputs.dir(outputDir)
    inputs.property("supabaseUrl", url)
    inputs.property("supabasePublishableKey", key)

    doLast {
        val packageDir = outputDir.resolve("org/example/project/auth")
        packageDir.mkdirs()
        packageDir.resolve("SupabaseConfig.kt").writeText(
            """
            package org.example.project.auth

            internal object SupabaseConfig {
                const val url: String = "$url"
                const val publishableKey: String = "$key"
                val isConfigured: Boolean = url.isNotBlank() && publishableKey.isNotBlank()
            }

            """.trimIndent() + "\n"
        )
    }
}

kotlin.sourceSets.getByName("commonMain").kotlin.srcDir(
    generateSupabaseConfig.map { it.outputs.files.singleFile }
)

