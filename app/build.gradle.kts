plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.campusfix"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.campusfix"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        // Export Room schemas to app/schemas/ so future schema changes can be
        // handled with real Migration objects instead of destructive resets.
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    // Local persistence + observable data + screen state.
    // LiveData support ships inside room-runtime (no room-livedata artifact
    // exists for Room 2.7+).
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Deferrable background work (reminders, simulated sync).
    implementation(libs.work.runtime)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.room.testing)
}
