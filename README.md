# 📦 Orbis & Dungeons: Race, Class & Mana System

**Current Version:** 2026.2.9 — Unified Commands, Mod Integration & UI Modernization

## Overview

**Orbis and Dungeons** introduces a permanent, modular character progression system to Hytale. Upon joining the world, players define their legacy through a robust **Race and Class system**.

Unlike standard gameplay, your choices here define your physical strengths, your weapon mastery, your magical capacity, and how you interact with the elements of the world. Will you be a tanky Orc with 200 HP, a nimble Elf, a fire-immune Tiefling, or a spell-casting Mage? The choice is yours, and it persists across sessions.

***

## 🌟 What's New in 2026.2.9

This version brings a **complete command overhaul**, **mod integration system**, **modernized UI**, and **smart reset commands** that automatically open selection UIs.

### Key Highlights

- **Unified Command System**: `/race`, `/class`, `/build`, `/language` — all with clean subcommands
- **Smart Reset Commands**: `/race reset` opens only the race UI, `/class reset` opens only the class UI, `/build reset` opens both
- **Mod Integration**: Automatic sync with RPGLeveling and HardcoreMode mods
- **4 Languages**: English, Português (Brasil), Español, Русский
- **Modern UI**: 850×600 selection screens with hover effects and pagination

***

## 🧬 Base Races (Biology)

Your race determines your base survivability and how the world affects you.

| Race | Role | Health | Stamina | Special Traits |
|------|------|--------|---------|----------------|
| **Orc** | Pure Tank | **200** (+100) | **8** (-2) | Massive health pool. Highest HP in the game. |
| **Dwarf** | Defensive Tank | **150** (+50) | 10 | 20% Physical Resistance, 50% Fall Resistance. |
| **Human** | Balanced | **135** (+35) | **15** (+5) | Jack-of-all-trades. No weaknesses. |
| **Elf** | Speed | 100 | **25** (+15) | High mobility for parkour and dodging. |
| **Tiefling** | Hybrid | **85** (-15) | **18** (+8) | Immune to Fire/Lava, weak to Magic (+50%). |

> **Base Stats**: Health = 100, Stamina = 10, Mana = 100

***

## ⚔️ Combat Classes (Training)

Classes act as **modifiers** to your Race. They grant significant damage bonuses to specific weapons but often require a trade-off in Health, Stamina, or Mana.

### 🔮 Mage

- **Weapon:** Staves, Wands & Scepters (**+25% Damage**)
- **Modifier:** -40 HP | +12 Stamina | **+100 Mana**
- **Playstyle:** Glass Cannon Spellcaster. Huge mana pool for magic, but extremely fragile.

### 🛡️ Crusader

- **Weapon:** Maces & Hammers (**+15% Damage**)
- **Modifier:** +30 HP | +0 Stamina
- **Playstyle:** Tank specialist. Great for survivability and frontline combat.

### ⚔️ Swordsman

- **Weapon:** Swords (**+20% Damage**)
- **Modifier:** +10 HP | +5 Stamina
- **Playstyle:** Versatile combatant. Good offense with decent defense.

### 🪓 Berserker

- **Weapon:** Axes & Battleaxes (**+30% Damage**)
- **Modifier:** -25 HP | +8 Stamina
- **Playstyle:** High risk, high reward. Sacrifices health for aggressive power.

### 🗡️ Assassin

- **Weapon:** Daggers (**+22% Damage**)
- **Modifier:** -35 HP | +10 Stamina
- **Playstyle:** Glass Cannon. Incredible speed and burst damage, extremely fragile.

### 🏹 Archer

- **Weapon:** Bows & Crossbows (**+40% Damage**)
- **Modifier:** -35 HP | +8 Stamina
- **Playstyle:** Extreme range sniper. Keep your distance — if you get caught, you die.

***

## 🛡️ Damage Resistance System

The world reacts to your biology. The system automatically applies the **best resistance available** from your Race or Class against specific elemental types.

- **Values:**
  - `0.0` = **Immune** (No damage)
  - `0.5` = **Resistant** (50% less damage)
  - `1.0` = Normal damage
  - `1.5` = **Weakness** (50% more damage)
