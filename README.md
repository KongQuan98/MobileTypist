# Typely (MobileTypist) ⌨️🔥

**Typely** is a high-fidelity, modern typing practice application built with **Kotlin Multiplatform
** and **Compose Multiplatform**. Designed for speed, precision, and habit formation, it brings a
professional typing experience to both **Android** and **iOS** from a single codebase.

## 🚀 Key Features

### 🎯 Pro Typing Engine

- **Multiple Modes**: Challenge yourself with Time (15s, 30s, 60s), Word Count, or curated Quotes.
- **Precision Tracking**: Real-time character-level feedback, jump-to-word logic, and smooth
  auto-scrolling.
- **Mechanical Feel**: Immersive haptics and randomized mechanical key-click sound effects.

### 🔥 Habit & Gamification

- **Daily Streaks**: Stay motivated with a Duolingo-style streak system and milestone celebrations.
- **Achievement System**: Unlock rewards for speed, accuracy, consistency, and early-bird/night-owl
  sessions.
- **Activity Heatmap**: Visualize your long-term progress with a dynamic, theme-reactive activity
  grid.

### 📊 Deep Analytics

- **Live HUD**: Monitor WPM, Accuracy, and Time Left in real-time.
- **Results Summary**: Beautiful post-game breakdowns with performance graphs and keystroke
  analysis.
- **Social Sharing**: Generate high-fidelity result cards as images to share your progress on social
  media.

### 🎨 Personalization

- **Dynamic Themes**: Choose from multiple color schemes (Classic, Ocean, Forest, Rose, Lavender).
- **Customizable Typist**: Select your avatar and manage your profile.
- **Typography**: Adjust font size and family (Monospace, Sans-Serif, Serif) for your ideal typing
  environment.

---

## 📱 Demos

### 🎮 Gameplay & Core Loop

*Showcase of starting a test, typing, and the smooth transition to results.*

|            Android             |            iOS             |
|:------------------------------:|:--------------------------:|
| *[Android Gameplay Recording]* | *[iOS Gameplay Recording]* |

### 🔥 Streak & Achievements

*Visualizing the habit loop and achievement unlock popups.*

|           Android            |           iOS            |
|:----------------------------:|:------------------------:|
| *[Android Streak Recording]* | *[iOS Streak Recording]* |

### 🎨 Themes & Customization

*Switching between different color themes and adjusting typing settings.*

|           Android            |           iOS            |
|:----------------------------:|:------------------------:|
| *[Android Themes Recording]* | *[iOS Themes Recording]* |

---

## 🛠️ Technology Stack

- **Compose Multiplatform**: Shared UI for Android & iOS.
- **Kotlin Multiplatform (KMP)**: 100% shared business logic and state management.
- **Multiplatform Settings**: Persistent local storage for stats and user preferences.
- **Kotlinx Serialization**: Lightning-fast data parsing.
- **GraphicsLayer API**: High-performance image generation for social sharing.
- **Kamel**: Asynchronous image loading.

## 🏗️ Project Structure

* `composeApp/src/commonMain`: Shared UI and ViewModels.
* `composeApp/src/androidMain`: Android-specific implementations (Haptics, SoundPool, Sharing).
* `composeApp/src/iosMain`: iOS-specific implementations (AVAudioPlayer, UIActivityController).
* `iosApp`: Native Swift wrapper for the iOS entry point.

## 🔨 Building the Project

### Prerequisites

- Android Studio / IntelliJ IDEA
- Xcode (for iOS)
- JDK 17+

### Commands
```bash
# Build Android Debug
./gradlew :composeApp:assembleDebug

# Run on iOS Simulator (from root)
./gradlew iosRun
```

## 🔒 Privacy

Typely respects your data. Everything stays on your device:

- **No** data collection.
- **No** cloud transmission.
- **Local-only** storage via encrypted preferences where applicable.

---

*Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)*
