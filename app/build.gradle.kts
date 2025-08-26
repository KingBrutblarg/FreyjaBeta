name: Android CI

on:
  push:
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
          cache: gradle

      - name: Set up Android cmdline-tools
        uses: android-actions/setup-android@v3

      - name: Accept Android SDK licenses
        run: yes | sdkmanager --licenses

      - name: Install required SDK packages
        run: |
          sdkmanager "platform-tools" "build-tools;34.0.0" "platforms;android-34"

      # ⬇️ ESCRIBE EL KEYSTORE DESDE EL SECRET
      - name: Restore release keystore from secret
        run: |
          echo -n "${{ secrets.ANDROID_KEYSTORE_BASE64 }}" | base64 -d > freyja-release.keystore
          ls -l freyja-release.keystore

      # ⬇️ Exporta los secrets como env vars para Gradle (coinciden con tu build.gradle.kts)
      - name: Export signing env vars
        run: |
          echo "ANDROID_KEYSTORE_PASSWORD=${{ secrets.ANDROID_KEYSTORE_PASSWORD }}" >> $GITHUB_ENV
          echo "ANDROID_KEY_ALIAS=${{ secrets.ANDROID_KEY_ALIAS }}" >> $GITHUB_ENV
          echo "ANDROID_KEY_PASSWORD=${{ secrets.ANDROID_KEY_PASSWORD }}" >> $GITHUB_ENV

      - name: Make gradlew executable
        run: chmod +x gradlew

      - name: Assemble Release
        run: ./gradlew --no-daemon assembleRelease

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-release
          path: app/build/outputs/apk/release/*.apk
          if-no-files-found: error