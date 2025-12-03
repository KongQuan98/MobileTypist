# Publishing Guide for Keyboard Warrior

## Overview
Keyboard Warrior is a comprehensive typing practice app built with Kotlin Multiplatform, supporting both Android and iOS platforms.

## Features
- ✅ Time-based typing tests (15s, 30s, 60s)
- ✅ Words mode (10, 25, 50, 100 words)
- ✅ Quotes mode with inspiring quotes
- ✅ Statistics tracking and history
- ✅ Dark theme support
- ✅ Local data storage
- ✅ Beautiful, modern UI

## Pre-Publishing Checklist

### 1. App Metadata

#### Android:
- Update `composeApp/build.gradle.kts`:
  - `applicationId`: Already set to `com.keyboardwarrior.typing`
  - `versionCode`: Increment for each release
  - `versionName`: Update version string

#### iOS:
- Update `iosApp/iosApp/Info.plist`:
  - Bundle identifier
  - Version number
  - Build number

### 3. App Icons

#### Android:
- Replace icons in `composeApp/src/androidMain/res/mipmap-*/`
- Generate icons using Android Asset Studio

#### iOS:
- Update icons in `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/`
- Generate icons using Xcode Asset Catalog

### 4. Signing Configuration

#### Android:
1. Create a keystore:
   ```bash
   keytool -genkey -v -keystore keyboard-warrior.jks -keyalg RSA -keysize 2048 -validity 10000 -alias keyboard-warrior
   ```
2. Create `composeApp/keystore.properties`:
   ```properties
   storePassword=your_store_password
   keyPassword=your_key_password
   keyAlias=keyboard-warrior
   storeFile=../keyboard-warrior.jks
   ```
3. Update `composeApp/build.gradle.kts` to use signing configs

#### iOS:
- Configure signing in Xcode with your Apple Developer account
- Set up provisioning profiles

### 5. Privacy Policy & Terms

The app includes privacy policy and terms of service in the About screen. For app store submission, you may need:
- A hosted privacy policy URL
- Terms of service URL
- Data collection disclosure

### 6. Testing

Before publishing:
- [ ] Test all typing modes
- [ ] Verify statistics saving/loading
- [ ] Test theme switching
- [ ] Test on multiple devices
- [ ] Test on different Android/iOS versions
- [ ] Verify no crashes or errors

## Building for Release

### Android:
```bash
./gradlew :composeApp:assembleRelease
```
APK will be in `composeApp/build/outputs/apk/release/`

### iOS:
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select "Any iOS Device" as target
3. Product > Archive
4. Distribute App through App Store Connect

## App Store Submission

### Google Play Store:
1. Create app listing in Google Play Console
2. Upload APK/AAB
3. Fill in store listing details
4. Add screenshots and graphics
5. Set content rating
6. Submit for review

### Apple App Store:
1. Create app in App Store Connect
2. Upload build via Xcode or Transporter
3. Fill in app information
4. Add screenshots and preview videos
5. Submit for review

## Version History

### Version 1.0.0 (Initial Release)
- Time-based typing tests
- Words mode
- Quotes mode
- Statistics tracking
- Dark theme
- Settings screen
- About screen with privacy policy

## Support

For issues or questions, please contact the development team.

## License

[Add your license information here]

