# Race and Class System Guide

## Overview

The mod uses a **two-step selection system** that separates innate racial traits from combat specializations:

1. **Races** - Innate biological traits (health, stamina, natural abilities)
2. **Classes** - Combat specializations (weapon bonuses, playstyle modifiers)

This design allows players to create unique combinations like:
- **Elf Crusader** - Agile tank with stamina for mobility
- **Orc Archer** - Unconventional ranged bruiser
- **Human Berserker** - Balanced high-damage warrior

---

## Configuration Files

### races_config.json

Located at: `%APPDATA%\Roaming\Hytale\UserData\Saves\[World]\mods\OrbisAndDungeons_RaceSelection\races_config.json`

Defines base racial traits:

```json
{
  "id": "orc",
  "displayName": "Orc",
  "tagline": "Powerful and robust",
  "healthBonus": 75.0,
  "staminaBonus": 0.0,
  "strengths": [
    "175 HP (+75)",
    "Powerful physique",
    "Melee combat specialist"
  ],
  "weaknesses": [
    "110 Stamina (+0)",
    "Slow stamina regeneration"
  ],
  "weapons": [],
  "damageResistances": {}
}
```

**Note:** Races no longer have weapon specializations. All weapon bonuses are handled by classes.

**New in v2026.1.27:** Races can now have `damageResistances` (see [DAMAGE_RESISTANCE_GUIDE.md](DAMAGE_RESISTANCE_GUIDE.md)).

### classes_config.json

Located at: `%APPDATA%\Roaming\Hytale\UserData\Saves\[World]\mods\OrbisAndDungeons_RaceSelection\classes_config.json`

Defines combat specializations:

```json
{
  "id": "berserker",
  "displayName": "Berserker",
  "tagline": "Devastating melee warrior",
  "healthModifier": -25.0,
  "staminaModifier": 8.0,
  "strengths": [
    "Devastating axe attacks (+30% damage)",
    "+8 Stamina bonus",
    "High mobility in combat"
  ],
  "weaknesses": [
    "-25 Health penalty",
    "High-risk playstyle"
  ],
  "weapons": [
    {
      "types": ["axe", "battleaxe"],
      "damageMultiplier": 1.3
    }
  ],
  "damageResistances": {}
}
```

**New in v2026.1.27:** Classes can now have `damageResistances` (see [DAMAGE_RESISTANCE_GUIDE.md](DAMAGE_RESISTANCE_GUIDE.md)).

---

## How Stats Combine

The system **adds** race bonuses and class modifiers together:

### Example 1: Orc Berserker
- Base: 100 HP, 110 Stamina
- Orc: +75 HP, +0 Stamina
- Berserker: -25 HP, +8 Stamina
- **Final: 150 HP, 118 Stamina**

### Example 2: Elf Archer
- Base: 100 HP, 110 Stamina
- Elf: +0 HP, +15 Stamina
- Archer: -35 HP, +8 Stamina
- **Final: 65 HP, 133 Stamina**

### Example 3: Human Swordsman
- Base: 100 HP, 110 Stamina
- Human: +35 HP, +5 Stamina
- Swordsman: +10 HP, +5 Stamina
- **Final: 145 HP, 120 Stamina**

---

## Player Experience

### Step 1: Race Selection

When a new player joins, they see:

```
┌─────────────────────────────────┐
│  CHOOSE YOUR RACE               │
│                                 │
│  [Elf]                          │
│  Agile and energetic            │
│  ✓ 125 Stamina (+15)            │
│  ✗ 100 HP (+0)                  │
│                                 │
│  [Orc]                          │
│  Powerful and robust            │
│  ✓ 175 HP (+75)                 │
│  ✗ 110 Stamina (+0)             │
│                                 │
│  [Human]                        │
│  Balanced and versatile         │
│  ✓ 135 HP (+35)                 │
│  ✓ 115 Stamina (+5)             │
│                                 │
│  [Confirm Selection]            │
└─────────────────────────────────┘
```

### Step 2: Class Selection

After choosing a race, players select their class:

