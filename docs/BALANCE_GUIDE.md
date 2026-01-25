# Race & Class Balancing Guide

## Core Balancing Principle

### The Golden Ratio: **1 Stamina = 5 Health**

This is the fundamental balance equation for the mod:
- **+5 Health** has the same value as **+1 Stamina**
- **-10 Health** penalty should grant **+2 Stamina** to be fair

**Why this ratio?**
- Health determines raw survivability
- Stamina enables mobility, dodging, and sustained combat
- Testing shows 1:5 ratio creates fair trade-offs between tank and mobile playstyles

---

## Effective Health Points (EHP)

Use this formula to calculate the **true power** of any class/race:

```
EHP = Base HP + Health Bonus + (Stamina Bonus × 5)
```

**Examples:**

**Orc Berserker:**
- Base: 100 HP
- Orc: +75 HP
- Berserker: -25 HP, +8 Stamina
- **EHP = 100 + 75 - 25 + (8 × 5) = 190 EHP**

**Elf Assassin (OLD - UNBALANCED):**
- Base: 100 HP
- Elf: +0 HP, +15 Stamina
- Assassin: -20 HP, +10 Stamina
- **EHP = 100 + 0 - 20 + (25 × 5) = 205 EHP** ← TOO HIGH!

**Elf Assassin (NEW - BALANCED):**
- Base: 100 HP
- Elf: +0 HP, +15 Stamina
- Assassin: -35 HP, +10 Stamina
- **EHP = 100 + 0 - 35 + (25 × 5) = 190 EHP** ← Much better!

---

## Weapon Damage Multipliers

### Damage vs Safety Trade-off

Weapon damage bonuses should reflect the **risk** of using that weapon type:

| Weapon Type | Risk Level | Recommended Multiplier |
|-------------|------------|------------------------|
| **Mace/Hammer** | High (slow, heavy) | 1.15 - 1.20 |
| **Sword** | Medium (balanced) | 1.20 - 1.25 |
| **Axe** | High (slow, two-handed) | 1.25 - 1.30 |
| **Dagger** | LOW (fast, hit-n-run) | 1.20 - 1.25 |
| **Bow/Crossbow** | LOW (ranged, safe) | 1.30 - 1.40 |

### Why Daggers Should Have Lower Multipliers

**The Problem:**
- Daggers have **built-in safety** via charged attack mechanics
- Charged attacks deal massive damage with the "Hit n' Run" mobility
- Players can spam charged attacks with high stamina
- **35% damage boost** on already-safe weapons = broken balance

**The Solution:**
- Reduce dagger multiplier to **1.22 - 1.25** (22-25%)
- Increase health penalty to **-35 HP** (same as Archer)
- Keep stamina bonus at +10 (enables the hit-n-run playstyle)

---

## Balanced Class Formulas

### Glass Cannon Classes (High Damage, High Risk)

**Formula:**
- Health: -30 to -40
- Stamina: +6 to +10
- Damage: +30% to +40%
- **EHP Target**: 160-180

**Examples:**
- Archer (Ranged): -35 HP, +8 Stamina, +40% bow/crossbow
- Assassin (Melee): -35 HP, +10 Stamina, +25% dagger

### Tank Classes (High Survivability, Moderate Damage)

**Formula:**
- Health: +25 to +35
- Stamina: -5 to +5
- Damage: +10% to +20%
- **EHP Target**: 180-200

**Examples:**
- Crusader: +30 HP, +0 Stamina, +15% mace/hammer

### Balanced Classes (Medium Everything)

**Formula:**
- Health: +5 to +15
- Stamina: +3 to +8
- Damage: +15% to +25%
- **EHP Target**: 165-185

**Examples:**
- Swordsman: +10 HP, +5 Stamina, +20% sword
- Berserker: -25 HP, +8 Stamina, +30% axe (high-risk balanced)

---

## Race Balancing

### Base Races (No Weapon Bonuses)

**Elf:**
- Health: +0 HP
- Stamina: +15 Stamina
- **EHP: 175** (mobile specialist)

**Orc:**
- Health: +75 HP
- Stamina: +0 Stamina
- **EHP: 175** (tank specialist)

**Human:**
- Health: +35 HP
- Stamina: +5 Stamina
- **EHP: 160** (balanced, slightly weaker to encourage class selection)

---

## Rebalanced Class Values

### CURRENT ISSUES (Pre-Balance)

