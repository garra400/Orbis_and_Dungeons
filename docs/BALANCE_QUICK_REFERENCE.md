# Quick Balance Reference

## Balance Formula

```
EHP = 100 + Health Bonus + (Stamina Bonus × 5)
```

**1 Stamina = 5 Health**

---

## Rebalanced Assassin (v2026.1.26.1)

### Changes
- Health: **-20 → -35** (same as Archer)
- Damage: **+35% → +22%** (reduced ~37%)
- Stamina: **+10** (unchanged)

### Why?
- Daggers have built-in safety (charged attack mobility)
- Old values: 205 EHP (overpowered)
- New values: 190 EHP (balanced)

---

## Quick Class Reference

| Class | HP | Stam | EHP | Dmg | Weapons |
|-------|-----|------|-----|-----|---------|
| None | 0 | 0 | 100 | 0% | All |
| Berserker | -25 | +8 | 115 | +30% | Axe |
| Swordsman | +10 | +5 | 135 | +20% | Sword |
| Crusader | +30 | 0 | 130 | +15% | Mace/Hammer |
| **Assassin** | **-35** | **+10** | **115** | **+22%** | **Dagger** |
| Archer | -35 | +8 | 105 | +40% | Bow/Crossbow |

---

## Race + Class Examples

### Elf Assassin (FIXED)
- **Old:** 65 HP, 135 Stamina, 205 EHP, +35% damage → OVERPOWERED
- **New:** 65 HP, 135 Stamina, 190 EHP, +22% damage → BALANCED

### Orc Berserker
- 150 HP, 118 Stamina, 190 EHP, +30% damage → BALANCED

### Human Swordsman
- 145 HP, 120 Stamina, 195 EHP, +20% damage → BALANCED

---

## How to Apply Changes

### Option 1: Auto-Generate (Recommended)
1. Delete `classes_config.json`
2. Restart server (or run `/racereload`)
3. New balanced values generate automatically

### Option 2: Manual Edit
1. Open `classes_config.json`
2. Find assassin entry
3. Change:
   ```json
   "healthModifier": -35.0,
   "damageMultiplier": 1.22
   ```
4. Save and run `/racereload`

---

## Files

- [BALANCE_GUIDE.md](BALANCE_GUIDE.md) - Complete balance theory
- [balance_reference.json](balance_reference.json) - JSON examples and presets
- [RACE_CLASS_SYSTEM.md](RACE_CLASS_SYSTEM.md) - System documentation