```
┌─────────────────────────────────┐
│  CHOOSE YOUR CLASS              │
│                                 │
│  [None]                         │
│  Pure racial traits             │
│  ✓ No restrictions              │
│  ✗ No weapon specialization     │
│                                 │
│  [Berserker]                    │
│  Devastating melee warrior      │
│  ✓ +30% axe damage              │
│  ✗ -25 Health penalty           │
│                                 │
│  [Swordsman]                    │
│  Balanced fighter               │
│  ✓ +20% sword damage            │
│  ✓ +10 HP, +5 Stamina           │
│                                 │
│  [Back] [Confirm Selection]     │
└─────────────────────────────────┘
```

---

## Default Races

### Elf
- **Health Bonus**: 0
- **Stamina Bonus**: +15
- **Strengths**: High mobility, endurance specialist
- **Weaknesses**: Fragile, requires skill to survive
- **Playstyle**: Hit-and-run, evasion-focused

### Orc
- **Health Bonus**: +75
- **Stamina Bonus**: 0
- **Strengths**: Tank, can take massive punishment
- **Weaknesses**: Low mobility, slow stamina regen
- **Playstyle**: Frontline bruiser, sustained combat

### Human
- **Health Bonus**: +35
- **Stamina Bonus**: +5
- **Strengths**: Adaptable, no major weaknesses
- **Weaknesses**: No specialization, average in all areas
- **Playstyle**: Jack-of-all-trades, flexible strategy

### Tiefling (NEW!)
- **Health Bonus**: -15
- **Stamina Bonus**: +12
- **Resistances**: Fire (0.0), Lava (0.0), Magic (1.5)
- **Strengths**: Immune to fire/lava, high mobility
- **Weaknesses**: Fragile, vulnerable to magic (+50%)
- **Playstyle**: Fire-immune mobility specialist, dominates lava zones

---

## Default Classes

### None
- **Modifiers**: 0 HP, 0 Stamina
- **Weapons**: None
- **Playstyle**: Pure racial traits, versatile

### Berserker
- **Modifiers**: -25 HP, +8 Stamina
- **Weapons**: Axes/Battleaxes (+30% damage)
- **Playstyle**: Glass cannon, high-risk high-reward

### Swordsman
- **Modifiers**: +10 HP, +5 Stamina
- **Weapons**: Swords (+20% damage)
- **Playstyle**: Balanced melee fighter

### Crusader
- **Modifiers**: +30 HP, 0 Stamina
- **Weapons**: Maces/Hammers (+15% damage)
- **Playstyle**: Tanky frontliner with moderate damage

### Assassin
- **Modifiers**: -20 HP, +10 Stamina
- **Weapons**: Daggers (+35% damage)
- **Playstyle**: High mobility, burst damage

### Archer
- **Modifiers**: -35 HP, +8 Stamina
- **Weapons**: Bows/Crossbows (+40% damage)
- **Playstyle**: Ranged specialist, stay at distance

---

## Creating Custom Races

### Step 1: Edit races_config.json

Add a new entry to the JSON array:

```json
{
  "id": "dwarf",
  "displayName": "Dwarf",
  "tagline": "Short but mighty",
  "healthBonus": 50.0,
  "staminaBonus": -5.0,
  "strengths": [
    "150 HP (+50)",
    "Resistant to knockback",
    "Mining specialist"
  ],
  "weaknesses": [
    "105 Stamina (-5)",
    "Slow movement speed",
    "Short reach"
  ],
  "weapons": []
}
```

### Step 2: Reload Configuration

Run the command in-game:
```
/racereload
```

The new race will immediately appear in the selection UI!

---

## Creating Custom Classes

### Step 1: Edit classes_config.json

Add a new entry:

```json
{
  "id": "spellblade",
  "displayName": "Spellblade",
  "tagline": "Magic-infused warrior",
  "healthModifier": 0.0,
  "staminaModifier": 15.0,
  "strengths": [
    "+15 Stamina for spellcasting",
    "Hybrid magic-melee playstyle",
    "+25% staff damage"
  ],
  "weaknesses": [
    "No health bonus",
    "Requires mana management"
  ],
  "weapons": [
    {
      "types": ["staff", "wand"],
      "damageMultiplier": 1.25
    }
  ]
}
```

### Step 2: Reload Configuration

Run `/racereload` and the new class appears in the selection UI!

---

## Weapon Type Matching