| Class | HP | Stamina | EHP | Damage | Issue |
|-------|-----|---------|-----|--------|-------|
| Berserker | -25 | +8 | 115 | +30% | OK |
| Swordsman | +10 | +5 | 135 | +20% | OK |
| Crusader | +30 | +0 | 130 | +15% | OK |
| **Assassin** | **-20** | **+10** | **130** | **+35%** | **TOO STRONG** |
| Archer | -35 | +8 | 105 | +40% | OK |

### NEW BALANCED VALUES

| Class | HP | Stamina | EHP | Damage | Rationale |
|-------|-----|---------|-----|--------|-----------|
| None | 0 | 0 | 100 | 0% | Pure racial traits |
| Berserker | -25 | +8 | 115 | +30% | High-risk melee |
| Swordsman | +10 | +5 | 135 | +20% | Balanced fighter |
| Crusader | +30 | +0 | 130 | +15% | Tank specialist |
| **Assassin** | **-35** | **+10** | **115** | **+25%** | **Fixed: Same risk as Archer** |
| Archer | -35 | +8 | 105 | +40% | Ranged glass cannon |

**Assassin Changes:**
- Health: -20 → **-35** (same penalty as Archer for safety)
- Stamina: +10 (unchanged - enables hit-n-run)
- Damage: +35% → **+25%** (reduced because daggers are inherently safe)

---

## Combined Race + Class Examples

### After Rebalancing

**Elf Assassin (OLD - OVERPOWERED):**
- Elf: +0 HP, +15 Stamina = 175 EHP
- Assassin: -20 HP, +10 Stamina = 130 EHP
- **Combined: 100 + 0 - 20 + (25 × 5) = 205 EHP** ← Too strong!
- **Damage:** +35% daggers

**Elf Assassin (NEW - BALANCED):**
- Elf: +0 HP, +15 Stamina = 175 EHP
- Assassin: -35 HP, +10 Stamina = 115 EHP
- **Combined: 100 + 0 - 35 + (25 × 5) = 190 EHP** ← Balanced!
- **Damage:** +25% daggers

**Orc Archer (Unconventional):**
- Orc: +75 HP, +0 Stamina = 175 EHP
- Archer: -35 HP, +8 Stamina = 105 EHP
- **Combined: 100 + 75 - 35 + (8 × 5) = 180 EHP**
- **Damage:** +40% bows
- **Playstyle:** Tanky ranged with sustain

**Human Berserker:**
- Human: +35 HP, +5 Stamina = 160 EHP
- Berserker: -25 HP, +8 Stamina = 115 EHP
- **Combined: 100 + 35 - 25 + (13 × 5) = 175 EHP**
- **Damage:** +30% axes
- **Playstyle:** Balanced high-risk warrior

---

## Charged Attack Considerations

### The Dagger Problem

**Issue:**
- Charged dagger attacks have **built-in mobility** (dash away after hit)
- This makes them **safer than other melee weapons**
- Damage multiplier applies to **both** normal and charged attacks
- High stamina = more charged attack spam

**Current API Limitation:**
- Hytale API doesn't distinguish between normal and charged damage
- **Cannot** reduce only charged attack damage
- Must balance by reducing **overall** multiplier

**Solution:**
- Reduce total dagger multiplier from 35% to **25%**
- Increase HP penalty to compensate for safety
- Keep high stamina for mobility (this is the class fantasy)

### Weapon Safety Rankings

**Safest to Riskiest:**

1. **Bow/Crossbow** (Range = safety) → Justifies 40% damage
2. **Dagger** (Mobility = safety) → Should be 22-25% damage
3. **Sword** (Balanced) → 20-25% damage is fair
4. **Axe** (Slow, exposed) → 28-30% damage justified
5. **Mace/Hammer** (Very slow) → 15-20% damage

---

## Recommended Balance Changes

### Immediate Changes

**1. Assassin Class (PRIORITY FIX)**

OLD:
```json
{
  "id": "assassin",
  "healthModifier": -20.0,
  "staminaModifier": 10.0,
  "weapons": [{"types": ["dagger"], "damageMultiplier": 1.35}]
}
```

NEW (BALANCED):
```json
{
  "id": "assassin",
  "healthModifier": -35.0,
  "staminaModifier": 10.0,
  "weapons": [{"types": ["dagger"], "damageMultiplier": 1.22}]
}
```

**Rationale:**
- EHP reduced from 205 (Elf Assassin) to 190
- Damage reduced from +35% to +22%
- Still maintains hit-n-run fantasy with +10 stamina
- Health penalty matches Archer for equal risk

**2. Berserker Class (Minor Adjustment)**

OLD:
```json
{"healthModifier": -25.0, "staminaModifier": 8.0, "damageMultiplier": 1.3}
```

