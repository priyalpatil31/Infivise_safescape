// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // Android application plugin (via Version Catalog)
    alias(libs.plugins.android.application) apply false

    // Google Services plugin for Firebase
    id("com.google.gms.google-services") version "4.4.0" apply false
}
