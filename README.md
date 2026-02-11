# Grandma Flashlight

A simple Android app: one full-screen button toggles the device flashlight on and off. Button shows «Включить фонарик» when off and «Выключить фонарик» when on (Russian UI, large text). Screen is light blue when the flashlight is off and yellow when on; text is black.

## Build and run

- **Android Studio**: Open this folder as a project. Sync Gradle, then Run on a device or emulator (device must have a flash).
- **Command line**: From the project root, run `./gradlew assembleDebug` (or `gradlew.bat assembleDebug` on Windows). If the wrapper is missing, open the project in Android Studio once to generate it, or install [Gradle](https://gradle.org/install/) and run `gradle wrapper`.

## CI

On each push to `master`, GitHub Actions runs tests and builds the debug APK (Gradle + Android SDK; SDK path set via job env and `local.properties`). The APK is available as an artifact named **Grandma-Flashlight** (Actions → select run → Artifacts → Grandma-Flashlight).

## App icon

The launcher icon is a vector flashlight (beam, body, power button) in the app’s drawable resources.

## Requirements

- Android 7.0 (API 24) or higher
- Device with camera flash
- Camera permission (requested at first use)