Weapon types are matched by **substring** in the item ID:

```json
"types": ["axe", "battleaxe"]
```

This will match:
- `hytale:iron_axe`
- `hytale:steel_battleaxe`
- `custom_mod:legendary_axe`

**Tips:**
- Use lowercase for weapon type names
- Use partial matches (e.g., "sword" matches "longsword", "broadsword")
- Multiple weapon types can share the same multiplier

---

## Balancing Guide

### Health vs Stamina Trade-offs

**Health** (Base: 100)
- Determines how much damage you can take
- Critical for frontline fighters
- Low HP = high-risk playstyle

**Stamina** (Base: 110)
- Used for sprinting, dodging, special abilities
- Critical for mobile/evasive playstyles
- Low stamina = limited mobility

### Recommended Balance

**Tank Classes:**
- High HP (+30 to +50)
- Low/neutral stamina (0 to +5)
- Moderate weapon damage (+10% to +20%)

**Glass Cannon Classes:**
- Low HP (-20 to -40)
- Moderate stamina (+5 to +10)
- High weapon damage (+30% to +50%)

**Balanced Classes:**
- Moderate HP (+5 to +20)
- Moderate stamina (+3 to +8)
- Moderate damage (+15% to +25%)

**Mobility Classes:**
- Low HP (-15 to -30)
- High stamina (+10 to +20)
- Variable damage (depends on playstyle)

---

## File Storage Format

### race_cache.txt

Player selections are cached in:
```
uuid|username|raceId|classId
550e8400-e29b-41d4-a716-446655440000|PlayerName|orc|berserker
```

This file auto-generates and updates when players make selections.

### Component Persistence

The mod uses Hytale's component system to persist race and class across:
- Server restarts
- World saves/loads
- Player reconnections

Data is stored in the world save files automatically.

---

## Common Customizations

### Make Everyone Start with Same Stats
Set all races to 0 bonuses, use only class modifiers:

```json
{"id": "human", "healthBonus": 0, "staminaBonus": 0}
{"id": "elf", "healthBonus": 0, "staminaBonus": 0}
{"id": "orc", "healthBonus": 0, "staminaBonus": 0}
```

### Create High-HP Server
Multiply all health values by 2:

```json
{"id": "orc", "healthBonus": 150.0}
{"id": "berserker", "healthModifier": -50.0}
```

### Remove Weapon Bonuses
Set all damage multipliers to 1.0:

```json
{"damageMultiplier": 1.0}
```

### Add New Weapon Types
Define custom weapon categories:

```json
"weapons": [
  {"types": ["katana", "ninjato"], "damageMultiplier": 1.4},
  {"types": ["shuriken"], "damageMultiplier": 1.2}
]
```

---

## Troubleshooting

### Changes Not Appearing
1. Save the JSON file
2. Run `/racereload` in-game
3. Reopen the selection UI if it's already open

### JSON Syntax Errors
- Ensure all commas are in the right places
- Don't add comma after last item in array/object
- Use double quotes for strings
- Numbers don't need quotes

### Race/Class Not Showing
- Check that `id` field is unique
- Verify JSON is valid (use JSONLint.com)
- Check console for error messages

### Weapon Bonus Not Working
- Verify weapon type string matches item ID
- Use lowercase for weapon types
- Check that player has selected the class (use `/raceinfo`)

---

## Migration from Old System

### Old System (Single JSON)
Previously, races included weapon specializations:
```json
{"id": "berserker_orc", "healthBonus": 50, "weapons": [...]}
```

### New System (Separated)
Now split into race + class:
```json
// races_config.json
{"id": "orc", "healthBonus": 75, "weapons": []}

// classes_config.json  
{"id": "berserker", "healthModifier": -25, "weapons": [...]}
```

### Player Data
Existing players will need to reselect their race and class. The old `race_cache.txt` format is backward-compatible and will auto-migrate to the new format.

---

## Advanced Examples

### Creating a "Pure Mage" Class

```json
{
  "id": "mage",
  "displayName": "Mage",
  "tagline": "Master of arcane arts",
  "healthModifier": -40.0,
  "staminaModifier": 25.0,
  "strengths": [
    "Massive stamina pool for spellcasting",
    "+50% staff damage",
    "Elemental specialist"
  ],
  "weaknesses": [
    "Very fragile (-40 HP)",
    "Useless in melee combat",
    "Requires distance"
  ],
  "weapons": [
    {
      "types": ["staff", "wand", "tome"],
      "damageMultiplier": 1.5
    }
  ]
}
```

