# Orbis and Dungeons - Changelog

---

## Version 2026.1.26.1 - Assassin Balance Fix

### ⚖️ Balance Changes (Community Feedback)

#### Assassin Class Rebalanced

**The Problem (Community Report):**
- Dual daggers have built-in "Hit n' Run" charged attack mobility
- 35% damage boost was amplifying charged attacks excessively  
- -20 HP penalty was too small for weapon safety level
- High stamina allowed constant charged attack spam
- Elf Assassin combo reached 205 EHP (overpowered)

**The Solution:**
- Health: **-20 → -35** (increased penalty to match Archer)
- Damage: **+35% → +22%** (reduced for weapon safety)
- Stamina: **+10** (unchanged - maintains hit-n-run fantasy)
- New EHP: **190** (balanced with other glass cannons)

**Rationale:**
- Daggers are safer than axes (charged attack dash)
- Cannot nerf charged attacks separately (API limitation)
- Applied 1 Stamina = 5 HP balance ratio
- Health penalty now reflects weapon safety level

### 📊 New Balance System

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

### 📁 New Documentation

#### Balance Guide
- [BALANCE_GUIDE.md](docs/BALANCE_GUIDE.md) - Complete balancing principles and formulas
- [balance_reference.json](docs/balance_reference.json) - Reference values and server presets

**What's Included:**
- EHP calculation formulas
- Weapon damage by safety level
- Custom class creation guide
- Server admin presets (Hardcore, Casual, PvP)
- Common balance mistakes to avoid

### 🎮 For Players

**Assassin Changes:**
- More fragile but still mobile
- Slightly lower damage but still deadly
- Requires better positioning and skill
- Hit-n-run playstyle preserved

**How to Update:**
1. Delete existing `classes_config.json`
2. Run `/racereload` in-game
3. New balanced values will generate automatically

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