- **Damage Types:** Fire, Lava, Physical, Magic, Poison, Lightning, Cold, Nature, Fall, Drowning, Void.

> **Spotlight: The Dwarf** — 20% Physical Resistance + 50% Fall Resistance. Toughest race against standard combat and terrain hazards.

> **Spotlight: The Tiefling** — 100% Fire + Lava Immunity. Can walk through fire and lava unharmed, but takes 50% extra Magic damage.

***

## 🔌 Mod Integration System

Orbis & Dungeons automatically detects and integrates with compatible mods:

### RPGLeveling Integration
- **Event-Driven**: Listens for level-up events and reapplies race/class bonuses
- **Additive System**: Race bonuses stack with RPGLeveling stats (no conflicts)
- **Zero Dependencies**: Uses reflection — works with or without RPGLeveling installed

### HardcoreMode Integration
- **Race-Based Difficulty**: Tank races face harder mobs, fragile races face easier mobs
- **Class-Based Scaling**: High-damage classes face increased challenge
- **Spawn Rate Modifiers**: Tank races attract more enemies, stealthy races attract fewer

***

## 💻 Commands

### `/race` — Race Management
| Subcommand | Usage | Description |
|------------|-------|-------------|
| `select` | `/race select [--player <name>]` | Opens race selection UI |
| `change` | `/race change <race> [--player <name>]` | Change race directly |
| `reset` | `/race reset [--player <name>]` | Reset race → opens **race UI only** |
| `info` | `/race info [--player <name>]` | Show current race info |
| `reload` | `/race reload` | Reload race configurations |

### `/class` — Class Management
| Subcommand | Usage | Description |
|------------|-------|-------------|
| `select` | `/class select [--player <name>]` | Opens class selection UI |
| `change` | `/class change <class> [--player <name>]` | Change class directly |
| `reset` | `/class reset [--player <name>]` | Reset class → opens **class UI only** |
| `info` | `/class info [--player <name>]` | Show current class info |

### `/build` — Combined Race + Class
| Subcommand | Usage | Description |
|------------|-------|-------------|
| `select` | `/build select [--player <name>]` | Opens race UI → flows to class UI |
| `change` | `/build change --race <race> [--class <class>] [--player <name>]` | Change both directly |
| `reset` | `/build reset [--player <name>]` | Reset both → opens **race UI → class UI** |
| `info` | `/build info [--player <name>]` | Show race + class info |

### `/language` — Language Management
| Subcommand | Usage | Description |
|------------|-------|-------------|
| `set` | `/language set <code>` | Set server language (en, pt_br, es, ru) |
| `list` | `/language list` | List all available languages |
| `current` | `/language current` | Show current language |

### Smart Reset Behavior

| Command | What Resets | UI Opened |
|---------|------------|-----------|
| `/race reset` | Race + Class | **Race UI only** (keeps previous class after selection) |
| `/class reset` | Class only | **Class UI only** |
| `/build reset` | Race + Class | **Race UI → Class UI** (full selection flow) |

***

## 🌐 Supported Languages

| Code | Language | Status |
|------|----------|--------|
| `en` | English | ✅ Complete |
| `pt_br` | Português (Brasil) | ✅ Complete |
| `es` | Español | ✅ Complete |
| `ru` | Русский | ✅ Complete |

Change with: `/language set <code>`

### Adding Custom Languages
1. Navigate to your world save: `mods/OrbisAndDungeons_RaceSelection/languages/`
2. Create a new `.json` file (e.g., `fr.json`)
3. Copy the structure from `en.json` and translate
4. Restart server or use `/race reload`
5. Select with `/language set fr`

***

## 🛠️ Installation

1. Download the **Orbis & Dungeons** JAR.
2. Place it in `UserData/Mods/` folder.
3. Start the server — configs are auto-generated on first run.
4. If updating from an older version, **delete** `races_config.json` and `classes_config.json` to regenerate with current stats.

### Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `mod_config.json` | World save `mods/` folder | Main mod settings, compatibility options |
| `races_config.json` | World save `mods/` folder | Race stats, resistances, descriptions |
| `classes_config.json` | World save `mods/` folder | Class stats, weapons, descriptions |
| `languages/*.json` | World save `mods/` folder | Translation files |
| `race_cache.txt` | World save `mods/` folder | File-backed persistence cache |

