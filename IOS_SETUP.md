# iOS Setup Guide

This guide explains how to build and run the Lumi app on iOS.

## Prerequisites

1. **macOS** - iOS development requires a Mac
2. **Xcode** - Install Xcode from the App Store (version 15.0 or later recommended)
3. **CocoaPods** (optional) - Usually not needed for Compose Multiplatform projects

## Building the iOS Framework

Before running the iOS app, you need to build the Kotlin Multiplatform framework:

```bash
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

Or build all iOS targets:

```bash
./gradlew :composeApp:assemble
```

## Running on iOS Simulator

### Option 1: Using Xcode (Recommended)

1. Open the Xcode project:
   ```bash
   open iosApp/iosApp.xcodeproj
   ```

2. Select a simulator from the device menu (e.g., "iPhone 15 Pro")

3. Click the Run button (▶️) or press `Cmd + R`

### Option 2: Using Command Line

```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
           -scheme iosApp \
           -destination 'platform=iOS Simulator,name=iPhone 15 Pro' \
           build
```

## Running on Physical Device

1. Open `iosApp/iosApp.xcodeproj` in Xcode

2. Select your device from the device menu

3. In Xcode, go to **Signing & Capabilities**:
   - Select your development team
   - Xcode will automatically manage signing

4. Click Run (▶️)

## Troubleshooting

### Framework Not Found

If you get "Framework 'ComposeApp' not found":
1. Build the framework first: `./gradlew :composeApp:assemble`
2. Clean and rebuild in Xcode: `Product > Clean Build Folder` (Shift + Cmd + K)

### Build Errors

1. Make sure you've built the Kotlin framework:
   ```bash
   ./gradlew :composeApp:assemble
   ```

2. In Xcode, try:
   - Product > Clean Build Folder
   - Product > Build

### Simulator Issues

- Make sure you have at least one iOS simulator installed
- In Xcode: **Xcode > Settings > Platforms** to download simulators

## Project Structure

- `composeApp/src/commonMain/` - Shared Kotlin code
- `composeApp/src/iosMain/` - iOS-specific Kotlin code
- `iosApp/iosApp/` - SwiftUI app entry point
- `iosApp/iosApp/ContentView.swift` - SwiftUI wrapper for Compose
- `iosApp/iosApp/iOSApp.swift` - iOS app entry point

## Notes

- The app uses Compose Multiplatform, so most UI code is shared between Android and iOS
- iOS-specific code goes in `composeApp/src/iosMain/`
- The SwiftUI wrapper (`ContentView.swift`) bridges Compose to iOS