KEEP AS-IS (Well balanced):
- EHP = 115 (base class) + 40 (stamina) = 155 effective
- High-risk playstyle with axes (slow, exposed)
- 30% damage justified by risk

**3. Swordsman Class (Well Balanced)**

KEEP AS-IS:
- +10 HP, +5 Stamina = 135 EHP
- +20% sword damage
- Perfectly balanced for general use

**4. Crusader Class (Well Balanced)**

KEEP AS-IS:
- +30 HP = pure tank
- +15% mace/hammer = moderate damage
- Good balance for defensive players

**5. Archer Class (Well Balanced)**

KEEP AS-IS:
- -35 HP, +8 Stamina = 105 EHP
- +40% ranged damage justified by safety
- Glass cannon archetype

---

## Advanced Balancing

### Creating New Classes

Use this checklist:

**1. Define Fantasy**
- What is the class's identity?
- Tank? DPS? Mobile? Ranged?

**2. Calculate EHP Budget**
- Determine target EHP (100-140 range)
- Allocate between HP and Stamina
- Remember: **1 Stamina = 5 HP**

**3. Assign Damage Multiplier**
- Consider weapon safety/risk
- Low EHP = High damage allowed
- High EHP = Low damage to prevent OP tanks

**4. Test in Combinations**
- Pair with Elf (stamina specialist)
- Pair with Orc (health specialist)
- Pair with Human (balanced)
- Ensure no combo exceeds 220 EHP with high damage

### EHP Targets by Archetype

**Glass Cannon:**
- EHP: 100-120 (base class)
- Damage: +30% to +40%
- Example: Archer, Assassin

**Balanced:**
- EHP: 120-140 (base class)
- Damage: +20% to +25%
- Example: Swordsman, Berserker

**Tank:**
- EHP: 130-150 (base class)
- Damage: +10% to +20%
- Example: Crusader

**Support/Utility:**
- EHP: 110-130 (base class)
- Damage: +5% to +15%
- Special abilities instead of damage

---

## Example Balance Calculations

### Example 1: Creating a "Spearman" Class

**Fantasy:** Medium-range fighter with reach advantage

**Step 1: Allocate EHP Budget**
- Target: 125 EHP (balanced)
- Choose: +5 HP, +5 Stamina
- Check: 100 + 5 + (5 × 5) = 130 EHP ✓

**Step 2: Assign Damage**
- Spears have medium risk (range but melee)
- Multiplier: 1.18 (18% damage)

**Final Config:**
```json
{
  "id": "spearman",
  "healthModifier": 5.0,
  "staminaModifier": 5.0,
  "weapons": [{"types": ["spear"], "damageMultiplier": 1.18}]
}
```

### Example 2: Creating a "Tank" Class

**Fantasy:** Maximum survivability, low damage

**Step 1: Allocate EHP Budget**
- Target: 150 EHP (high tank)
- Choose: +50 HP, +0 Stamina
- Check: 100 + 50 + (0 × 5) = 150 EHP ✓

**Step 2: Assign Damage**
- High EHP = low damage
- Multiplier: 1.08 (8% damage)

**Final Config:**
```json
{
  "id": "guardian",
  "healthModifier": 50.0,
  "staminaModifier": 0.0,
  "weapons": [{"types": ["shield"], "damageMultiplier": 1.08}]
}
```

---

## Community Feedback Analysis

### Assassin Class Issues (Community Report)

**Reported Problems:**
1. ✗ Dual daggers have built-in "Hit n' Run" charged attack
2. ✗ 35% damage boost amplifies charged damage too much
3. ✗ +10 Stamina allows constant charged attack spam
4. ✗ Only -20 HP penalty is too small for safety level
5. ✗ Compared to Berserker (-25 HP), penalty should be higher

**Our Analysis:**
- Daggers are **safer than axes** (mobility vs exposure)
- Charged attacks **cannot be nerfed separately** (API limitation)
- High stamina + high damage + safety = **broken combo**
- EHP with Elf race = **205** (should be ~190)

**Solutions Implemented:**
1. ✓ Health penalty: -20 → **-35 HP** (matches Archer)
2. ✓ Damage multiplier: 1.35 → **1.22** (reduced by ~10%)
3. ✓ Stamina kept at +10 (maintains class fantasy)
4. ✓ New EHP: **190** (balanced with other glass cannons)

---

## Recommended Balance Changes

### For Server Admins

**Conservative Balance (Recommended):**
```json
{
  "id": "assassin",
  "healthModifier": -35.0,
  "staminaModifier": 10.0,
  "weapons": [{"types": ["dagger"], "damageMultiplier": 1.22}]
}
```

