# Pomodoro Design System

The centralized design system for the Pomodoro application, providing consistent theming, components, and design tokens following strict Clean Architecture principles.

## Overview

This module encapsulates all UI components and theme definitions, ensuring that features never use Material3 directly. All styling, colors, typography, spacing, and shapes are centralized here for maintainability and consistency.

## Architecture

The design system is organized into two main categories:

- **Theme Foundation**: Colors, typography, spacing, shapes, and the main theme composable
- **Components**: Reusable UI components that follow the Pomodoro design language

## Theme Foundation

### Colors

The color system is based on a dark theme with a blue-gray palette:

```kotlin
// Background colors
BackgroundDark = #1C2834
SurfaceDark = #263847
SurfaceVariantDark = #2C3E50

// Primary colors
PrimaryBlue = #2196F3
PrimaryBlueDark = #1976D2

// Accent colors
AccentOrange = #FF6B35

// Text colors
TextPrimary = #FFFFFF
TextSecondary = #8B97A5
TextTertiary = #5E6A78
```

**Usage:**

```kotlin
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.primary // Uses PrimaryBlue
)
```

### Typography

Complete typography scale from display (96sp) to label small (11sp):

```kotlin
// Display Large - Timer displays
MaterialTheme.typography.displayLarge // 96sp, Bold

// Headlines
MaterialTheme.typography.headlineLarge // 32sp, Bold (brand name)
MaterialTheme.typography.headlineMedium // 24sp, SemiBold (screen titles)

// Titles
MaterialTheme.typography.titleLarge // 20sp, SemiBold (section headers)
MaterialTheme.typography.titleMedium // 16sp, SemiBold

// Body
MaterialTheme.typography.bodyLarge // 16sp, Regular
MaterialTheme.typography.bodyMedium // 14sp, Regular

// Labels
MaterialTheme.typography.labelLarge // 14sp, Medium (uppercase)
MaterialTheme.typography.labelMedium // 12sp, Medium (uppercase)
MaterialTheme.typography.labelSmall // 11sp, Regular
```

### Spacing

Consistent spacing values via `LocalSpacing`:

```kotlin
val spacing = LocalSpacing.current

Column(modifier = Modifier.padding(spacing.spaceM)) {
    // spaceXXS = 4dp
    // spaceXS = 8dp
    // spaceS = 12dp
    // spaceM = 16dp (standard)
    // spaceL = 20dp
    // spaceXL = 24dp
    // spaceXXL = 32dp
    // spaceXXXL = 48dp
}
```

### Shapes

Corner radius definitions:

```kotlin
MaterialTheme.shapes.extraSmall // 4dp
MaterialTheme.shapes.small // 8dp
MaterialTheme.shapes.medium // 12dp (standard buttons/cards)
MaterialTheme.shapes.large // 16dp (large cards)
MaterialTheme.shapes.extraLarge // 20dp
```

## Components

### Button Components

#### PomodoroButton

Primary button with three variants: Primary, Secondary, and Text.

```kotlin
// Primary button (blue background)
PomodoroButton(
    text = "START",
    onClick = { /* action */ },
    variant = ButtonVariant.Primary
)

// With icon
PomodoroButton(
    text = "START",
    onClick = { /* action */ },
    variant = ButtonVariant.Primary,
    icon = Icons.Default.PlayArrow
)

// Secondary button (dark surface)
PomodoroButton(
    text = "Reset",
    onClick = { /* action */ },
    variant = ButtonVariant.Secondary
)

// Text button
PomodoroButton(
    text = "Skip",
    onClick = { /* action */ },
    variant = ButtonVariant.Text
)
```

#### PomodoroIconButton

Icon-only button in two sizes.

```kotlin
// Standard size (48dp)
PomodoroIconButton(
    icon = PomodoroIcons.Settings,
    onClick = { /* action */ },
    size = IconButtonSize.Standard
)

// Compact size (40dp)
PomodoroIconButton(
    icon = PomodoroIcons.Settings,
    onClick = { /* action */ },
    size = IconButtonSize.Compact
)

// Transparent background
PomodoroIconButton(
    icon = PomodoroIcons.Close,
    onClick = { /* action */ },
    backgroundColor = null
)
```

### Card Component

#### PomodoroCard

Card surface with consistent styling.

```kotlin
PomodoroCard {
    Text(
        text = "Work Duration",
        style = MaterialTheme.typography.titleLarge
    )
    Text(
        text = "25 minutes",
        style = MaterialTheme.typography.bodyMedium
    )
}
```

### Input Components

#### PomodoroChip

Duration selector chip with selected/unselected states.

```kotlin
PomodoroChip(
    text = "25",
    selected = true,
    onClick = { /* select 25 */ }
)
```

#### PomodoroSwitch

Toggle switch with custom styling.

```kotlin
PomodoroSwitch(
    checked = soundEnabled,
    onCheckedChange = { soundEnabled = it }
)
```

#### PomodoroSlider

Slider for value selection.

```kotlin
PomodoroSlider(
    value = volume,
    onValueChange = { volume = it },
    valueRange = 0f..1f
)
```

