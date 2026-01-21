# Orbis and Dungeons - Version 2026.1.20 Release Notes

## 🎉 What's New

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