### Creating a "Paladin" Class

```json
{
  "id": "paladin",
  "displayName": "Paladin",
  "tagline": "Holy warrior",
  "healthModifier": 20.0,
  "staminaModifier": 5.0,
  "strengths": [
    "Blessed armor (+20 HP)",
    "Divine protection",
    "+20% mace damage"
  ],
  "weaknesses": [
    "Moderate damage output",
    "No ranged options"
  ],
  "weapons": [
    {
      "types": ["mace", "hammer"],
      "damageMultiplier": 1.2
    }
  ]
}
```

### Creating an "Elemental" Race

```json
{
  "id": "elemental",
  "displayName": "Elemental",
  "tagline": "Being of pure energy",
  "healthBonus": -30.0,
  "staminaBonus": 40.0,
  "strengths": [
    "70 HP (-30)",
    "150 Stamina (+40)",
    "Infinite energy regeneration",
    "Immune to environmental damage"
  ],
  "weaknesses": [
    "Extremely fragile",
    "Vulnerable to physical damage",
    "Requires constant movement"
  ],
  "weapons": []
}
```

---

## Technical Notes

### Stat Application Order

1. Player spawns with base stats (100 HP, 110 Stamina)
2. Race bonuses are applied (`healthBonus`, `staminaBonus`)
3. Class modifiers are added (`healthModifier`, `staminaModifier`)
4. Final stats are calculated and applied to player

### Damage Calculation

Weapon damage is checked in this order:
1. Get player's selected class
2. Check if current weapon matches any weapon type in class config
3. Apply damage multiplier if match found
4. Base damage is used if no match

### Persistence

Both race and class selections are stored in:
- **Component System**: `RaceData` component attached to player
- **File Cache**: `race_cache.txt` for quick lookups
- **World Save**: Automatically saved with world data

---

## Commands

### `/racereload`
Reloads both `races_config.json` and `classes_config.json` from disk without restarting the server.

### `/raceinfo [player]`
Displays current race and class selection with timestamp.

Example output:
```
Race: Orc - Berserker (selected 3 days ago on 2026-01-22 14:30:00)
```

### `/racereset [player]`
Resets race and class selection, reopening the selection UI.

---

## Best Practices

### For Server Admins

1. **Test Changes**: Create a test world before applying to production
2. **Backup Configs**: Keep copies of working configurations
3. **Document Custom Races**: Tell players what custom races/classes do
4. **Balance Testing**: Play with different combinations to ensure fairness
5. **Gradual Changes**: Don't change too many values at once

### For Players

1. **Read Descriptions**: Strengths and weaknesses explain the playstyle
2. **Experiment**: Try different race/class combos to find your style
3. **Check Weapon Types**: Make sure you use the right weapons for your class
4. **Don't Rush**: Take time to read the stats before confirming

---

## FAQ

**Q: Can I change my race/class after selection?**  
A: Yes, use `/racereset` command.

**Q: Can I have multiple weapon types in one class?**  
A: Yes, add multiple weapon configs to the `weapons` array.

**Q: What happens if I set health to -100?**  
A: Players would spawn with 0 HP and die instantly. Don't do this.

**Q: Can classes have different multipliers for different weapons?**  
A: Yes! Add multiple weapon configs with different multipliers.

Example:
```json
"weapons": [
  {"types": ["sword"], "damageMultiplier": 1.3},
  {"types": ["dagger"], "damageMultiplier": 1.5}
]
```

**Q: Do I need to restart the server after editing JSON files?**  
A: No! Just run `/racereload` in-game.

**Q: Can I remove default races/classes?**  
A: Yes, simply delete entries from the JSON files and reload.

**Q: What's the maximum number of races/classes?**  
A: Unlimited! The UI uses pagination to handle any number.

---

## Version History

- **2026.1.26** - Separated race and class selection into two-step system
- **2026.1.25** - Initial JSON configuration system
- **2026.1.24** - Added weapon-specialized classes
