pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ReadIt"
include(":app")
// Kode bersama Android + web (Compose Multiplatform). Aplikasi web untuk iPhone dibangun dari modul ini.
include(":shared")
