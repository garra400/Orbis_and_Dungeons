# Quick Reference - Two-Step Selection System

## System Flow

```
Player Joins
    ↓
Race Selection UI (Step 1)
    ↓ [Confirm]
Class Selection UI (Step 2)
    ↓ [Confirm]
Stats Applied (Race + Class)
    ↓
Game Starts
```

## Stat Calculation

```
Final HP = 100 + Race.healthBonus + Class.healthModifier
Final Stamina = 110 + Race.staminaBonus + Class.staminaModifier
```

## Example Combinations

| Race  | Class     | Final HP | Final Stamina | Weapon Bonus          |
|-------|-----------|----------|---------------|-----------------------|
| Elf   | None      | 100      | 125           | None                  |
| Elf   | Archer    | 65       | 133           | +40% bow/crossbow     |
| Orc   | Berserker | 150      | 118           | +30% axe/battleaxe    |
| Orc   | Crusader  | 205      | 110           | +15% mace/hammer      |
| Human | Swordsman | 145      | 120           | +20% sword            |
| Human | Assassin  | 115      | 125           | +35% dagger           |

## Configuration Files

### races_config.json
```json
{
  "id": "race_id",
  "displayName": "Display Name",
  "tagline": "Short description",
  "healthBonus": 0.0,
  "staminaBonus": 0.0,
  "strengths": ["List", "of", "positives"],
  "weaknesses": ["List", "of", "negatives"],
  "weapons": []
}
```

### classes_config.json
```json
{
  "id": "class_id",
  "displayName": "Display Name",
  "tagline": "Short description",
  "healthModifier": 0.0,
  "staminaModifier": 0.0,
  "strengths": ["List", "of", "positives"],
  "weaknesses": ["List", "of", "negatives"],
  "weapons": [
    {
      "types": ["weapon", "type"],
      "damageMultiplier": 1.0
    }
  ]
}
```

## Key Differences

| Aspect         | Race                  | Class                    |
|----------------|-----------------------|--------------------------|
| **Purpose**    | Innate traits         | Combat specialization    |
| **Stats**      | Base bonuses          | Modifiers                |
| **Weapons**    | None (removed)        | Weapon-specific bonuses  |
| **Selection**  | Step 1 (first)        | Step 2 (after race)      |
| **Examples**   | Elf, Orc, Human       | Berserker, Archer, Mage  |

## Commands Quick Reference

| Command             | Description                           |
|---------------------|---------------------------------------|
| `/racereload`       | Reload JSON configs                   |
| `/raceinfo`         | Show your race and class              |
| `/racereset`        | Reset and reselect race/class         |

## Default Values

### Base Stats (No Selection)
- Health: 100
- Stamina: 110

### Race Bonuses
- Elf: 0 HP, +15 Stamina
- Orc: +75 HP, 0 Stamina
- Human: +35 HP, +5 Stamina

### Class Modifiers
- None: 0 HP, 0 Stamina, no weapons
- Berserker: -25 HP, +8 Stamina, +30% axe
- Swordsman: +10 HP, +5 Stamina, +20% sword
- Crusader: +30 HP, 0 Stamina, +15% mace/hammer
- Assassin: -20 HP, +10 Stamina, +35% dagger
- Archer: -35 HP, +8 Stamina, +40% bow/crossbow

## Extreme Combinations

### Maximum HP: Orc + Crusader
```
100 + 75 + 30 = 205 HP
110 + 0 + 0 = 110 Stamina
```

### Maximum Stamina: Elf + Assassin
```
100 + 0 + (-20) = 80 HP
110 + 15 + 10 = 135 Stamina
```

### Glass Cannon: Elf + Archer
```
100 + 0 + (-35) = 65 HP
110 + 15 + 8 = 133 Stamina
+40% bow/crossbow damage
```

### Ultimate Tank: Orc + Crusader
```
100 + 75 + 30 = 205 HP
110 + 0 + 0 = 110 Stamina
+15% mace/hammer damage
```

## File Locations

```
%APPDATA%\Roaming\Hytale\UserData\Saves\[World]\mods\OrbisAndDungeons_RaceSelection\
├── races_config.json      (Edit race traits)
├── classes_config.json    (Edit class specializations)
└── race_cache.txt         (Auto-generated, don't edit)
```

## Backup Your Configs!

Before making major changes, copy your JSON files:
```
races_config.json → races_config.json.backup
classes_config.json → classes_config.json.backup
```

---

**Documentation Version**: 2026.1.26  
**Mod Version**: 2026.1.26
