# Running iOS App from Android Studio

Android Studio can help you build and run iOS apps. Here's how to set it up:

## Prerequisites

1. **macOS** - Required for iOS development
2. **Xcode** - Must be installed (even if you don't open it)
3. **iOS Simulator** - At least one simulator must be installed

## Steps to Run iOS from Android Studio

### 1. Build the iOS Framework

First, build the Kotlin framework for iOS:

```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Or build all iOS targets:

```bash
./gradlew :composeApp:assemble
```

### 2. Open iOS Simulator

You can start a simulator from Android Studio:

1. In Android Studio, go to **Tools > Device Manager**
2. Click **+ Create Device** or select an existing iOS simulator
3. Click the **Play** button to start the simulator

Alternatively, start from terminal:

```bash
xcrun simctl boot "iPhone 15 Pro"
open -a Simulator
```

### 3. Build the Framework

Build the iOS framework using Gradle:

```bash
./gradlew :composeApp:iosSimulatorArm64Binaries
```

Or use the Gradle panel in Android Studio:
1. Open the **Gradle** tool window (right sidebar)
2. Navigate to **composeApp > Tasks > ios**
3. Double-click **iosSimulatorArm64Binaries**

### 4. Run from Android Studio

**Option A: Using Gradle Task (Recommended)**

1. In the Gradle panel, find **composeApp > Tasks > ios**
2. Double-click **runIosSimulator**
3. This will build the framework and prepare the Xcode project

**Option B: Using Run Configuration**

Android Studio should automatically detect iOS run configurations. If not:

1. Click the run configuration dropdown (top toolbar)
2. Look for **iosApp** or **iOS Application** configurations
3. If missing, Android Studio will create them automatically after building the framework
4. Select the iOS configuration and click **Run** (▶️)

**Note**: Android Studio uses Xcode's build tools in the background, so Xcode must be installed.

## Alternative: Using Gradle Tasks

You can also run iOS directly via Gradle:

```bash
# Build and run on simulator
./gradlew :composeApp:iosSimulatorArm64Binaries
```

Then in Android Studio:
1. Go to **Run > Edit Configurations**
2. Create an **iOS Application** configuration
3. Select the built framework

## Troubleshooting

### iOS Simulator Not Detected

1. Make sure Xcode is installed: `xcode-select --print-path`
2. Install simulators: Open Xcode > Settings > Platforms
3. List available simulators: `xcrun simctl list devices`

### Framework Not Found

1. Build the framework first:
   ```bash
   ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
   ```

2. Clean and rebuild:
   ```bash
   ./gradlew clean
   ./gradlew :composeApp:assemble
   ```

### Android Studio Doesn't Show iOS Option

1. Make sure you're on macOS
2. Ensure Xcode is installed
3. Restart Android Studio
4. Invalidate caches: **File > Invalidate Caches / Restart**

### Build Errors

If you get Kotlin/Native compilation errors:
1. Check that all dependencies are CMP-compatible
2. Make sure `kotlinx.datetime` is properly configured
3. Try: `./gradlew clean :composeApp:assemble`

## Quick Commands

```bash
# List iOS simulators
xcrun simctl list devices

# Boot a specific simulator
xcrun simctl boot "iPhone 15 Pro"

# Build iOS framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Build all targets
./gradlew :composeApp:assemble
```

## Notes

- Android Studio uses Xcode's build tools in the background
- The first build may take longer as it compiles the Kotlin/Native framework
- You can still use Xcode for debugging, but Android Studio works for development

