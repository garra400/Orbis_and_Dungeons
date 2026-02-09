# Mod Integration Guide

## Overview

Orbis and Dungeons now features compatibility with two popular Hytale mods:
- **RPGLeveling** - Level-based progression system
- **HardcoreMode** - Dynamic difficulty scaling

The integration is **optional** and uses reflection to avoid hard dependencies. The mod works perfectly standalone or with either/both companion mods installed.

## Architecture

### Integration Layer Structure

```
src/main/java/com/garra400/racas/integration/
├── ModIntegration.java              # Main facade - auto-detection & API
├── RPGLevelingIntegration.java      # RPGLeveling event handling
└── HardcoreModeIntegration.java     # HardcoreMode difficulty scaling
```

### How It Works

1. **Auto-Detection**: On mod startup, `ModIntegration.initialize()` attempts to detect companion mods via reflection:
   ```java
   Class.forName("org.zuxaw.plugin.api.RPGLevelingAPI");
   ```

2. **Graceful Fallback**: If a mod isn't found, integration is disabled and Orbis runs standalone

3. **No Hard Dependencies**: All external mod classes accessed via reflection - JAR files are NOT required

4. **Event-Driven**: Listens for RPGLeveling events to synchronize stats automatically

## RPGLeveling Integration

### Features

- **Stat Synchronization**: Race/class bonuses preserved after level-ups
- **Event Handling**: Automatically reapplies bonuses when player levels up
- **Level-Based Scaling**: Access player level/XP for future features

### How It Works

#### Event Listener Registration

When RPGLeveling is detected, registers a global event listener:

```java
EventRegistry.registerGlobal(LevelUpEvent.class, event -> {
    Player player = event.getPlayer();
    ModIntegration.onPlayerLevelUp(player);
});
```

#### Stat Preservation

RPGLeveling recalculates stats on level-up, which could override race bonuses. Integration prevents this:

1. Player levels up → `LevelUpEvent` fires
2. Event listener catches event
3. Calls `ModIntegration.onPlayerLevelUp(player)`
4. Reapplies race/class bonuses via `RaceManager.applyRaceAndClass()`
5. Race bonuses persist alongside RPGLeveling stats

#### Additive System

Both mods use `EntityStatMap.addModifier()` which is **additive**:
- RPGLeveling: Adds +10 HP for level 5
- Orbis Orc Race: Adds +100 HP
- **Result**: Player has 210 HP total (100 base + 10 level + 100 race)

### API Methods

```java
// Check if RPGLeveling is available
ModIntegration.isRPGLevelingAvailable()

// Get player level (returns 0 if not available)
int level = ModIntegration.getPlayerLevel(player);

// Get player XP (returns 0 if not available)
long xp = ModIntegration.getPlayerXP(player);

// Apply race with level synchronization
ModIntegration.applyRaceWithLevelSync(player, raceId, classId);
```

## HardcoreMode Integration

### Features

- **Race-Based Difficulty**: Tank races face tougher mobs, fragile races face weaker mobs
- **Class-Based Difficulty**: High-damage classes face increased challenge
- **Spawn Rate Modifiers**: Tank races attract more enemies
- **Level Scaling**: Difficulty increases with RPGLeveling level

### How It Works

HardcoreMode can query Orbis via the `ModIntegration` API to adjust difficulty dynamically.

#### Difficulty Multipliers

**By Race:**
| Race | Multiplier | Effect |
|------|------------|--------|
| Orc, Dwarf | 1.15x | +15% mob stats (HP, damage) |
| Human | 1.0x | Standard difficulty |
| Elf, Tiefling | 0.90x | -10% mob stats |

**By Class:**
| Class | Multiplier | Effect |
|-------|------------|--------|
| Berserker, Archer, Mage | 1.10x | +10% mob stats |
| Swordsman, Crusader | 1.05x | +5% mob stats |
| Assassin | 0.95x | -5% mob stats |
| None | 1.0x | Standard |

**Combined Example:**
- Orc Berserker: 1.15 × 1.10 = **1.265x** (+26.5% mob difficulty)
- Elf Assassin: 0.90 × 0.95 = **0.855x** (-14.5% mob difficulty)

#### Spawn Rate Modifiers

**Increased Spawns (+20%):**
- Orc (attracts enemies with intimidating presence)
- Dwarf (sturdy, draws aggro)

**Decreased Spawns (-20%):**
- Elf (stealthy, avoids detection)
- Tiefling (demonic aura repels weaker enemies)

#### Level Scaling

If both RPGLeveling and HardcoreMode are present:

```java
float levelMult = 1.0f + (level * 0.02f); // +2% per level
float finalDifficulty = baseDifficulty * raceClassMult * levelMult;
```

**Example: Level 25 Orc Berserker**
- Base: 1.0
- Race+Class: 1.265
- Level: 1.0 + (25 × 0.02) = 1.5
- **Final: 1.0 × 1.265 × 1.5 = 1.898x** (nearly double difficulty!)

### API Methods

```java
// Get combined player difficulty multiplier
float mult = ModIntegration.getPlayerDifficultyMultiplier(player);

// Get race-specific multiplier
float raceMult = ModIntegration.getRaceDifficultyMultiplier("orc");

// Get class-specific multiplier
float classMult = ModIntegration.getClassDifficultyMultiplier("berserker");

// Calculate mob difficulty with level scaling
float mobDiff = HardcoreModeIntegration.calculateMobDifficulty(player, 1.0f);

// Get spawn rate multiplier
float spawnMult = HardcoreModeIntegration.getSpawnRateMultiplier("orc");
```

