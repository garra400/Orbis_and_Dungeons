# UI System Documentation

## Overview

The Orbis and Dungeons mod features a modernized, paginated UI system for race and class selection. The UI is built using Hytale's native UI framework with custom styling and interactive components.

## UI Files Structure

```
src/main/resources/Common/UI/
├── Common.ui                           # Reusable component definitions
└── Custom/
    ├── Pages/
    │   ├── race_selection.ui           # Race selection screen
    │   └── class_selection.ui          # Class selection screen
    └── Templates/
        └── selection_cards.ui          # Card templates (optional)
```

## Component System

### Common.ui Components

The `Common.ui` file defines reusable components that can be used across all UI pages:

#### @PageOverlay
- **Purpose**: Full-screen overlay for modal UI pages
- **Layout**: Overlay mode with full coverage
- **Usage**: Wraps entire page content

#### @DecoratedContainer
- **Purpose**: Styled container with borders and background
- **Features**:
  - 2px border with rounded corners (CornerRadius: 8)
  - Semi-transparent dark background (#1a1a1a at 95% opacity)
  - Consistent padding and margins

#### @Title
- **Purpose**: Page title text
- **Style**:
  - Centered alignment
  - Font size: 24px, bold
  - Color: #ffffff (white)

#### @Subtitle
- **Purpose**: Descriptive subtitle text
- **Style**:
  - Centered alignment
  - Font size: 14px
  - Color: #c0c0c0 (light gray)

#### @BackButton
- **Purpose**: Navigation back button
- **Features**:
  - Hover and pressed states
  - Consistent sizing (120x40px)
  - Translucent background with state changes

#### @TextButton
- **Purpose**: Primary action buttons
- **Style**:
  - Green background (#2d7b4c)
  - Hover/pressed state animations
  - Bold text, white color

#### @DefaultScrollbarStyle
- **Purpose**: Consistent scrollbar appearance
- **Properties**:
  - Width: 8px
  - Track color: #2a2a2a
  - Thumb color: #555555

#### @SelectionCard
- **Purpose**: Interactive selection cards
- **States**:
  - Normal: Dark background
  - Hovered: Lighter background
  - Pressed: Even lighter
  - Selected: Blue-tinted with thicker border

#### @PageButton
- **Purpose**: Page navigation buttons
- **Features**:
  - Previous/Next navigation
  - Disabled state support
  - Hover effects

## UI Pages

### Race Selection (race_selection.ui)

**Dimensions**: 950x650px

**Structure**:
```
╔═══════════════════════════════════════════╗
║            Header (Title/Subtitle)        ║
╠═══════════════╦═══════════════════════════╣
║  Race List    ║  Race Details Panel       ║
║  (Scrollable) ║  - Icon                   ║
║               ║  - Name                   ║
║  [ ] Human    ║  - Tagline                ║
║  [✓] Elf      ║  - Strengths              ║
║  [ ] Orc      ║  - Weaknesses             ║
║               ║                           ║
╠═══════════════╩═══════════════════════════╣
║  Footer (Navigation + Confirm Button)     ║
╚═══════════════════════════════════════════╝
```

**Key Features**:
- Dynamic race loading from RaceRegistry
- Pagination support (4 races per page)
- Real-time preview updates
- Translatable labels via TranslationManager
- Selection indicator (checkmark)

**UI Elements**:
- `#Container` - Main decorated container
- `#Header` - Title section
- `#RaceListPanel` - Scrollable race list
- `#RaceDetailPanel` - Selected race details
- `#Footer` - Navigation and confirm buttons

### Class Selection (class_selection.ui)

**Dimensions**: 950x650px

**Structure**: Similar to race_selection.ui

**Key Features**:
- Dynamic class loading from ClassConfigLoader
- Pagination support (4 classes per page)
- Back button to return to race selection
- Preview of class bonuses and penalties
- Combined race + class stats display

**Navigation Flow**:
```
Start → Race Selection → Class Selection → Confirm → Apply Stats
         ↑                    ↓
         └────── Back ─────────┘
```

## Color Palette

### Backgrounds
```css
#1a1a1a(0.95)  /* Primary dark background */
#0f0f0f(0.9)   /* Deeper dark for panels */
#0a0a0a(0.5)   /* Header/Footer background */
```

### Borders
```css
#404040(0.4)   /* Normal state */
#606060(0.7)   /* Hover state */
#4d8ac0(1.0)   /* Selected state (blue) */
```

### Text Colors
```css
#ffffff        /* Primary text */
#c0c0c0        /* Secondary text */
#999999        /* Tertiary/description text */
#d4af37        /* Gold (titles) */
```

### Status Colors
```css
#66ff66        /* Success/Positive (green) */
#ff6666        /* Error/Negative (red) */
#ffcc66        /* Warning (yellow) */
#66aaff        /* Info (blue) */
```

## Java Integration

### Opening UI Pages

```java
// Open race selection
UIManager.openPage(player, "race_selection");

// Open class selection
UIManager.openPage(player, "class_selection");
```

### Event Handling

The UI uses Hytale's event system with custom event data codecs:

**RaceEventData**:
```java
public class RaceEventData {
    public String raceId;
    public int page;

    public static final Codec<RaceEventData> CODEC =
        BuilderCodec.of(...)
            .add("Race", Codec.STRING, data -> data.raceId)
            .add("Page", Codec.INT, data -> data.page)
            .build();
}
```

**Event Types**:
- `"select"` - User selects a race/class
- `"prevpage"` - Navigate to previous page
- `"nextpage"` - Navigate to next page
- `"confirm"` - Confirm selection
- `"back"` - Go back to previous screen

### Updating UI Elements

```java
// Update text
cmd.set("#Title.Text", TranslationManager.translate("ui.title"));

// Update label
cmd.set("#RaceName.Text", race.getDisplayName());

// Show/hide elements
cmd.set("#Element.Visible", "true");
```

### Dynamic Content Generation

```java
// Add race buttons dynamically
for (Race race : getCurrentPageRaces()) {
    cmd.appendInline("#RaceButtons", createRaceButton(race));
    evt.addEventBinding(
        CustomUIEventBindingType.Activating,
        "#RaceButton_" + race.getId(),
        new RaceEventData(race.getId(), currentPage)
    );
}
```

## Translation Integration

All UI text is translatable via the TranslationManager system:

**Translation Keys**:
```
ui.race_selection.title          → "Select Your Race"
ui.race_selection.subtitle       → "Choose your race..."
ui.race_selection.strengths      → "STRENGTHS"
ui.race_selection.weaknesses     → "WEAKNESSES"
ui.race_selection.confirm        → "Confirm Selection"

ui.class_selection.title         → "Select Your Class"
ui.class_selection.back          → "← Back to Races"

race.<raceId>.name               → Race display name
race.<raceId>.tagline            → Race description
race.<raceId>.strength.N         → Strength line N
race.<raceId>.weakness.N         → Weakness line N

class.<classId>.name             → Class display name
class.<classId>.tagline          → Class description
```

## Pagination System

The UI supports unlimited pages with automatic navigation:

**Configuration**:
- Items per page: 4
- Dynamic page calculation: `totalPages = ceil(itemCount / 4)`
- Zero-indexed page numbers internally

**Navigation Logic**:
```java
int currentPage = 0;
int totalPages = (int) Math.ceil(races.size() / 4.0);

// Previous button
if (currentPage > 0) {
    currentPage--;
    refreshUI();
}

// Next button
if (currentPage < totalPages - 1) {
    currentPage++;
    refreshUI();
}
```

## Best Practices

### Adding New UI Elements

1. **Define ID**: Always assign unique IDs to elements
2. **Use Components**: Leverage Common.ui components
3. **Maintain Consistency**: Follow existing color/style patterns
4. **Test States**: Verify hover/pressed/selected states
5. **Translate Text**: Use TranslationManager for all text

### Performance Optimization

- Load UI files once and cache
- Use pagination to limit DOM elements
- Avoid deep nesting (max 3-4 levels)
- Minimize inline styles (use components)
- Batch UI updates when possible

### Debugging UI Issues

**Common Problems**:
1. **Element not found**: Check ID spelling and hierarchy
2. **Text not translating**: Verify translation key exists
3. **Events not firing**: Confirm event binding syntax
4. **Styling issues**: Check selector specificity

**Debug Commands**:
```java
// Log UI structure
UIManager.debugPrintHierarchy(page);

// Verify element exists
if (page.findElement("#MyElement") != null) {
    // Element found
}
```

## Accessibility Considerations

- **High Contrast**: Dark backgrounds with light text
- **Clear Hierarchy**: Distinct visual levels
- **Interactive Feedback**: Hover/pressed states
- **Readable Font Sizes**: 11px minimum, 24px for titles
- **Color Blindness**: Don't rely solely on color (use icons/text)

## Future Enhancements

Potential improvements for future versions:

- [ ] Animation system for transitions
- [ ] Tooltips for detailed information
- [ ] Search/filter functionality
- [ ] Keyboard navigation support
- [ ] Preview animations for abilities
- [ ] Comparison mode (side-by-side)
- [ ] Favorites/bookmarks system
- [ ] Sound effects for interactions

---

**Last Updated**: 2026-02-09
**Version**: 2.0 (UI Modernization Update)
