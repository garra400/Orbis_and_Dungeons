# Orbis and Dungeons - Changelog

---

## Version 2026.2.9 - UI Modernization & Dynamic Configuration System

### 🎨 UI System Modernization

#### Complete UI Redesign
- **New Modern Aesthetic**: Completely redesigned race and class selection interfaces
  - Gradient backgrounds with transparency effects
  - Rounded corners (CornerRadius: 6-8px)
  - Hover/pressed state animations on all interactive elements
  - Consistent color palette (#1a1a1a backgrounds, #d4af37 gold accents)

- **Enhanced Visual Hierarchy**:
  - Larger, bolder titles (24-28px)
  - Clear section separation with borders
  - Icon-based visual feedback (emoji support)
  - Status badges for selection indicators

- **Improved Component System** (`Common.ui`):
  - `@PageOverlay` - Full-screen modal overlay
  - `@DecoratedContainer` - Styled containers with borders
  - `@Title` / `@Subtitle` - Standardized text styles
  - `@BackButton` / `@TextButton` - Reusable interactive buttons
  - `@DefaultScrollbarStyle` - Consistent scrollbar appearance
  - `@SelectionCard` - Interactive selection cards with states
  - `@PageButton` - Navigation buttons with disabled state support

### 🐛 Critical Bug Fixes

#### Fixed: Hardcoded Translation Keys Issue
**Problem**: Users reported seeing "race.finstermensch.strength.3" even when configuring only 2 strengths

**Root Cause**: `RaceSelectionPage.java` was hardcoded to load exactly 3 strengths and 2 weaknesses from translation keys, ignoring the actual `RaceConfig` data:

```java
// OLD CODE (BROKEN)
String strength1 = TranslationManager.translate("race." + raceKey + ".strength.1");
String strength2 = TranslationManager.translate("race." + raceKey + ".strength.2");
String strength3 = TranslationManager.translate("race." + raceKey + ".strength.3");
```

**Solution**: Now dynamically reads from `RaceConfig` like `ClassSelectionPage` does:

```java
// NEW CODE (FIXED)
RaceConfig config = RaceConfigLoader.getConfig(raceKey);
List<String> strengths = config.strengths != null ? config.strengths : List.of();
for (int i = 0; i < 3; i++) {
    String text = i < strengths.size() ? "- " + strengths.get(i) : "";
    cmd.set("#PositiveLine" + (i + 1) + ".Text", text);
}
```

**Impact**:
- ✅ Custom races with 1-3 strengths now work correctly
- ✅ Empty strength slots don't show translation keys
- ✅ Race descriptions now read directly from `races_config.json`
- ✅ Consistent behavior between races and classes

#### Fixed: Race Descriptions Not Updating from Config
**Problem**: Users reported that editing race descriptions in `races_config.json` worked for classes but not races

**Root Cause**: Same as above - race UI was loading from translation keys instead of config

**Solution**: Race UI now reads descriptions from config file like class UI does

**Testing Checklist**:
1. Edit `races_config.json` - change race strengths/weaknesses
2. Run `/racereload` command
3. Open race selection UI - changes should appear immediately
4. Verify empty slots don't show translation keys

### 🌐 Translation System Enhancements

#### Added: Spanish (es.json) Translation
Complete Spanish translation added with 127 translation keys:

**New Language**:
- **Code**: `es`
- **Name**: Español (Spanish)
- **Status**: ✅ Complete
- **Coverage**: All commands, UI elements, races, and classes

**How to Use**:
```
/racesetlanguage --confirm --language=es
```

**Updated Language List**:
| Code | Language | Status |
|------|----------|--------|
| `en` | English | ✅ Complete |
| `pt_br` | Português (Brasil) | ✅ Complete |
| `ru` | Русский (Russian) | ✅ Complete |
| `es` | Español (Spanish) | ✅ NEW |

#### Improved: Translation File Location Documentation
**Clarification**: Translation files must be placed in the correct location:

**Correct Path**:
```
UserData/Saves/[WorldName]/mods/OrbisAndDungeons_RaceSelection/languages/
```

**NOT** in `UserData/Mods/` (common mistake)

**Why**: Hytale's mod system uses per-world data directories. Each world save has its own mod configuration folder to allow different settings per world.

**Adding Custom Translations**:
1. Navigate to your world save folder
2. Go to `mods/OrbisAndDungeons_RaceSelection/languages/`
3. Add your custom `.json` file (e.g., `fr.json` for French)
4. Restart server or use `/racereload`
5. Select with `/racesetlanguage --confirm --language=fr`

### 📚 New Documentation

#### docs/UI_SYSTEM.md
Complete UI system documentation including:
- Component reference (all `@` components)
- Color palette guide
- Java integration examples
- Event handling patterns
- Translation integration
- Best practices for UI development
- Accessibility considerations

**Topics Covered**:
- UI file structure and organization
- Reusable component definitions
- Color palette and theming
- Java-UI integration patterns
- Event data serialization
- Dynamic content generation
- Translation key naming conventions
- Pagination system implementation

### 🔧 Technical Improvements

#### Modular Configuration System
The UI now fully respects the modular config system:

**Race Configuration** (`races_config.json`):
```json
{
  "id": "custom_race",
  "displayName": "Custom Race",
  "tagline": "Your description here",
  "strengths": [
    "First strength",
    "Second strength"
  ],
  "weaknesses": [
    "First weakness"
  ]
}
```

**Benefits**:
- Add races with any number of strengths (0-3 shown in UI)
- Add races with any number of weaknesses (0-2 shown in UI)
- UI automatically adapts to config data
- No code changes required for new races
- No hardcoded translation keys

#### Enhanced RaceSelectionPage.java
- Added dynamic config loading
- Removed hardcoded translation assumptions
- Added proper null checking
- Improved code documentation
- Fixed inconsistency with ClassSelectionPage

### 🎯 UI Features Summary

**Race Selection Screen (950x650px)**:
- Dynamic race list from `RaceRegistry`
- Pagination support (4 races per page)
- Real-time preview panel
- Strengths/weaknesses from config
- Selection indicator (checkmark)
- Translatable labels
- Hover effects on all buttons

**Class Selection Screen (950x650px)**:
- Dynamic class list from `ClassConfigLoader`
- Pagination support (4 classes per page)
- Back button to race selection
- Combined race + class preview
- Confirm button to apply
- Consistent styling with race screen

**Color Palette**:
- Background: `#1a1a1a`, `#0f0f0f`, `#0a0a0a`
- Borders: `#404040` → `#4d8ac0` (selected)
- Text: `#ffffff` (primary), `#c0c0c0` (secondary)
- Accents: `#d4af37` (gold), `#ff8c00` (orange)
- Status: `#66ff66` (success), `#ff6666` (error)

### 📝 Files Modified

**UI Files**:
- `src/main/resources/Common/UI/Common.ui` - Added 7 reusable components
- `src/main/resources/Common/UI/Custom/Pages/race_selection.ui` - Complete redesign
- `src/main/resources/Common/UI/Custom/Pages/class_selection.ui` - Complete redesign

**Java Files**:
- `src/main/java/com/garra400/racas/ui/RaceSelectionPage.java` - Fixed dynamic loading
- `src/main/java/com/garra400/racas/i18n/TranslationManager.java` - Added ES support

**Translation Files**:
- `src/main/resources/languages/es.json` - NEW Spanish translation (127 keys)

**Documentation**:
- `docs/UI_SYSTEM.md` - NEW comprehensive UI documentation

### 🚀 For Server Admins

**No Breaking Changes**:
- Existing race/class selections preserved
- Config files remain compatible
- Translation keys backward compatible
- No database migrations needed

**Update Steps**:
1. Replace JAR file with new version
2. Restart server
3. Spanish translation available automatically
4. Custom races will now display correctly
5. No config changes needed

**What Players Will Notice**:
- ✅ Modern, polished UI design
- ✅ Custom races display properly
- ✅ Spanish language option available
- ✅ Smoother animations and transitions
- ✅ Better visual feedback on interactions

### 🐛 Known Issues Resolved

1. ✅ "race.finstermensch.strength.3" translation key shown
2. ✅ Custom race descriptions not appearing in UI
3. ✅ Race config changes not reflected in UI
4. ✅ Inconsistency between race and class UI behavior

### 📖 Migration Guide

**For Mod Developers**:
If you were adding custom races, you may have worked around the hardcoded translation issue. This workaround is no longer needed:

**Old Workaround** (no longer needed):
```json
// You may have added these dummy translations
"race.myrace.strength.1": "...",
"race.myrace.strength.2": "...",
"race.myrace.strength.3": ""  // Had to add even if unused
```

**New System** (proper way):
```json
// In races_config.json
{
  "id": "myrace",
  "strengths": [
    "First strength",
    "Second strength"
    // No need to add empty third entry
  ]
}
```

The UI will now read directly from config and won't require translation keys for unused slots.

---

## Version 2026.1.31 (Build 48910) - UI Fixes, Translation Updates & Balance Changes

### 🏆 Honorable Mention

**Special thanks to m1rrh** for the following contributions:
- **ColorConverter utility** - Color conversion system for UI elements
- **Translation System suggestion** - Inspired the multi-language support implementation

### ⚖️ Balance Changes

#### Orc - Major Buff (Tank Role)
| Stat | Before | After | Change |
|------|--------|-------|--------|
| Health | +75 HP (175 total) | +100 HP (200 total) | **+25 HP** |
| Stamina | 0 (10 total)  | **-2 (8 total)** | -2 (8 total) |
| Role | Melee DPS | **Pure Tank** | Role shift |

**Design Philosophy:** Orc is now the ultimate tank race with the highest HP pool in the game. Trades stamina for raw survivability.

#### Dwarf - Resistance Update  
| Stat | Before | After | Change |
|------|--------|-------|--------|
| Health | +50 HP (150 total) | +50 HP (150 total) | No change |
| Physical Resistance | 30% | **20%** | -10% |
| Fall Resistance | 50% | 50% | No change |

**Design Philosophy:** Dwarf remains a defensive specialist with damage resistances, but slightly toned down physical resistance for better balance.

#### UI Description Overhaul
All race and class descriptions have been simplified to show **only concrete stats**:
- Removed flavor text and subjective descriptions
- Now shows exact numbers: `+100 Health (200 total)`, `-2 Stamina (8 total)`
- Clearer understanding of what each race/class actually provides

### 🐛 Bug Fixes

#### Custom UI Element Selector Fix
- **Problem:** Server failed to connect with error `"Selected element in CustomUI command was not found. Selector: #StrengthsHeader.Text"`
- **Root Cause:** Java code was referencing UI element IDs that didn't exist in the `.ui` files
- **Solution:** Added missing IDs to UI elements and updated Java selectors

**Fixed Selectors:**
- `#StrengthsHeader` - Added ID to "STRENGTHS" label
- `#WeaknessesHeader` - Added ID to "WEAKNESSES" label  
- `#PrevPageButtonLabel` - Added ID to Previous button's label
- `#NextPageButtonLabel` - Added ID to Next button's label
- `#ConfirmSelectionLabel` - Added ID to Confirm button's label
- `#BackToRaceLabel` - Added ID to Back button's label (class selection only)

**Files Modified:**
- `resources/Common/UI/Custom/Pages/race_selection.ui`
- `resources/Common/UI/Custom/Pages/class_selection.ui`
- `ui/RaceSelectionPage.java`
- `ui/ClassSelectionPage.java`

### 🌐 Multi-Language Translation System

#### Complete Translation System Implementation
A comprehensive internationalization (i18n) system has been implemented, providing full multi-language support for the entire mod:

**Supported Languages:**
| Code | Language | Status |
|------|----------|--------|
| `en` | English | ✅ Complete |
| `pt_br` | Português (Brasil) | ✅ Complete |
| `ru` | Русский (Russian) | ✅ Complete |

#### System Architecture

**TranslationManager Class:**
- Centralized translation service (`i18n/TranslationManager.java`)
- Dynamic language file loading from `mods/languages/` folder
- Auto-extraction of default language files from JAR resources
- Runtime language switching with `/racesetlanguage` command
- Persistent language preference per player

**How It Works:**
1. On startup, extracts default language files to `mods/languages/` folder
2. Loads all `.json` translation files automatically
3. Provides `translate(key)` method for all translatable strings
4. Falls back to English if a key is missing in the selected language

#### Translated Content

**All Commands Fully Translated:**
- `/raceinfo` - Race and class information display
- `/racetrade` - Race trading messages
- `/racereset` - Race reset confirmations
- `/racereload` - Configuration reload status
- `/tradeclass` - Class change messages
- `/resetclass` - Class reset confirmations
- `/raceselect` - UI opening messages
- `/racesetlanguage` - Language selection

**All UI Elements Translated:**
- Race selection screen title, subtitle, buttons
- Class selection screen title, subtitle, buttons
- Navigation buttons (Previous, Next, Confirm, Back)
- Strengths/Weaknesses headers

**All Races Translated:**
| Race | English | Português | Русский |
|------|---------|-----------|---------|
| Human | Human | Humano | Человек |
| Elf | Elf | Elfo | Эльф |
| Orc | Orc | Orc | Орк |
| Dwarf | Dwarf | Anão | Дварф |
| Tiefling | Tiefling | Tiefling | Тифлинг |

**All Classes Translated:**
| Class | English | Português | Русский |
|-------|---------|-----------|---------|
| None | None | Nenhuma | Нет |
| Berserker | Berserker | Berserker | Берсерк |
| Swordsman | Swordsman | Espadachim | Мечник |
| Crusader | Crusader | Cruzado | Крестоносец |
| Assassin | Assassin | Assassino | Убийца |
| Archer | Archer | Arqueiro | Лучник |
| Mage | Mage | Mago | Маг |

#### Translation File Structure

Each language file follows this JSON structure:
```json
{
    "language.name": "English",
    "language.code": "en",
    
    // Command translations
    "command.raceinfo.title": "&6=== Race Info for &f%s &6===",
    "command.raceinfo.race": "&6Race: &f%s",
    
    // UI translations
    "ui.race_selection.title": "Select Your Race",
    "ui.race_selection.strengths": "STRENGTHS",
    
    // Race translations
    "race.human.name": "Human",
    "race.human.tagline": "Balanced and adaptable...",
    "race.human.strength.1": "+35 Health (135 total)",
    "race.human.weakness.1": "No special abilities",
    
    // Class translations
    "class.berserker.name": "Berserker",
    "class.berserker.tagline": "Rage-fueled destruction."
}
```

#### New Command: `/racesetlanguage`
- **Usage:** `/racesetlanguage --confirm --language=<code>`
- **Function:** Change the mod's display language
- **Available:** `en`, `pt_br`, `ru`
- **Example:** `/racesetlanguage --confirm --language=pt_br`

#### Adding Custom Languages
Server admins can add new languages:
1. Create a new file in `mods/languages/` (e.g., `es.json`)
2. Copy the structure from `en.json`
3. Translate all keys to the new language
4. Restart server or use `/racereload`
5. Players can select with `/racesetlanguage`

### 📝 Technical Details

**Files Created:**
- `i18n/TranslationManager.java` - Translation engine
- `i18n/T.java` - Shorthand translation helper
- `commands/SetLanguageCommand.java` - Language selection command
- `resources/languages/en.json` - English translations (127 keys)
- `resources/languages/pt_br.json` - Portuguese translations (127 keys)
- `resources/languages/ru.json` - Russian translations (127 keys)

**Files Modified:**
- All command classes - Now use `TranslationManager.translate()`
- `RaceSelectionPage.java` - UI text now translated
- `ClassSelectionPage.java` - UI text now translated
- `RaceMod.java` - Initializes TranslationManager on startup
- `storage/loader/RaceConfigLoader.java` - Orc and Dwarf stat changes

**Build Information:**
- Build Number: 54844
- Date: January 31, 2026

### ⚠️ Note for Server Admins
If you have existing language files in `mods/languages/`, you may need to **delete them** to receive the updated translations, or manually add the new translation keys.

---

## Version 2026.1.27 (Build 48905) - Mana System, New Race & Class, JoinScreen Fix

### 🎉 What's New

#### Separate Mana System Implementation
- **Mana is now a separate stat** from Stamina (previously conflated)
- Uses Hytale's native `DefaultEntityStatTypes.MANA` stat
- Classes can now modify Health, Stamina, AND Mana independently
- Base mana value: 100

#### New Race: Dwarf
- **Stats:** +50 Health (150 total), +0 Stamina (10 base)
- **Resistances:** 30% Physical damage resistance, 50% Fall damage resistance
- **Theme:** Extreme tank with unbreakable resilience
- **Strengths:** Stone Skin (Physical resistance), Sure-footed (Fall resistance), Extreme survivability
- **Weaknesses:** Very low mobility (10 stamina), Slow movement, Short stature
- **Design:** Pure tank race with highest HP and strong defensive resistances, trades all mobility for survivability

#### New Class: Mage
- **Stats:** -40 Health (60 HP), +12 Stamina (22 total), +100 Mana (100 total)
- **EHP:** 120 (calculated: 60 + 12×5)
- **Damage Bonus:** +25% with staff/wand/scepter
- **Theme:** Glass cannon spellcaster with high magical energy
- **Strengths:** Extended mana pool, High stamina, Spell casting specialist
- **Weaknesses:** Extremely fragile (60 HP), Vulnerable in melee, Mana dependent

#### JoinScreen Mod Compatibility Fix
- **Problem:** Race selection UI doesn't appear with JoinScreen mod installed
- **Solution:** New `/raceselect` command to manually open race/class selection UI
- **Benefits:** 
  - Resolves event interception conflict
  - Gives players control over when to select race/class
  - No permissions required (available to all)

### 🔧 Changes & Improvements

#### Class System Refactoring
- Added `manaModifier` field to `ClassConfig`
- Updated all 7 classes with mana parameter:
  - None, Berserker, Swordsman, Crusader, Assassin, Archer: 0 mana modifier
  - Mage: +100 mana modifier
- `RaceManager.applyRaceAndClass()` now applies mana bonuses via `applyBonus(stats, "Mana", totalManaBonus)`

#### Updated Balance Reference
- Mage class follows 1 Stamina = 5 Health EHP formula (120 EHP)
- Dwarf race provides strong tank baseline for all classes
- Mage + Dwarf combination: 90 HP, 27 Stamina, 150 Mana (very tanky mage)

### 📝 Technical Details

**Files Modified:**
- `storage/config/ClassConfig.java` - Added manaModifier field
- `storage/loader/ClassConfigLoader.java` - Updated all class configs with mana
- `RaceManager.java` - Added mana stat application logic
- `RaceMod.java` - Registered RaceSelectCommand
- `races/RaceRegistry.java` - Registered DwarfRace

**Files Created:**
- `commands/RaceSelectCommand.java` - Manual UI opener command
- `races/DwarfRace.java` - Dwarf race implementation

**Build Information:**
- Build Number: 54844
- Date: January 27, 2026
- Java Warnings: 18 deprecation warnings (API compatibility maintained)

### 🐛 Bug Fixes
- Fixed mana stat not being modified by class selection
- Resolved JoinScreen mod event conflict with manual command fallback

---

## Version 2026.1.25.2 - Major Update: Commands, Persistence Fix, Resistances & Balance

### 🎉 What's New

#### New Class Management Commands
Two new commands for managing player classes without reopening the UI:

**`/tradeclass <class> [player]`**
- **Function:** Change your class or another player's class instantly
- **Usage:** 
  - Self: `/tradeclass assassin`
  - Others: `/tradeclass berserker Steve`
- **Valid Classes:** NONE, BERSERKER, SWORDSMAN, CRUSADER, ASSASSIN, ARCHER
- **Requirement:** Must have a race selected first
- **Permission:** Available to all players

**`/resetclass [player]`**
- **Function:** Reset your class to NONE (keeping your race)
- **Usage:**
  - Self: `/resetclass`
  - Others: `/resetclass Alex`
- **Effect:** Removes all class bonuses while preserving race bonuses
- **Permission:** Available to all players

### 🔥 New Feature: Damage Resistance System

Inspired by **OrbisOrigins**, races and classes can now have resistances, immunities, and weaknesses to specific damage types!

#### How It Works

**Resistance Values:**
- **0.0** = Immune (100% damage reduction)
- **0.5** = 50% damage reduction
- **1.0** = Normal damage
- **1.5** = 50% extra damage (weakness)

**Combination:**
- Race and class resistances are independent
- **Best (lowest) resistance applies**
- Example: Race 0.5, Class 0.3 → Final 0.3 (80% reduction)

#### Damage Types Available

Fire, Lava, Physical, Magic, Poison, Lightning, Cold, Nature, Fall, Drowning, Void

#### Example Configuration

```json
{
  "id": "tiefling",
  "damageResistances": {
    "Fire": 0.0,    // Immune to fire
    "Lava": 0.0,    // Immune to lava
    "Magic": 1.5    // +50% magic damage
  }
}
```

### 🆕 New Race: Tiefling

**"Demon-touched bloodline, born of fire."**

**Stats:**
- **Health:** -15 (85 HP)
- **Stamina:** +12 (22 total)
- **EHP:** 145 (85 + 60 stamina)

**Resistances:**
- **Fire:** Immune (0.0)
- **Lava:** Immune (0.0)
- **Magic:** +50% damage (1.5)

**Strengths:**
- Can walk through fire and lava unharmed
- High stamina for mobility
- Infernal heritage

**Weaknesses:**
- Fragile physique (-15 HP)
- Extremely vulnerable to magic
- Mistrusted by NPCs

**Playstyle:** High-risk fire tank with mobility focus. Dominates fire-based environments (lava zones, burning buildings) but struggles against magic users.

### ⚖️ Balance Changes

#### Assassin Class Rebalanced (Community Feedback)

**The Problem:**
- Dual daggers have built-in "Hit n' Run" charged attack mobility
- 35% damage boost was amplifying charged attacks excessively  
- -20 HP penalty was too small for weapon safety level
- High stamina allowed constant charged attack spam
- Elf Assassin combo reached 205 EHP (overpowered)

**The Solution:**
- Health: **-20 → -35** (increased penalty to match Archer)
- Damage: **+35% → +22%** (reduced for weapon safety)
- Stamina: **+10** (unchanged - maintains hit-n-run fantasy)
- New EHP: **115** (balanced with other glass cannons)

**Rationale:**
- Daggers are safer than axes (charged attack dash)
- Cannot nerf charged attacks separately (API limitation)
- Applied 1 Stamina = 5 HP balance ratio
- Health penalty now reflects weapon safety level

#### The Golden Ratio: **1 Stamina = 5 Health**

All classes now follow this balance principle:
- **Effective HP (EHP)** = Base HP + Health Bonus + (Stamina Bonus × 5)
- Example: Berserker = 100 - 25 + (8 × 5) = **115 EHP**

#### Updated Class Stats

| Class | HP | Stamina | EHP | Damage | Change |
|-------|-----|---------|-----|--------|--------|
| None | 0 | 0 | 100 | 0% | - |
| Berserker | -25 | +8 | 115 | +30% | ✓ Balanced |
| Swordsman | +10 | +5 | 135 | +20% | ✓ Balanced |
| Crusader | +30 | +0 | 130 | +15% | ✓ Balanced |
| **Assassin** | **-35** | **+10** | **115** | **+22%** | **🔧 FIXED** |
| Archer | -35 | +8 | 105 | +40% | ✓ Balanced |

### 🐛 Critical Bug Fix - Class Persistence

#### Fixed: Class Selection Not Saving to Components

**The Problem:**
- Players could select classes in the UI successfully
- UI showed "Class: Assassin" during selection
- **After closing UI:** `/raceinfo` showed "Class: None"
- Class bonuses (damage, stats) were not being applied
- Only race bonuses worked, class completely ignored

**Root Cause:** 
```java
// ❌ OLD CODE (BROKEN)
Holder holder = playerRef.getHolder(); // Returns null!
holder.putComponent(raceDataType, raceData); // NullPointerException
```

The Hytale API's `getHolder()` method returns `null` in the current version, causing component saves to fail silently.

**The Solution:**
```java
// ✅ NEW CODE (WORKING)
Store<EntityStore> store = world.getEntityStore().getStore();
Ref<EntityStore> ref = playerRefComponent.getReference();
store.putComponent(ref, raceDataType, raceData); // Works!
```

Migrated to the **OrbisOrigins pattern** using `Store.putComponent(ref, ...)` instead of `Holder.putComponent()`.

### 🔧 Technical Changes

#### New Systems
- **`RaceDamageResistanceSystem`** - Damage resistance handler
  - Runs in FilterDamageGroup (before armor reduction)
  - Combines race + class resistances
  - Uses Math.min() for best resistance

#### New Commands & Classes
- **`TradeClassCommand.java`** - `/tradeclass` command implementation
- **`ResetClassCommand.java`** - `/resetclass` command implementation
- Both use `AbstractPlayerCommand` pattern (top-level, not subcommands)

#### Refactored RaceManager
- **New Method:** `applyRaceAndClass(Ref, Store, raceId, classId)` - For UI calls
- **Updated Method:** `applyRaceAndClass(Player, raceId, classId)` - For commands, now uses `world.execute()` to access Store safely
- **Rewritten:** `saveRaceAndClassSelection()` - Changed from Holder pattern to Store pattern
- **Simplified:** `getPlayerClass()` - Now reads directly from `RaceStorage` file cache
- **New Method:** `getDamageResistance(Player, String)` - Returns combined resistance multiplier

#### Enhanced Storage System
- **Added:** `RaceStorage.getPlayerClass(UUID)` method
- **Updated:** File cache format stores both race and class: `uuid|username|raceId|classId`
- **Dual Persistence:** Classes now save to both component system AND file cache

#### Config Updates
- **`RaceConfig.damageResistances`** - Map<String, Float> for race resistances
- **`ClassConfig.damageResistances`** - Map<String, Float> for class resistances

#### Command Architecture Discovery
**Important Finding:** Hytale's command system does NOT support nested subcommands reliably.

**What Doesn't Work:**
```java
// ❌ Subcommands of /race don't work
addSubCommand(new TradeClassCommand()); // /race tradeclass - NOT FOUND
addSubCommand(new ResetClassCommand()); // /race resetclass - NOT FOUND
```

**What Works:**
```java
// ✅ Top-level commands work perfectly
commands.registerCommand(new TradeClassCommand()); // /tradeclass ✓
commands.registerCommand(new ResetClassCommand()); // /resetclass ✓
```

**Solution:** Created independent top-level commands instead of `/race` subcommands.

### 📝 Updated Command List

| Command | Function | Example |
|---------|----------|---------|
| `/racetrade <race>` | Change race | `/racetrade orc` |
| `/racereset` | Reset race | `/racereset` |
| `/raceinfo [player]` | View race & class | `/raceinfo Steve` |
| `/racereload` | Reload configs | `/racereload` |
| **`/tradeclass <class>`** | **Change class** | **`/tradeclass assassin`** |
| **`/resetclass`** | **Reset class** | **`/resetclass`** |

### 📚 New Documentation

- **DAMAGE_RESISTANCE_GUIDE.md** - Complete resistance system guide
  - All damage types explained
  - Balance guidelines
  - Configuration examples
  - Troubleshooting guide

- **BALANCE_GUIDE.md** - Complete balancing principles and formulas
- **balance_reference.json** - Reference values and server presets

### 🎯 What This Means For Players

#### Before This Update:
- ❌ Class selection appeared to work but didn't save
- ❌ Had to reopen UI every session to "reselect" class
- ❌ Class bonuses never applied
- ❌ No way to change class without reopening full race UI
- ❌ No damage resistance/immunity system
- ❌ Assassin was overpowered in Elf combo

#### After This Update:
- ✅ Class selection persists correctly across sessions
- ✅ Class bonuses (damage, stats) apply immediately
- ✅ Can change class with simple command: `/tradeclass berserker`
- ✅ Can experiment with different classes easily
- ✅ Both race AND class data saved to components + file cache
- ✅ Damage resistances add new tactical depth
- ✅ New Tiefling race with fire immunity
- ✅ Assassin rebalanced for fairness

### 🔬 Debug Improvements

Added comprehensive logging throughout the save process:
- `applyRaceAndClass: Applying race=X, class=Y`
- `saveRaceAndClassSelection: Saving race=X, class=Y`
- `saveRaceAndClassSelection: Set race=X, class=Y`
- `saveRaceAndClassSelection: Component saved successfully`
- `getPlayerClass: Retrieved class=X from storage`

These logs help diagnose any future persistence issues.

### 🚀 For Server Admins

**No Breaking Changes:**
- Existing race selections remain intact
- File cache automatically migrates to new format
- No config wipes needed (except for Assassin balance - see below)
- Old JARs can be replaced directly

**Recommended Update Steps:**
1. Delete existing `classes_config.json` (to get Assassin balance fix)
2. Replace JAR file
3. Restart server
4. Run `/racereload` in-game
5. New balanced values will generate automatically

**Testing Checklist:**
1. Verify `/tradeclass assassin` works
2. Confirm `/resetclass` resets to NONE
3. Check `/raceinfo` shows both race and class
4. Test class bonuses apply (damage multipliers work)
5. Test Tiefling fire/lava immunity
6. Verify Assassin has correct stats (-35 HP, +22% damage)
7. Restart server and verify class persists

---

## Version 2026.1.26 - Two-Step Selection System

### 🎯 Major System Overhaul

#### Separated Race and Class Selection
The mod now features a **two-step selection process** that separates innate racial traits from combat specializations:

**Step 1: Choose Your Race** (Innate Traits)
- **Elf**: Agile and energetic (+15 Stamina)
- **Orc**: Powerful and robust (+75 Health)  
- **Human**: Balanced and versatile (+35 Health, +5 Stamina)

**Step 2: Choose Your Class** (Combat Specialization)
- **None**: Pure racial traits, no specialization
- **Berserker**: High-risk warrior (-25 HP, +8 Stamina, +30% axe damage)
- **Swordsman**: Balanced fighter (+10 HP, +5 Stamina, +20% sword damage)
- **Crusader**: Tank specialist (+30 HP, +15% mace/hammer damage)
- **Assassin**: Agile striker (-20 HP, +10 Stamina, +35% dagger damage)
- **Archer**: Ranged specialist (-35 HP, +8 Stamina, +40% bow/crossbow damage)

### 📁 Configuration Files

#### Two Separate JSON Files
Now you can edit races and classes independently:

**races_config.json** - Base racial traits
```json
{
  "id": "orc",
  "displayName": "Orc",
  "healthBonus": 75.0,
  "staminaBonus": 0.0,
  "strengths": ["175 HP (+75)", "Powerful physique", "Melee combat specialist"],
  "weaknesses": ["110 Stamina (+0)", "Slow stamina regeneration"]
}
```

**classes_config.json** - Combat specializations
```json
{
  "id": "berserker",
  "displayName": "Berserker",
  "healthModifier": -25.0,
  "staminaModifier": 8.0,
  "weapons": [{
    "types": ["axe", "battleaxe"],
    "damageMultiplier": 1.3
  }],
  "strengths": ["Devastating axe attacks (+30% damage)", "+8 Stamina bonus"],
  "weaknesses": ["-25 Health penalty", "High-risk playstyle"]
}
```

### 🎮 How It Works

1. **First Selection**: Player chooses their race (Elf, Orc, or Human)
2. **Second Selection**: Player chooses their class (None, Berserker, Swordsman, etc.)
3. **Combined Stats**: The system adds race bonuses + class modifiers together
   - Example: Orc (+75 HP) + Berserker (-25 HP) = **+50 total HP bonus**

### 🔧 Technical Changes

#### New Components
- `ClassConfig.java` - JSON model for class definitions
- `ClassConfigLoader.java` - Manages classes_config.json loading/saving
- `ClassSelectionPage.java` - Second-step UI for class selection
- `class_selection.ui` - UI layout for class selection screen

#### Modified Systems
- `RaceData` - Now stores both `selectedRace` and `selectedClass`
- `RaceManager.applyRaceAndClass()` - Combines race + class bonuses
- `RaceStorage` - Updated format to `uuid|username|raceId|classId`
- `RaceConfigLoader` - Simplified to 3 base races (removed weapon specializations)
- `RaceDamageBoostSystem` - Now applies class weapon bonuses instead of race weapons

#### UI Flow
- Race selection → Class selection → Combined application
- Back button in class selection returns to race selection
- Pagination supported in both screens (4 items per page)

### 🚀 Command Updates

#### `/raceinfo`
Now displays both race and class:
```
Race: Orc - Berserker (selected today at 18:24:35)
```

---

## Version 2026.1.25.2 - Class Management Commands & Critical Persistence Fix

### 🎉 What's New

#### New Class Management Commands
Two new commands for managing player classes without reopening the UI:

**`/tradeclass <class> [player]`**
- **Function:** Change your class or another player's class instantly
- **Usage:** 
  - Self: `/tradeclass assassin`
  - Others: `/tradeclass berserker Steve`
- **Valid Classes:** NONE, BERSERKER, SWORDSMAN, CRUSADER, ASSASSIN, ARCHER
- **Requirement:** Must have a race selected first
- **Permission:** Available to all players

**`/resetclass [player]`**
- **Function:** Reset your class to NONE (keeping your race)
- **Usage:**
  - Self: `/resetclass`
  - Others: `/resetclass Alex`
- **Effect:** Removes all class bonuses while preserving race bonuses
- **Permission:** Available to all players

### 🐛 Critical Bug Fix - Class Persistence

#### Fixed: Class Selection Not Saving to Components

**The Problem:**
- Players could select classes in the UI successfully
- UI showed "Class: Assassin" during selection
- **After closing UI:** `/raceinfo` showed "Class: None"
- Class bonuses (damage, stats) were not being applied
- Only race bonuses worked, class completely ignored

**Root Cause:** 
```java
// ❌ OLD CODE (BROKEN)
Holder holder = playerRef.getHolder(); // Returns null!
holder.putComponent(raceDataType, raceData); // NullPointerException
```

The Hytale API's `getHolder()` method returns `null` in the current version, causing component saves to fail silently.

**The Solution:**
```java
// ✅ NEW CODE (WORKING)
Store<EntityStore> store = world.getEntityStore().getStore();
Ref<EntityStore> ref = playerRefComponent.getReference();
store.putComponent(ref, raceDataType, raceData); // Works!
```

Migrated to the **OrbisOrigins pattern** using `Store.putComponent(ref, ...)` instead of `Holder.putComponent()`.

### 🔧 Technical Changes

#### Refactored RaceManager
- **New Method:** `applyRaceAndClass(Ref, Store, raceId, classId)` - For UI calls
- **Updated Method:** `applyRaceAndClass(Player, raceId, classId)` - For commands, now uses `world.execute()` to access Store safely
- **Rewritten:** `saveRaceAndClassSelection()` - Changed from Holder pattern to Store pattern
- **Simplified:** `getPlayerClass()` - Now reads directly from `RaceStorage` file cache

#### Enhanced Storage System
- **Added:** `RaceStorage.getPlayerClass(UUID)` method
- **Updated:** File cache format now stores both race and class: `uuid|username|raceId|classId`
- **Dual Persistence:** Classes now save to both component system AND file cache

#### Command Architecture Discovery
**Important Finding:** Hytale's command system does NOT support nested subcommands reliably.

**What Doesn't Work:**
```java
// ❌ Subcommands of /race don't work
addSubCommand(new TradeClassCommand()); // /race tradeclass - NOT FOUND
addSubCommand(new ResetClassCommand()); // /race resetclass - NOT FOUND
```

**What Works:**
```java
// ✅ Top-level commands work perfectly
commands.registerCommand(new TradeClassCommand()); // /tradeclass ✓
commands.registerCommand(new ResetClassCommand()); // /resetclass ✓
```

**Solution:** Created independent top-level commands instead of `/race` subcommands.

### 📝 Updated Command List

| Command | Function | Example |
|---------|----------|---------|
| `/racetrade <race>` | Change race | `/racetrade orc` |
| `/racereset` | Reset race | `/racereset` |
| `/raceinfo [player]` | View race & class | `/raceinfo Steve` |
| `/racereload` | Reload configs | `/racereload` |
| **`/tradeclass <class>`** | **Change class** | **`/tradeclass assassin`** |
| **`/resetclass`** | **Reset class** | **`/resetclass`** |

### 🎯 What This Means For Players

#### Before This Update:
- ❌ Class selection appeared to work but didn't save
- ❌ Had to reopen UI every session to "reselect" class
- ❌ Class bonuses never applied
- ❌ No way to change class without reopening full race UI

#### After This Update:
- ✅ Class selection persists correctly across sessions
- ✅ Class bonuses (damage, stats) apply immediately
- ✅ Can change class with simple command: `/tradeclass berserker`
- ✅ Can experiment with different classes easily
- ✅ Both race AND class data saved to components + file cache

### 🔬 Debug Improvements

Added comprehensive logging throughout the save process:
- `applyRaceAndClass: Applying race=X, class=Y`
- `saveRaceAndClassSelection: Saving race=X, class=Y`
- `saveRaceAndClassSelection: Set race=X, class=Y`
- `saveRaceAndClassSelection: Component saved successfully`
- `getPlayerClass: Retrieved class=X from storage`

These logs help diagnose any future persistence issues.

### 🚀 For Server Admins

**No Breaking Changes:**
- Existing race selections remain intact
- File cache automatically migrates to new format
- No config wipes needed
- Old JARs can be replaced directly

**Testing Checklist:**
1. Verify `/tradeclass assassin` works
2. Confirm `/resetclass` resets to NONE
3. Check `/raceinfo` shows both race and class
4. Test class bonuses apply (damage multipliers work)
5. Restart server and verify class persists

---

## Version 2026.1.25 - JSON Update (The Most Requested Feature!)

### 🎉 What's New - Community-Requested Feature!

#### JSON-Based Race Configuration System
**The feature you've all been asking for is finally here!** Players can now customize and balance races without touching any code!

### 📝 How It Works

#### File Location
Your race configuration is stored in a JSON file at:
```
%APPDATA%\Roaming\Hytale\UserData\Saves\[World Name]\mods\OrbisAndDungeons_RaceSelection\races_config.json
```

Simply navigate to your world's `mods` folder and edit the `races_config.json` file!

#### What You Can Do

**1. Modify Existing Classes:**
- Change health and stamina values
- Adjust weapon damage multipliers
- Edit class descriptions and taglines
- Tweak strengths and weaknesses

**2. Balance The Game Your Way:**
- Find Berserker too weak? Buff his HP!
- Think Archer is overpowered? Lower the damage bonus!
- Want all classes to start with 150 HP? Go for it!
- Create your own meta!

**3. Add Your Own Custom Classes:**
- Create completely new races with unique stats
- Define custom weapon specializations
- Design your own class identity
- No coding required - just edit the JSON!

### 🔧 How To Use

#### Modifying Classes
1. Open `races_config.json` in your world's mods folder
2. Find the class you want to modify
3. Change the values (HP, Stamina, damage multipliers, etc.)
4. Save the file
5. In-game, use `/racereload` command (or restart server)
6. Done! Changes are applied instantly

#### Adding New Classes
1. Copy an existing race entry in the JSON
2. Change the `"id"` to something unique (e.g., "necromancer")
3. Modify all the stats and descriptions
4. Save the file
5. Use `/racereload` command
6. Your new class appears in the selection UI automatically!

### 📋 Example Configuration

```json
{
  "id": "necromancer",
  "displayName": "Necromancer",
  "tagline": "Master of dark arts and death magic.",
  "healthBonus": -10.0,
  "staminaBonus": 12.0,
  "strengths": [
    "90 HP (-10)",
    "22 Stamina (+12)",
    "Magic specialist"
  ],
  "weaknesses": [
    "Fragile in melee",
    "Weak against holy damage"
  ],
  "weapons": [
    {
      "types": ["staff", "wand"],
      "damageMultiplier": 1.25
    }
  ]
}
```

### 🎮 Dynamic UI System

**Fully Automatic:**
- UI automatically detects all races from JSON
- Pagination adjusts based on number of classes
- 4 races per page, unlimited pages supported
- No code changes needed - ever!

**Scalability:**
- 8 classes = 2 pages
- 12 classes = 3 pages
- 20 classes = 5 pages
- Add as many as you want!

### ⚡ New Commands

#### `/racereload`
- **Permission:** Requires OP/admin
- **Function:** Reloads race configuration from JSON without server restart
- **Usage:** After editing races_config.json, run this command to apply changes instantly
- **Effect:** All race stats, bonuses, and descriptions update immediately

### 🛠️ Technical Details

**Configuration Format:**
- `healthBonus`: Bonus HP relative to base (100 HP baseline)
- `staminaBonus`: Bonus stamina relative to base (10 Stamina baseline)
- `weapons`: Array of weapon type bonuses
  - `types`: Keywords to match weapon IDs (e.g., "sword", "axe", "bow")
  - `damageMultiplier`: Damage multiplier (1.0 = normal, 1.30 = +30%)

**Auto-Generation:**
- First run creates default config with all 8 current classes
- Includes detailed stat information in strengths/weaknesses
- Pretty-printed JSON for easy editing

### 📚 Documentation

For detailed configuration guide, see: `docs/RACE_CONFIGURATION.md`

---

## Version 2026.1.24 - Weapon-Specialized Classes

### 🎉 What's New

#### Four New Combat Classes
Each class is specialized for a specific weapon type with unique playstyles:

**⚔️ Swordsman** - *Balanced Warrior*
- **Health:** 110 HP (+10)
- **Stamina:** 15 (+5)
- **Weapon Bonus:** +20% damage with swords
- **Playstyle:** Jack-of-all-trades with sword specialization

**🛡️ Crusader** - *Mace Specialist*
- **Health:** 130 HP (+30)
- **Stamina:** 10 (+0)
- **Weapon Bonus:** +15% damage with maces and hammers
- **Playstyle:** Tanky frontline fighter with no mobility

**🗡️ Assassin** - *Dagger Master*
- **Health:** 80 HP (-20)
- **Stamina:** 20 (+10)
- **Weapon Bonus:** +35% damage with daggers
- **Playstyle:** Glass cannon with high burst damage

**🏹 Archer** - *Ranged Specialist*
- **Health:** 65 HP (-35)
- **Stamina:** 18 (+8)
- **Weapon Bonus:** +40% damage with bows and crossbows
- **Playstyle:** High damage at range but very fragile

### ⚖️ Balance Changes

#### Berserker (Rebalanced)
- **Health:** 75 HP (-25)
- **Stamina:** 18 (+8)
- **Weapon Bonus:** +30% damage with axes
- **Impact:** High damage output but sacrifices survivability

### 📊 Class Comparison Table

| Class      | Health | Stamina | Weapon Type      | Damage Bonus | Total Power* |
|------------|--------|---------|------------------|--------------|--------------|
| Orc        | 175    | 10      | None             | 0%           | 185          |
| Human      | 135    | 15      | None             | 0%           | 150          |
| Crusader   | 130    | 10      | Mace/Hammer      | +15%         | 155          |
| Elf        | 100    | 25      | None             | 0%           | 125          |
| Swordsman  | 110    | 15      | Sword            | +20%         | 145          |
| Assassin   | 80     | 20      | Dagger           | +35%         | 135          |
| Berserker  | 75     | 18      | Axe              | +30%         | 123          |
| Archer     | 65     | 18      | Bow/Crossbow     | +40%         | 123          |

*Total Power = HP + Stamina + (Damage% × 100) - rough approximation

### 🎮 Design Philosophy

**Trade-offs Over Pure Power:**
- Specialized classes trade stats for damage bonuses
- Human/Elf/Orc remain competitive for players who prefer stats
- No "best" class - each has clear strengths and weaknesses

**Weapon Specialization:**
- Damage bonuses are modest (+15% to +40%)
- Lower stats balance the offensive bonuses
- Glass cannons (Assassin/Archer) have highest damage but lowest survivability

**Class Identity:**
- Base races (Human/Elf/Orc): High stats, no weapon bonuses, versatile
- Specialized classes: Lower stats, weapon bonuses, require specific playstyle

### 🖥️ User Interface Improvements

#### Paginated Race Selection
- **Dynamic Pagination System**: Race selection UI now supports multiple pages
  - 4 classes per page for cleaner visual presentation
  - Previous/Next navigation buttons for page switching
  - Page indicator shows current page (e.g., "Page 1 / 2")
- **Fixed UI Elements**: Page counter position locked to prevent text shifting
- **Dynamic Button Generation**: Buttons are generated with unique IDs for proper event handling
- **Scalable Design**: UI automatically adapts to number of available races

### 🐛 Bug Fixes
- Fixed Elf class descriptions that incorrectly mentioned infinite stamina
- Corrected UI event binding system to use unique button identifiers
- Fixed page info label positioning to prevent movement during navigation

---

## Version 2026.1.23-hotfix - Race Info Access

### 🐛 Bug Fixes
- `raceinfo` now reads the cache/file (`race_cache.txt`) so it shows a player’s race even if the target is offline or their component isn’t loaded.

### 🔧 Changes
- `/raceinfo` is now open to all players (no OP/permission required).


---

## Version 2026.1.23 - Persistent File-Based Cache System & Berserker Class

### 🎉 What's New

#### New Berserker Class
- **Berserker Race Added**: Aggressive melee-focused class with enhanced weapon damage
- **Weapon Damage System**: New per-class weapon damage modifiers system
  - Each class can have custom damage multipliers for different weapon types
  - Berserker excels in close-quarters combat with bonus melee damage

#### Modular Race System Architecture
- **Flexible Attribute System**: New modular parameter system for defining races
  - **Health** (Vida): Customizable max health per class
  - **Stamina**: Customizable stamina pool per class
  - **Breath** (Fôlego): Customizable breath/oxygen capacity per class
  - **Mana**: Customizable mana pool for magical abilities per class
  - **Weapon Damage**: Per-class weapon damage modifiers
  
- **Easy Class Addition**: Simplified process for adding new races/classes
  - Define attributes through clear parameters
  - Automatic application of stat modifiers
  - Consistent behavior across all classes

- **Future-Ready**: Architecture prepared for upcoming features
  - Resistance/Defense modifiers (coming soon)
  - Expandable to additional stat types

#### File-Based Race Cache
- **Added `RaceStorage` System**: New file-based caching layer for race data persistence
- **Dual Persistence Strategy**: Race data is now stored in both component system AND file cache
- **Improved Reliability**: File cache (`race_cache.txt`) acts as backup and faster lookup mechanism
- **Cross-Session Support**: Race data persists even if components fail to load

### 🐛 Bug Fixes

#### Fixed: Race Selection Prompt on Portal Travel
**Issue:** Players were prompted to select their race again when entering portals or traveling between dimensions.

**Root Cause:** The game temporarily unloads and reloads player entities during portal transitions, causing the race tracking system to lose the player's selection.

**Solution:** The new file-based storage system (`RaceStorage`) maintains race data independently of entity lifecycle, preventing re-selection prompts during portal travel.

### 🔧 Technical Improvements

#### Storage System
- **`RaceStorage.java` Class**: New storage manager with concurrent thread-safe operations
  - Stores player UUID, username, and race ID
  - Format: `uuid|username|raceId` (one entry per line)
  - Automatic save on every race change
  - Load on mod initialization
  
- **Enhanced `RaceManager`**:
  - Now saves to both component system and file storage
  - Fallback mechanism: checks file storage if component data is unavailable
  - Better resilience against data loss
  - Portal travel now preserves race selection

- **Updated `RaceMod`**:
  - Initializes `RaceStorage` on startup with mod data directory
  - Ensures storage directory exists before operations

#### Storage Location
- File: `<mod_data_directory>/race_cache.txt`
- Thread-safe concurrent access
- UTF-8 encoding for proper character support

#### Fallback Chain
When loading a player's race, the system now checks:
1. Memory cache (fastest)
2. Component system (primary persistence)
3. **File storage (new fallback layer)**

This triple-redundancy ensures race selections are never lost during portal travel or server restarts.

---

## Version 2026.1.21 - Component System & Admin Commands

### 🎉 What's New

#### Component-Based Architecture
- **Migrated to Component System**: Race data is now stored using Hytale's persistent component system (`RaceData` component)
- **Persistent Storage**: Race selection, timestamp, and player data now persist across server restarts
- **Better Performance**: Eliminated memory-based tracking in favor of entity components

#### New Admin Commands
Added three new operator commands for race management:

**`/racetrade <race> [--player <username>]`**
- Change your race or another player's race
- Arguments: `HUMAN`, `ELF`, or `ORC`
- Example: `/racetrade elf --player Steve`

**`/racereset [--player <username>]`**
- Reset race selection (player must reconnect to choose again)
- Example: `/racereset --player Alex`

**`/raceinfo [--player <username>]`**
- Display detailed race information including selection date
- Example: `/raceinfo --player Notch`

All commands support the optional `--player` argument for operators to manage other players' races.

---

## Version 2026.1.20 Release Notes

### 🎉 What's New

This release focuses on **fixing critical multiplayer issues** and improving overall stability and user experience.

---

## 🐛 Critical Fixes

### Fixed: Race Selection Prompt on Every Server Reconnection
**Issue:** On dedicated servers, the race selection UI appeared every time a player reconnected, even after already choosing a race.

**Root Cause:** The mod was using an in-memory `Set<PlayerRef>` to track players who had selected races. In dedicated server environments, `PlayerRef` instances are recreated on each connection, causing the system to "forget" that the player had already chosen.

**Solution Implemented:** The mod now **checks if race stat modifiers are already applied** to the player instead of relying on memory-based tracking.

#### Technical Details

**Old Method (Memory-Based):**
```java
// Problem: PlayerRef changes on reconnect in dedicated servers
private static final Set<PlayerRef> playersWithRace = new HashSet<>();

// This fails when player reconnects
if (playersWithRace.contains(playerRef)) {
    return; // Never worked reliably
}
```

**New Method (Stat-Based Persistence):**
```java
public static boolean hasRaceApplied(Player player) {
    EntityStatMap stats = EntityStatsModule.get(player);
    
    // Check if Health or Stamina differ from base values
    var healthStat = stats.get("Health");
    var staminaStat = stats.get("Stamina");
    
    // Base values: Health=100, Stamina=10
    if (healthStat != null && healthStat.getMax() != 100f) {
        return true; // Orc or Human race applied
    }
    
    if (staminaStat != null && staminaStat.getMax() != 10f) {
        return true; // Elf or Human race applied
    }
    
    return false;
}
```

#### Why This Works

**Persistence:** Stat modifiers are saved with player data by Hytale's system. They persist:
- ✅ Between player reconnections
- ✅ Between server restarts
- ✅ Across dimension changes
- ✅ Through all game sessions

**Detection Logic:**
- **Elf:** Stamina max = 25 (base 10 + 15 bonus)
- **Orc:** Health max = 175 (base 100 + 75 bonus)
- **Human:** Both stats modified (Health 135, Stamina 15)

If either stat differs from base (100 HP or 10 Stamina), a race has been selected.

#### ⚠️ Important Notes - Temporary Solution

**This is a provisional detection method.** It works by comparing current stat values against known base values.

**Limitations:**
1. **Assumption-based:** Assumes base stats are always 100 HP / 10 Stamina
2. **Fragile:** If another mod modifies these stats, detection may fail
3. **No direct tracking:** Doesn't store which specific race was chosen, only that *something* was applied

**Future Improvements Planned:**
- Implement proper persistent data storage (NBT tags or similar)
- Store race choice explicitly in player data
- Add race metadata for better tracking
- Support for stat modifications from other mods

**Current Status:** ✅ Works reliably for vanilla game and single-mod environments

---

## 🔧 Other Improvements

### Removed Spellbook Dependency
The mod no longer requires Spellbook as a dependency. It now uses only native Hytale APIs, making it:
- ✅ Lighter weight
- ✅ Easier to install
- ✅ More compatible with other mods
- ✅ Fewer potential conflicts

### Full English Localization
All UI text has been translated to English for broader accessibility:
- Title: "Select Your Race"
- Button labels: "ELF", "ORC", "HUMAN"
- Section headers: "STRENGTHS", "WEAKNESSES"
- Confirm button: "Confirm Selection"

---

## 📊 Race Balance (Unchanged)

Races remain balanced as in previous version:

| Race | Health | Stamina | Playstyle |
|------|--------|---------|-----------|
| **Elf** | 100 (base) | 25 (+15) | High mobility, agile combat |
| **Orc** | 175 (+75) | 10 (base) | Tank, frontline warrior |
| **Human** | 135 (+35) | 15 (+5) | Balanced, all-rounder |

---

## 🔍 Testing Recommendations

For server administrators and mod testers:

### Test Cases
1. **First-time selection:** Verify UI appears on first world join
2. **Reconnection:** Disconnect and reconnect - UI should NOT reappear
3. **Server restart:** Restart server, rejoin - UI should NOT reappear
4. **Dimension travel:** Travel through portals - UI should NOT reappear
5. **Stat persistence:** Verify race bonuses remain after reconnect

### Known Working Scenarios
- ✅ Single player
- ✅ Local multiplayer
- ✅ Dedicated servers
- ✅ Server restarts
- ✅ Player reconnections

### Debug Verification
To verify race is applied, check in-game:
- **Elf:** Press F3 or check stats - should show 25 Stamina
- **Orc:** Should show 175 Health
- **Human:** Should show 135 Health, 15 Stamina

---

## 🐛 Bug Reports

If you encounter issues:
1. Verify you're using the latest version (2026.1.20+)
2. Check if race selection reappears after reconnection
3. Verify stats are correctly applied (use F3/debug mode)
4. Report with server type (dedicated/local), Hytale version, and any other mods installed

---

## 📝 Technical Notes for Developers

### Race Detection Implementation

The race detection system in `RaceManager.hasRaceApplied()` is designed to be lightweight and compatible with Hytale's save system.

**How it works:**
```
Player connects → Check stats → Compare to base values → Decision
    ↓
    └─ Base (100/10) → Show UI
    └─ Modified → Skip UI
```

**Reliability Factors:**
- Uses deprecated but functional `EntityStatsModule.get()`
- Exception-safe with try-catch fallback
- Returns `false` on any error (safe default = show UI)

**Future Migration Path:**
When Hytale provides stable persistent player data APIs, this system should be migrated to:
1. Store race choice in player NBT/persistent data
2. Read choice directly instead of inferring from stats
3. Support arbitrary stat modifications from any source

---

## 🎮 Compatibility Matrix

| Component | Status | Notes |
|-----------|--------|-------|
| Hytale Server | ✅ Compatible | Tested on latest |
| Dedicated Servers | ✅ Fixed | Main focus of this release |
| Custom Armor Mods | ✅ Compatible | Only modifies MAX stats |
| Other Stat Mods | ⚠️ Caution | May interfere with detection |
| Dimension Mods | ✅ Compatible | No conflicts |

---

## 📥 Installation

1. Download `Orbis_and_Dungeons-2026.1.20-*.jar`
2. Remove old version if upgrading
3. Place in `UserData/Saves/mods/mods/`
4. Restart Hytale/Server
5. Existing players: Stats persist automatically
6. New players: Will see race selection on first join

---

## 💬 Support

- Report issues on the mod page
- Provide logs when reporting bugs
- Join community Discord for help

---

**Enjoy your adventures! Choose your race wisely - it's permanent!** ⚔️🏹⚖️
