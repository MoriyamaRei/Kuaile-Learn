import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

// Info aplikasi (versi, alamat server) dibuat jadi kode supaya sama di Android dan web.
val appVersion = (project.findProperty("appVersion") as String?) ?: "1.1.1"
val buildNumber = (System.getenv("GITHUB_RUN_NUMBER") ?: "1").toInt()
val sbUrl = (project.findProperty("supabaseUrl") as String?) ?: System.getenv("SUPABASE_URL") ?: ""
val sbKey = (project.findProperty("supabaseKey") as String?) ?: System.getenv("SUPABASE_KEY") ?: ""
val genDir = layout.buildDirectory.dir("generated/appinfo")
val genAppInfo by tasks.registering {
    val out = genDir
    inputs.property("v", "$appVersion|$buildNumber|$sbUrl|$sbKey")
    outputs.dir(out)
    doLast {
        val f = out.get().file("com/nesxha/readit/AppInfo.kt").asFile
        f.parentFile.mkdirs()
        f.writeText(
            """
            package com.nesxha.readit

            /** Dibuat otomatis oleh shared/build.gradle.kts. */
            object AppInfo {
                const val VERSION_NAME = "$appVersion"
                const val VERSION_CODE = $buildNumber
                const val SUPABASE_URL = "$sbUrl"
                const val SUPABASE_KEY = "$sbKey"
            }
            """.trimIndent() + "\n",
        )
    }
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "kuaile"
        browser {
            commonWebpackConfig { outputFileName = "kuaile.js" }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(genAppInfo)
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
            implementation("androidx.activity:activity-compose:1.9.2")
        }
    }
}

android {
    namespace = "com.nesxha.readit.shared"
    compileSdk = 34
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.nesxha.readit.res"
    generateResClass = always
}