#### PomodoroTextField

Text input field.

```kotlin
PomodoroTextField(
    value = name,
    onValueChange = { name = it },
    label = "Name",
    placeholder = "Enter your name"
)
```

#### PomodoroDropdown

Dropdown menu.

```kotlin
PomodoroDropdown(
    selectedText = "Digital Beep (Default)",
    options = listOf("Digital Beep", "Classic Bell", "Chime"),
    onOptionSelected = { selected = it }
)
```

### Progress Components

#### PomodoroProgressBar

Linear progress indicator.

```kotlin
PomodoroProgressBar(progress = 0.75f)
```

#### PomodoroProgressIndicator

Circular progress indicator.

```kotlin
// Determinate
PomodoroProgressIndicator(progress = 0.75f)

// Indeterminate
PomodoroProgressIndicator()
```

### Navigation Components

#### PomodoroTopBar

Top app bar with title and optional actions.

```kotlin
PomodoroTopBar(
    title = "Focus",
    navigationIcon = PomodoroIcons.Settings,
    onNavigationClick = { /* navigate to settings */ },
    actions = {
        IconButton(onClick = { /* more actions */ }) {
            Icon(PomodoroIcons.More, contentDescription = "More")
        }
    }
)
```

#### PomodoroBottomNavigation

Bottom navigation bar.

```kotlin
PomodoroBottomNavigation(
    items = listOf(
        PomodoroNavigationItem(
            label = "TIMER",
            icon = PomodoroIcons.Timer,
            selected = true,
            onClick = { /* navigate to timer */ }
        ),
        PomodoroNavigationItem(
            label = "STATS",
            icon = PomodoroIcons.Stats,
            selected = false,
            onClick = { /* navigate to stats */ }
        ),
        PomodoroNavigationItem(
            label = "HISTORY",
            icon = PomodoroIcons.History,
            selected = false,
            onClick = { /* navigate to history */ }
        )
    )
)
```

### Surface Components

#### PomodoroDivider

Horizontal divider.

```kotlin
PomodoroDivider()
```

#### PomodoroModal

Bottom sheet modal.

```kotlin
PomodoroModal {
    Text(
        text = "Settings",
        style = MaterialTheme.typography.headlineMedium
    )
    // Modal content
}
```

## Icon System

Centralized icon constants via `PomodoroIcons`:

```kotlin
// Navigation
PomodoroIcons.Settings
PomodoroIcons.Back
PomodoroIcons.Close
PomodoroIcons.Help
PomodoroIcons.More

// Timer
PomodoroIcons.Timer
PomodoroIcons.Play
PomodoroIcons.Pause
PomodoroIcons.Reset

// Stats & History
PomodoroIcons.Stats
PomodoroIcons.History
PomodoroIcons.Check
PomodoroIcons.Flame

// Settings
PomodoroIcons.Sound
PomodoroIcons.Vibration

// Actions
PomodoroIcons.Share
PomodoroIcons.ChevronDown
```

## Usage in Features

### Setting up PomodoroTheme

Wrap your app content with `PomodoroTheme`:

```kotlin
setContent {
    PomodoroTheme {
        // App content
    }
}
```

### Accessing Theme Values

```kotlin
@Composable
fun MyScreen() {
    val spacing = LocalSpacing.current
    
    Column(
        modifier = Modifier.padding(spacing.spaceM)
    ) {
        Text(
            text = "Title",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        PomodoroButton(
            text = "Action",
            onClick = { /* action */ }
        )
    }
}
```

## Testing

All components have snapshot tests using Roborazzi. Tests are located in:

```
core/design-system/src/test/kotlin/com/jujodevs/pomodoro/core/designsystem/snapshot/
```

Run tests with:

```bash
./gradlew :core:design-system:testDebugUnitTest
```

Generate/verify snapshots:

```bash
./gradlew :core:design-system:recordRoborazziDebug
./gradlew :core:design-system:compareRoborazziDebug
```

## Design Decisions

1. **Dark Theme Only**: The app currently uses dark theme exclusively
2. **Material3 Foundation**: Components use Material3 internally but never expose it
3. **No Dynamic Color**: Fixed color palette, no Material You dynamic colors
4. **System Font**: Uses default system font (can be replaced later)
5. **Accessibility**: All components follow Material3 accessibility guidelines

## Rules

- ❌ **Never** use Material3 components directly in features
- ✅ **Always** use Pomodoro components from design-system
- ✅ Access colors via `MaterialTheme.colorScheme`
- ✅ Access typography via `MaterialTheme.typography`
- ✅ Access spacing via `LocalSpacing.current`
- ✅ Access shapes via `MaterialTheme.shapes`
- ✅ Use `PomodoroIcons` for all icons

## Contributing

When adding new components:

1. Create component in appropriate package under `components/`
2. Use theme tokens (colors, spacing, typography, shapes)
3. Add `@Preview` annotations
4. Create Roborazzi snapshot tests
5. Document usage in this README
6. Ensure accessibility compliance

## Dependencies

This module depends on:

- Compose BOM (2026.01.01)
- Material3 (internal use only)
- Compose UI Tooling
