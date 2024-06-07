plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.google.gms.google.services)
	alias(libs.plugins.google.firebase.crashlytics)
}

android {
	namespace = "vinh.le.bookappkotlin"
	compileSdk = 34
	
	defaultConfig {
		applicationId = "vinh.le.bookappkotlin"
		minSdk = 33
		targetSdk = 33
		versionCode = 1
		versionName = "1.0"
		
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	
	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures{
		viewBinding = true
	}
}

dependencies {
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation("com.github.barteksc:android-pdf-viewer:2.8.2")
	implementation(libs.androidx.constraintlayout)
	implementation(libs.firebase.analytics)
	implementation(libs.firebase.auth)
	implementation(libs.firebase.database)
	implementation(libs.firebase.crashlytics)
	implementation(libs.firebase.storage)
	implementation(libs.androidx.activity)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}
