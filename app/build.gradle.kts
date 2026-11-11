plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.nesxha.readit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nesxha.readit"
        minSdk = 26
        targetSdk = 34
        versionCode = (System.getenv("GITHUB_RUN_NUMBER") ?: "1").toInt()
        versionName = (project.findProperty("appVersion") as String?) ?: "1.1.1"
        // Alamat dan publishable key Supabase (boleh publik; keamanan dijaga fungsi di server).
        // Kosong = Mode Demo.
        val sbUrl = (project.findProperty("supabaseUrl") as String?) ?: System.getenv("SUPABASE_URL") ?: ""
        val sbKey = (project.findProperty("supabaseKey") as String?) ?: System.getenv("SUPABASE_KEY") ?: ""
        buildConfigField("String", "SUPABASE_URL", "\"$sbUrl\"")
        buildConfigField("String", "SUPABASE_KEY", "\"$sbKey\"")
    }

    // Kunci rilis dibaca dari lingkungan (GitHub Secrets di CI). Tidak ada kunci rahasia di repo.
    // Build debug memakai kunci debug bawaan Android, jadi tetap bisa dijalankan di laptop.
    signingConfigs {
        create("release") {
            val ks = System.getenv("KEYSTORE_FILE")
            if (ks != null) {
                storeFile = file(ks)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            // Kunci debug bawaan Android (tidak rahasia).
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    testOptions { unitTests.isReturnDefaultValues = true }
}

dependencies {
    implementation(project(":shared"))
    val bom = platform("androidx.compose:compose-bom:2024.09.03")
    implementation(bom)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    // Fragment baru wajib supaya registerForActivityResult bekerja (pustaka Google membawa versi lama).
    implementation("androidx.fragment:fragment-ktx:1.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("androidx.glance:glance-appwidget:1.1.0")
    implementation("androidx.glance:glance-material3:1.1.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    // Scan kartu KMC: pemindai QR/barcode Google (tanpa izin kamera) dan pembaca teks ML Kit.
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    // Pengenal tulisan tangan Google (urutan goresan bebas). Model Mandarin diunduh sekali, lalu offline.
    implementation("com.google.mlkit:digital-ink-recognition:18.1.0")
    // Tes: membaca ulang QR buatan sendiri (util/Qr) untuk memastikan bisa dipindai.
    testImplementation("com.google.zxing:core:3.5.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20240303")
}