**Aggressive Nerf (If Still Overpowered):**
```json
{
  "id": "assassin",
  "healthModifier": -40.0,
  "staminaModifier": 8.0,
  "weapons": [{"types": ["dagger"], "damageMultiplier": 1.20}]
}
```

**Buff (If Too Weak After Changes):**
```json
{
  "id": "assassin",
  "healthModifier": -30.0,
  "staminaModifier": 12.0,
  "weapons": [{"types": ["dagger"], "damageMultiplier": 1.25}]
}
```

---

## Playtesting Guide

### How to Test Balance

**1. Test Each Class Solo:**
- Spawn with class
- Fight same enemy type (e.g., zombies)
- Count kills before death
- Record time-to-kill

**2. Compare Results:**
- All classes should have similar **total damage output** over time
- Glass cannons: High burst, die faster
- Tanks: Low burst, survive longer
- **Total kills should be similar!**

**3. Test Race Combinations:**
- Extreme combos (Elf Assassin, Orc Crusader)
- Balanced combos (Human Swordsman)
- Counter-intuitive combos (Orc Archer, Elf Crusader)

**4. Adjust Based on Data:**
- If one combo dominates: Nerf the class
- If one combo is useless: Buff the class
- If race is always chosen: Nerf the race

---

## Balance Targets by Combination

### Minimum EHP (Weakest Combo)
- **Target:** 140-150 EHP
- **Example:** Elf Archer
- **Calculation:** 100 + 0 - 35 + (23 × 5) = 180 EHP

### Maximum EHP (Strongest Combo)
- **Target:** 210-220 EHP
- **Example:** Orc Crusader
- **Calculation:** 100 + 75 + 30 + (0 × 5) = 205 EHP

### Average EHP (Most Combos)
- **Target:** 180-195 EHP
- **Example:** Human Swordsman
- **Calculation:** 100 + 35 + 10 + (10 × 5) = 195 EHP

---

## Quick Balance Checklist

Before approving any custom class/race:

- [ ] Calculate EHP using formula
- [ ] Check EHP is within 140-220 range
- [ ] Verify damage multiplier matches risk level
- [ ] Test with all three races
- [ ] Ensure no combo exceeds 230 EHP with high damage
- [ ] Confirm weapon types are spelled correctly
- [ ] Test in actual gameplay

---

## JSON Balance Reference

### Template for New Classes

```json
{
  "id": "your_class_id",
  "displayName": "Your Class Name",
  "tagline": "Short description",
  "healthModifier": 0.0,     // -40 to +50 range
  "staminaModifier": 0.0,    // -10 to +20 range
  "strengths": [
    "Describe bonuses here",
    "List weapon specialization",
    "Explain playstyle advantage"
  ],
  "weaknesses": [
    "Describe penalties here",
    "List vulnerabilities",
    "Explain playstyle drawback"
  ],
  "weapons": [
    {
      "types": ["weapon1", "weapon2"],
      "damageMultiplier": 1.25  // 1.0 to 1.5 range
    }
  ]
}
```

### Balance Verification Script

Use this mental checklist:

```
1. EHP = 100 + healthMod + (staminaMod × 5)
2. Is EHP between 100-150? ✓
3. Is damage appropriate for EHP? ✓
4. Does it work with Elf? (stamina specialist) ✓
5. Does it work with Orc? (health specialist) ✓
6. Does it work with Human? (balanced) ✓
```

---

## Changelog of Balance Changes

### Version 2026.1.26.1 - Assassin Rebalance

**Assassin Class:**
- Health: -20 → **-35** (increased penalty)
- Damage: +35% → **+22%** (reduced multiplier)
- Stamina: +10 (unchanged)

**Rationale:**
- Community feedback: Assassin too strong
- Daggers have built-in safety (charged attack mobility)
- EHP reduced from 205 to 190 (Elf Assassin combo)
- Damage reduced to account for weapon safety

---

## Future Considerations

### Potential Features

1. **Separate Charged Attack Multipliers**
   - If API adds support, create separate configs
   - Example: `normalDamage: 1.2, chargedDamage: 1.1`

2. **Conditional Bonuses**
   - Bonus damage at low HP (Berserker rage)
   - Bonus mobility at high stamina (Assassin flow)

3. **Stat Scaling**
   - Bonuses increase with player level
   - Late-game classes unlock at level 10+

4. **PvP vs PvE Balance**
   - Separate multipliers for player vs monster damage
   - PvP-specific health/stamina modifiers

---

## Contact & Feedback

Found an unbalanced combo? Report it with:
- Race + Class combination
- EHP calculation
- Weapon type used
- Why it feels overpowered/underpowered

We'll update this guide based on community playtesting!