**Persistence:** This mod uses a dual persistence strategy — file-backed cache (`race_cache.txt`) + Hytale's component system. Your choices are safe even if the server crashes.

***

## ⚙️ Compatibility Mode

If you're using other mods that manage player stats (like **Endless Levelling**, **MMO Skilltree**, or **RPGLeveling**), you can configure Orbis to work alongside them.

### `mod_config.json` Options

```json
{
  "applyStatModifiers": true,      // Master toggle for all stat modifiers
  "applyHealthModifier": true,     // Toggle health bonuses specifically
  "applyStaminaModifier": true,    // Toggle stamina bonuses specifically  
  "applyManaModifier": true,       // Toggle mana bonuses specifically
  "statApplicationDelayMs": 500,   // Delay before applying stats (ms)
  "compatibilityMode": true,       // Enable compatibility with other mods
  "autoDetectStatMods": true,      // Auto-detect conflicting mods
  "applyWeaponDamageMultipliers": true,  // Weapon damage bonuses
  "applyDamageResistance": true,   // Damage resistance system
  "showRaceUiOnFirstJoin": true,   // Show UI on first join
  "healAfterStatApplication": true, // Heal player after applying stats
  "debugMode": false               // Enable debug logging
}
```

### For Users of Other Stat Mods

If you're using **Endless Levelling**, **MMO Skilltree**, **RPGLeveling**, or similar mods:

1. Open `mod_config.json` in your world save's `mods/OrbisAndDungeons_RaceSelection/` folder
2. Set `"applyStatModifiers": false` to disable health/stamina/mana modifiers
3. Keep `"applyWeaponDamageMultipliers": true` and `"applyDamageResistance": true` for weapon bonuses and resistances

This allows you to enjoy race/class identity (weapon bonuses, resistances) without conflicting with other mods that manage base stats.

***

## ❓ Troubleshooting

### Files Not Generated / Commands Don't Exist

If the mod doesn't seem to be loading:

1. **Check mod placement**: The JAR must be in `UserData/Mods/` folder
2. **Check server logs**: Look for `[Orbis] Starting Orbis and Dungeons mod...` message
3. **Dependencies**: This mod requires `Hytale:EntityModule` which is built-in
4. **Permissions**: Ensure the server has write permissions to create config files
5. **Server type**: This mod is for **dedicated servers**. Single-player mode uses a different mod loading system.

### Instant Death on Login

This can happen when stat mods conflict. To fix:

1. Open `mod_config.json`
2. Set `"healAfterStatApplication": true`
3. Set `"statApplicationDelayMs": 500` or higher
4. If still occurring, set `"applyStatModifiers": false`

### Stats Being Overwritten by Other Mods

If your health keeps getting reduced after teleporting or chunk reloading:

1. Open `mod_config.json`
2. Set `"compatibilityMode": true`
3. Set `"statApplicationDelayMs": 1000` (1 second delay)
4. If persistent, set `"applyHealthModifier": false` to let other mods handle health

### Commands Not Working

1. Check if you have operator permissions
2. Try `/help race` to see if command is registered
3. Check server logs for registration errors
4. Restart the server completely

***

## 📊 Quick Reference — Race + Class Combinations

| Combination | Total HP | Total Stamina | Weapon Bonus | Notes |
|-------------|----------|---------------|--------------|-------|
| Orc + Berserker | 175 | 16 | +30% Axe | Aggressive tank |
| Orc + Crusader | 230 | 8 | +15% Mace | Ultimate tank |
| Dwarf + Crusader | 180 | 10 | +15% Mace | Fortress |
| Human + Swordsman | 145 | 20 | +20% Sword | All-rounder |
| Elf + Assassin | 65 | 35 | +22% Dagger | Speed glass cannon |
| Elf + Archer | 65 | 33 | +40% Bow | Sniper |
| Tiefling + Mage | 45 | 30 | +25% Staff | Fire-immune spellcaster |
| Human + Mage | 95 | 27 | +25% Staff | Balanced mage |

***

**Choose wisely — your selection defines your adventure!** ⚔️🏹⚖️