## Installation & Configuration

### Server Setup

1. **Install Orbis and Dungeons** (required)
2. **Install RPGLeveling** (optional) - [Download from mod repository]
3. **Install HardcoreMode** (optional) - [Download from mod repository]

No configuration needed - integration is automatic!

### Manifest Declaration

The `manifest.json` declares optional dependencies:

```json
{
  "OptionalDependencies": {
    "RPGLeveling": "*",
    "HardcoreMode": "*"
  }
}
```

This tells Hytale's mod loader:
- Load Orbis after these mods (if present)
- Don't fail if they're missing

### Initialization Order

```java
RaceMod.start() {
    // 1. Initialize configs and storage
    TranslationManager.initialize();
    RaceConfigLoader.init();
    ClassConfigLoader.init();
    RaceStorage.init();

    // 2. Initialize mod integrations
    ModIntegration.initialize(); // Auto-detects RPGLeveling & HardcoreMode

    // 3. Register components, systems, commands, events
    // ...
}
```

## Testing Integration

### Verify Detection

Check server console on startup:

```
[Orbis] RPGLeveling detected - integration enabled
[Orbis] HardcoreMode detected - integration enabled
[Orbis] Successfully registered RPGLeveling event listeners
[Orbis] HardcoreMode integration initialized (passive API mode)
```

Or if running standalone:

```
[Orbis] RPGLeveling not found - running standalone
[Orbis] HardcoreMode not found - running standalone
```

### Test RPGLeveling Sync

1. Select a race (e.g., Orc with 175 HP)
2. Check HP: Should be 175
3. Level up via RPGLeveling
4. Check HP again: Should be 175 + level bonus (not reset to 100)
5. Console should show:
   ```
   [Orbis] Reapplying race bonuses after level-up: orc / none
   ```

### Test HardcoreMode Scaling

1. Select Orc Berserker
2. Fight a zombie
3. Zombie should have ~27% more HP/damage than normal
4. Observe increased spawn rate in dangerous areas

## Troubleshooting

### Integration Not Working

**Symptom**: Mods installed but Orbis shows "not found"

**Solutions**:
1. Check mod loading order in Hytale's mod list
2. Verify RPGLeveling/HardcoreMode are enabled
3. Check for mod version compatibility
4. Review console for `ClassNotFoundException` errors

### Stats Reset After Level-Up

**Symptom**: Race bonuses disappear when leveling up

**Possible Causes**:
1. RPGLeveling integration failed to initialize
2. Event listener not registered properly
3. Race bonuses not saved in persistent component

**Solutions**:
1. Check console for event registration errors
2. Verify `ModIntegration.initialize()` was called
3. Run `/raceinfo` to check if race is properly stored
4. Try `/racetrade orc` to reapply bonuses

### Difficulty Not Scaling

**Symptom**: HardcoreMode difficulty doesn't reflect race/class

**Possible Causes**:
1. HardcoreMode not calling Orbis API
2. Race/class not properly set
3. HardcoreMode configuration overriding

**Solutions**:
1. Verify HardcoreMode integration is active (check console)
2. Run `/raceinfo` to confirm race/class selection
3. Check HardcoreMode config for compatibility settings

## Future Enhancements

### Planned Features

- **Skill Trees**: RPGLeveling skill points affect race abilities
- **Dynamic Passives**: Unlock race-specific abilities via leveling
- **Difficulty Profiles**: HardcoreMode presets per race combination
- **Cross-Mod Quests**: RPGLeveling quests reward race-specific items

### Adding New Integrations

To integrate with another mod:

1. Create integration class in `integration/` package
2. Add detection logic to `ModIntegration.initialize()`:
   ```java
   try {
       Class.forName("com.example.NewMod");
       newModAvailable = true;
       NewModIntegration.initialize();
   } catch (ClassNotFoundException e) {
       newModAvailable = false;
   }
   ```
3. Use reflection to access mod classes (avoid hard dependencies)
4. Add optional dependency to `manifest.json`

## Technical Reference

### Reflection Pattern

All external mod access uses reflection to avoid compile-time dependencies:

```java
// Instead of direct call (creates hard dependency):
RPGLevelingAPI.getPlayerLevel(player); // ❌ Breaks if mod missing

// Use reflection (graceful fallback):
Class<?> apiClass = Class.forName("org.zuxaw.plugin.api.RPGLevelingAPI");
Method getLevelMethod = apiClass.getMethod("getPlayerLevel", Player.class);
Integer level = (Integer) getLevelMethod.invoke(null, player); // ✅ Safe
```

### Component Persistence

Race data persists via Hytale's component system:

```java
@Component(name = "RaceData")
public class RaceData {
    public String raceId;
    public String classId;
    public long selectionTimestamp;
}
```

This ensures:
- Survives server restarts
- Works across worlds/dimensions
- Automatically serialized to disk

### Stat Modifier System

Both mods use additive modifiers:

```java
EntityStatMap stats = player.getStats();
stats.addModifier(StatTypes.MAX_HEALTH, new StatModifier(
    "orbis_race_orc",        // Unique ID
    100.0f,                  // +100 HP
    ModifierOperation.ADD    // Additive
));
```

Multiple modifiers stack:
- Orbis adds "orbis_race_orc" → +100 HP
- RPGLeveling adds "rpg_level_10" → +20 HP
- **Total bonus: +120 HP**

---

**Last Updated**: 2026-02-09
**Integrated Mods**: RPGLeveling, HardcoreMode
**Status**: Fully Functional, Optional
