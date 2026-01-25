# Damage Resistance System Guide

## Overview

The damage resistance system allows races and classes to have immunity, resistance, or weakness to specific damage types. This feature is inspired by **OrbisOrigins** and uses the same resistance mechanics.

---

## How It Works

### Resistance Values

Resistances are defined as **multipliers** applied to incoming damage:

- **`0.0`** = **Immune** (100% damage reduction)
- **`0.1-0.9`** = **Resistance** (10-90% damage reduction)
- **`1.0`** = **Normal** (no change)
- **`1.1-2.0`** = **Weakness** (10-100% additional damage)

### Damage Calculation

When a player takes damage:
1. The system checks the player's **race** resistances
2. The system checks the player's **class** resistances
3. **The best (lowest) resistance applies**

**Example:**
- Race has Fire resistance: `0.5` (50% reduction)
- Class has Fire resistance: `0.2` (80% reduction)
- **Final resistance: `0.2`** (best of both)

---

## Damage Types

The following damage types are available in Hytale:

### Common Types
- **`Fire`** - Fire damage (torches, lava contact)
- **`Lava`** - Lava damage (submersion)
- **`Physical`** - Physical/melee damage
- **`Fall`** - Fall damage
- **`Drowning`** - Drowning damage
- **`Magic`** - Magical damage
- **`Poison`** - Poison damage
- **`Lightning`** - Lightning/electric damage
- **`Cold`** - Cold/frost damage
- **`Nature`** - Nature/plant-based damage
- **`Void`** - Void/shadow damage

**Note:** Damage types are case-sensitive and must match exactly. Use `DamageCause.getAssetMap().keySet()` to discover available types at runtime.

---

## Configuration

### Race Resistances

Add `damageResistances` to your race config in **races_config.json**:

```json
{
  "id": "tiefling",
  "displayName": "Tiefling",
  "tagline": "Demon-touched bloodline, born of fire.",
  "healthBonus": -15.0,
  "staminaBonus": 12.0,
  "strengths": [
    "85 HP (-15)",
    "22 Stamina (+12)",
    "Immune to Fire Damage",
    "Immune to Lava Damage"
  ],
  "weaknesses": [
    "Fragile physique",
    "Vulnerable to Magic (+50%)"
  ],
  "weapons": [],
  "damageResistances": {
    "Fire": 0.0,
    "Lava": 0.0,
    "Magic": 1.5
  }
}
```

### Class Resistances

Add `damageResistances` to your class config in **classes_config.json**:

```json
{
  "id": "paladin",
  "displayName": "Paladin",
  "tagline": "Holy warrior blessed by the light.",
  "healthModifier": 25.0,
  "staminaModifier": 3.0,
  "strengths": [
    "125 HP (+25)",
    "Resistant to Magic (50%)",
    "Holy champion"
  ],
  "weaknesses": [
    "Slower playstyle",
    "Vulnerable to Shadow damage"
  ],
  "weapons": [
    {"types": ["mace", "hammer"], "damageMultiplier": 1.18}
  ],
  "damageResistances": {
    "Magic": 0.5,
    "Poison": 0.3,
    "Void": 1.3
  }
}
```

---

## Default Races

### Elf
- **Resistances:** None
- **Playstyle:** Relies on mobility

### Orc
- **Resistances:** None
- **Playstyle:** Relies on high HP

### Human
- **Resistances:** None
- **Playstyle:** Balanced baseline

### Tiefling (NEW!)
- **Fire:** 0.0 (Immune)
- **Lava:** 0.0 (Immune)
- **Magic:** 1.5 (+50% damage)
- **Playstyle:** High-risk fire tank

---

## Balance Guidelines

### Immunity (0.0)
- Very powerful - use sparingly
- Should have significant drawbacks
- Example: Fire immunity → Magic weakness

### Strong Resistance (0.2-0.5)
- 50-80% damage reduction
- Suitable for tank classes
- Example: Paladin with 50% magic resistance

