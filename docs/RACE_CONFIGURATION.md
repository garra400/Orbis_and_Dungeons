# Orbis and Dungeons - Race Configuration System

## Overview

This mod now supports **JSON-based race configuration**, allowing server administrators and players to customize race stats and bonuses without recompiling the mod!

## Configuration File Location

The configuration file is automatically created at:
```
%APPDATA%\Roaming\Hytale\UserData\Saves\mods\mods\Orbis_and_Dungeons\races_config.json
```

(or your server's equivalent data directory)

## Configuration Format

The file contains a JSON array of race configurations. Each race has the following properties:

```json
{
  "id": "elf",
  "displayName": "Elf",
  "tagline": "Agile and tireless, moves like the wind.",
  "healthBonus": 0.0,
  "staminaBonus": 15.0,
  "strengths": [
    "High stamina pool",
    "Extended mobility",
    "Fast movement"
  ],
  "weaknesses": [
    "Lower base health",
    "Requires stamina management"
  ],
  "weapons": []
}
```

### Property Descriptions

- **id**: Unique identifier for the race (lowercase, no spaces)
- **displayName**: Name shown in UI and messages
- **tagline**: Short description (one line)
- **healthBonus**: Bonus HP added to base health (can be negative)
- **staminaBonus**: Bonus stamina added to base stamina (can be negative)
- **strengths**: Array of positive traits/characteristics
- **weaknesses**: Array of negative traits/drawbacks
- **weapons**: Array of weapon configurations (see below)

### Weapon Configuration

Weapon bonuses are defined using this format:

```json
"weapons": [
  {
    "types": ["sword"],
    "damageMultiplier": 1.20
  }
]
```

- **types**: Array of weapon type keywords (e.g., "sword", "axe", "bow", "dagger", "mace", "hammer")
- **damageMultiplier**: Damage multiplier (1.0 = normal, 1.20 = +20%, 1.50 = +50%)

The system matches weapon IDs/animations that contain any of the specified keywords.

## Example Race: Berserker

```json
{
  "id": "berserker",
  "displayName": "Berserker",
  "tagline": "Reckless warrior who trades defense for raw power.",
  "healthBonus": -25.0,
  "staminaBonus": 8.0,
  "strengths": [
    "+30% axe damage",
    "High burst damage",
    "Aggressive playstyle"
  ],
  "weaknesses": [
    "Very low health",
    "Glass cannon",
    "High risk gameplay"
  ],
  "weapons": [
    {
      "types": ["axe"],
      "damageMultiplier": 1.30
    }
  ]
}
```

## Balancing Guidelines

### Health and Stamina
- Base values: Human (100 HP, 10 Stamina)
- Healthy range: 65-175 HP, 10-25 Stamina
- Total stat pool: ~110-185 (HP + Stamina + Damage×100)

### Damage Multipliers
- Conservative: 1.15 - 1.20 (+15-20%)
- Moderate: 1.25 - 1.35 (+25-35%)
- Aggressive: 1.40+ (+40%+)
- Balance rule: Higher damage = lower health/stamina

### Design Principles
1. **Trade-offs**: High damage should cost survivability
2. **Specialization**: Weapon bonuses should be meaningful but not mandatory
3. **Versatility**: Base races (no weapon bonuses) should remain viable with higher stats
4. **Identity**: Each race should have a clear playstyle

## Reloading Configuration

After editing the JSON file, use the command:
```
/racereload
```

This command:
- Requires OP/admin permissions
- Reloads the config file without restarting the server
- Updates all race stats immediately
- Existing players keep their selected race but get the new stats on next application

## Creating New Races

1. Add a new entry to the JSON array
2. Give it a unique `id`
3. Define stats and weapon bonuses
4. Run `/racereload` command
5. New race will appear in the selection UI automatically

## Removing Races

Simply delete the race entry from the JSON array and run `/racereload`. Players who had that race selected will need to choose a new one.

## Troubleshooting

**Config not loading:**
- Check JSON syntax (use a JSON validator)
- Ensure file is in the correct location
- Check server console for error messages

**Changes not applying:**
- Run `/racereload` after editing
- Make sure you're editing the right file (check data directory path)
- Verify JSON is valid

**Weapon bonuses not working:**
- Check that weapon types match item IDs (e.g., "sword", not "blade")
- Try multiple keywords: `["sword", "blade"]`
- Test with different weapons to identify the correct keyword

## Default Configuration

On first run, the mod generates a default configuration with 8 races:
- Elf (high stamina, mobility focus)
- Orc (massive HP, tank)
- Human (balanced, jack-of-all-trades)
- Berserker (low HP, +30% axe damage)
- Swordsman (balanced, +20% sword damage)
- Crusader (high HP, +15% mace/hammer damage)
- Assassin (low HP, high stamina, +35% dagger damage)
- Archer (very low HP, +40% bow/crossbow damage)

Feel free to modify any of these or create your own!
