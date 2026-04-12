# Keyboard Typist

A comprehensive typing practice app built with Kotlin Multiplatform for Android and iOS.

## Features

### 🎯 Multiple Typing Modes
- **Time Mode**: Test your typing speed with timed challenges (15s, 30s, 60s)
- **Words Mode**: Type a specific number of words (10, 25, 50, 100)
- **Quotes Mode**: Practice typing with inspiring quotes and passages

### 📊 Statistics & Tracking
- Track your best WPM (Words Per Minute)
- View detailed statistics for each test
- See your accuracy, correct characters, and errors
- View test history with timestamps
- Calculate average WPM and accuracy

### 🎨 User Experience
- Beautiful, modern dark theme UI
- Smooth animations and transitions
- Real-time typing feedback with color-coded characters
- Character-by-character accuracy tracking
- Word-by-word progress tracking

### ⚙️ Settings & Customization
- Dark theme toggle
- Sound effects preferences
- Vibration settings
- Statistics display options
- Clear all data option

### 📱 Platform Support
- Android (API 24+)
- iOS (via Kotlin Multiplatform)
- Shared business logic and UI code


## Project Structure

* `/composeApp` contains the shared Compose Multiplatform code:
  - `commonMain`: Shared code for all platforms
  - `androidMain`: Android-specific implementations
  - `iosMain`: iOS-specific implementations

* `/iosApp` contains the iOS app entry point and Xcode project

## Building the Project

### Prerequisites
- Android Studio or IntelliJ IDEA
- JDK 11 or higher
- Xcode (for iOS builds)
- Android SDK (for Android builds)

### Android
```bash
./gradlew :composeApp:assembleDebug
```

### iOS
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Build and run

## Setup for Publishing

See [PUBLISHING.md](PUBLISHING.md) for detailed instructions on:
- App signing
- Store submission
- Version management

## Technologies Used

- **Kotlin Multiplatform**: Shared codebase for Android and iOS
- **Jetpack Compose Multiplatform**: Modern declarative UI
- **Material 3**: Material Design components
- **MultiplatformSettings**: Local data storage
- **Kotlinx Serialization**: Data serialization
- **Ktor**: HTTP client (for future features)

## App Architecture

- **Navigation**: Custom navigation manager with screen-based routing
- **Data Storage**: Local storage using SharedPreferences (Android) and UserDefaults (iOS)
- **State Management**: Compose state management with remember and mutableState

## Privacy

Keyboard Typist respects your privacy:
- All data is stored locally on your device
- No data is transmitted or shared
- No personal information is collected

## License

[Add your license information here]

## Contributing

[Add contribution guidelines if applicable]

## Support

For issues, questions, or feature requests, please contact the development team.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)