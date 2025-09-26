# Pronunciation Correction Android App (Chaquopy + TensorFlow Lite)

An Android application for pronunciation correction and labeling. It integrates on-device inference with TensorFlow Lite and audio processing via Python (Chaquopy). The app includes activities for recording, labeling, word cards, and inference.

## Features
- On-device inference using TensorFlow Lite (`app/src/main/assets/EfficientNetB0_0929.tflite`)
- Audio processing with Python via Chaquopy
- Recording, playback, and labeling flows
- Word card exercises and result visualization
- Compatible ABIs: `armeabi-v7a`, `arm64-v8a`

## Tech Stack
- Android SDK 30 (minSdk 19, targetSdk 30)
- Kotlin/Java Android app (see `app/src/main/java`)
- Chaquopy for embedded Python (`apply plugin: 'com.chaquo.python'`)
- TensorFlow Lite 2.2.0
- AndroidX (AppCompat, ConstraintLayout, RecyclerView, Material)

## Project Structure
```
android/                 # launcher icons and store imagery
app/
  build.gradle           # module config, Chaquopy + TFLite setup
  src/main/
    AndroidManifest.xml  # permissions, activities, app config
    assets/
      EfficientNetB0_0929.tflite
    java/                # app source code (packages under com.cgh...)
    python/
      mfcc.py            # audio feature extraction (example)
    res/                 # layouts, values, drawables, menus
build.gradle             # project-level gradle config
settings.gradle          # includes :app
gradle.properties        # AndroidX, Jetifier, JVM args
```

## Prerequisites
- Android Studio (Arctic Fox or newer recommended)
- Android SDK 30 platform installed
- JDK 8 (for source/target compatibility 1.8)
- Internet access for Gradle + Chaquopy dependency resolution

Optional (for local Python builds on Windows):
- Python 3.8 installed if you plan to rebuild Python wheels (see the `buildPython` path in `app/build.gradle`). For CI or Linux/macOS, remove or adjust that line.

## Setup
1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync. If prompted to install missing SDK components, accept.
4. If you are not on Windows, comment or remove this line in `app/build.gradle` under `python { pip { ... } }`:
```
buildPython "C:\\Users\\kuo\\AppData\\Local\\Programs\\Python\\Python38\\python.exe"
```
Chaquopy will download prebuilt wheels automatically when possible.
5. Ensure the TFLite model exists at `app/src/main/assets/EfficientNetB0_0929.tflite`. If you have a different model name/path, update the code accordingly.

## Building and Running
- Build Release (R8/Proguard enabled): use Android Studio Build > Generate Signed Bundle / APK.
- Build Debug: select a connected device or emulator and click Run.
- Target ABIs: `armeabi-v7a`, `arm64-v8a` (set in `ndk.abiFilters`). Ensure your emulator/device matches one of these.

Permissions used (declared in `AndroidManifest.xml`):
- Network: `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `CHANGE_WIFI_STATE`
- Storage: `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`
- Location: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- Audio: `RECORD_AUDIO`

## Python Dependencies
Configured via Chaquopy in `app/build.gradle`:
```
python {
    pip {
        install "librosa"
    }
}
```
Add additional packages as needed. Avoid heavy native dependencies unless Chaquopy supports them.

## Useful Gradle Tasks
- Clean: `./gradlew clean`
- Assemble debug: `./gradlew :app:assembleDebug`
- Assemble release: `./gradlew :app:assembleRelease`

## Contribution Guide
- Use feature branches with descriptive names, e.g., `docs/readme-improvements`.
- Follow Conventional Commits for messages (e.g., `docs: improve README`).
- Open a pull request; include screenshots or recordings if UI changes are involved.

## Troubleshooting
- Chaquopy wheel resolution issues: remove `buildPython` hardcoded path or point to a valid Python installation on your machine.
- `org.apache.http.legacy` library not found: ensure you are building with Android SDK 30+, and the legacy library usage is declared (already included in manifest and module config).
- If Gradle runs out of memory, adjust `org.gradle.jvmargs` in `gradle.properties`.

## License
Add your license here (e.g., MIT). If this is proprietary, state the ownership and usage restrictions.