### Moderate Resistance (0.6-0.8)
- 20-40% damage reduction
- General defensive bonus
- Example: Nature resistance for forest races

### Weakness (1.1-1.5)
- 10-50% additional damage
- Balances strong resistances
- Example: Undead vulnerable to holy damage

### Major Weakness (1.6-2.0)
- 60-100% additional damage
- Extreme vulnerability
- Use for critical weaknesses only

---

## Examples

### Fire-Immune Tank (Tiefling)
```json
"damageResistances": {
  "Fire": 0.0,    // Immune to fire
  "Lava": 0.0,    // Immune to lava
  "Magic": 1.5    // +50% magic damage
}
```

**Balance:**
- Can stand in fire/lava without taking damage
- EHP = 85 HP + (12 × 5) = 145 (balanced for utility)
- Magic weakness prevents abuse

### Undead Race
```json
"damageResistances": {
  "Poison": 0.0,     // Immune to poison
  "Drowning": 0.0,   // Don't need to breathe
  "Magic": 0.5,      // 50% magic resistance
  "Fire": 2.0        // Double fire damage
}
```

### Elemental Mage Class
```json
"damageResistances": {
  "Fire": 0.4,       // 60% fire resistance
  "Cold": 0.4,       // 60% cold resistance
  "Lightning": 0.4,  // 60% lightning resistance
  "Physical": 1.3    // +30% physical damage
}
```

---

## Testing

### In-Game Testing
1. Select race/class with resistances
2. Test damage from various sources:
   - Stand in fire (Fire damage)
   - Jump into lava (Lava damage)
   - Get hit by mobs (Physical damage)
   - Fall from heights (Fall damage)

### Debug Commands
```
/raceinfo - Shows your current race and class
```

### Expected Behavior
- **0.0 resistance:** Take 0 damage (immune)
- **0.5 resistance:** Take 50% of normal damage
- **1.5 weakness:** Take 150% of normal damage

---

## Technical Details

### System Architecture

**RaceDamageResistanceSystem:**
- Runs in `FilterDamageGroup`
- Executes AFTER `GatherDamageGroup`
- Executes BEFORE `InspectDamageGroup`
- Modifies damage amount before armor reduction

**Resistance Combination:**
- Race and class resistances are independent
- **Best (lowest) resistance wins**
- Example: Race 0.5, Class 0.3 → Final 0.3

### Code Reference

**RaceManager.getDamageResistance():**
```java
public static float getDamageResistance(Player player, String damageType) {
    float raceResistance = 1.0f;
    float classResistance = 1.0f;
    
    // Get best resistance
    return Math.min(raceResistance, classResistance);
}
```

**RaceDamageResistanceSystem.handle():**
```java
float resistance = RaceManager.getDamageResistance(player, damageType);
float newDamage = currentDamage * resistance;
damage.setAmount(newDamage);
```

---

## Common Patterns

### Tank Race
```json
"damageResistances": {
  "Physical": 0.8,  // 20% physical reduction
  "Fall": 0.5       // 50% fall reduction
}
```

### Glass Cannon
```json
"damageResistances": {
  "Physical": 1.2,  // +20% physical damage taken
  "Magic": 0.7      // 30% magic reduction
}
```

### Elemental Specialist
```json
"damageResistances": {
  "Fire": 0.0,      // Fire immune
  "Cold": 2.0,      // Double cold damage
  "Lightning": 0.3  // 70% lightning reduction
}
```

---

## Troubleshooting

### Resistance Not Working
1. Check damage type ID is **exact** (case-sensitive)
2. Verify value is between `0.0` and `2.0`
3. Check system is registered in RaceMod
4. Reload configs with `/racereload`

### Multiple Resistances
If both race and class have the same resistance, the **better one applies**.

### Negative Values
Invalid - will be treated as `0.0` (immune)

---

## Credits

Damage resistance system inspired by **OrbisOrigins** by Hexvane.
