# Orbis and Dungeons - Race & Class System

## Overview

Orbis and Dungeons introduces a permanent, modular character progression system to Hytale. Upon joining for the first time, players navigate a **paginated UI** to define their character's strengths. Choices persist across sessions, worlds, and dimensions via a robust file-backed cache system.

> 🚧 **Development Note: Current vs. Future System**
> 
> *   **Currently:** The UI presents a single list where you must choose **either** a Race **or** a Class. You cannot have both simultaneously in this version (e.g., you cannot be an "Orc Berserker" yet).
> *   **Future Plans:** We are working towards a system where players will have **two separate slots**, allowing you to select a Race (for base stats) AND a Class (for weapon specialization) to create unique combinations.

***

## 🧬 Base Races

_Races focus on raw stats (Health & Stamina) and general survivability. They do not have specific weapon bonuses, making them ideal for versatile playstyles._

### ⚔️ Orc – The Unstoppable Tank

_"War tank, resists the impossible."_

*   **Stats:** **175 HP** (+75) | **10 Stamina**
*   **Role:** Pure Tank.
*   **Best for:** Players who want to stand on the frontline and soak massive damage.

### ⚖️ Human – The Balanced Adventurer

_"Versatile and balanced, adapts to everything."_

*   **Stats:** **135 HP** (+35) | **15 Stamina** (+5)
*   **Role:** Versatile / Jack-of-all-trades.
*   **Best for:** Players who want flexibility without locking into a specific weapon or weakness.

### 🏹 Elf – The Agile Warrior

_"Agile and tireless, moves like the wind."_

*   **Stats:** **100 HP** | **25 Stamina** (+15)
*   **Role:** Mobility Specialist.
*   **Best for:** Parkour and high-tempo combat. Relies on dodging rather than tanking.

***

## ⚔️ Combat Classes

_Classes are specialized for combat. They generally trade base Health/Stamina for significant **Damage Bonuses** with specific weapons. These define a strict fighting style._

### 🛡️ Crusader – Mace Specialist

*   **Stats:** **130 HP** (+30) | **10 Stamina**
*   **Bonus:** **+15% Damage** with Maces & Hammers.
*   **Playstyle:** A durable fighter bridging the gap between tank and damage dealer.

### ⚔️ Swordsman – Balanced Warrior

*   **Stats:** **110 HP** (+10) | **15 Stamina** (+5)
*   **Bonus:** **+20% Damage** with Swords.
*   **Playstyle:** A versatile combatant with a slight offensive edge over the Human race.

### 🪓 Berserker – The Frenzied Striker

*   **Stats:** **75 HP** (-25) | **18 Stamina** (+8)
*   **Bonus:** **+30% Damage** with Axes & Battleaxes.
*   **Playstyle:** High risk, high reward. Sacrifices health for aggressive stamina and damage.

### 🗡️ Assassin – Dagger Master

*   **Stats:** **80 HP** (-20) | **20 Stamina** (+10)
*   **Bonus:** **+35% Damage** with Daggers.
*   **Playstyle:** Glass cannon. High burst damage and speed, but very fragile.

### 🏹 Archer – Ranged Specialist

*   **Stats:** **65 HP** (-35) | **18 Stamina** (+8)
*   **Bonus:** **+40% Damage** with Bows & Crossbows.
*   **Playstyle:** Extreme range damage. Requires keeping distance due to very low health.

***

## 📊 Comparison Table

| Type  |Name      |Role       |Health |Stamina |Weapon Bonus     |
| ----- |--------- |---------- |------ |------- |---------------- |
| <strong>RACE</strong> |<strong>Orc</strong> |Tank       |<strong>175</strong> |10      |None             |
| <strong>RACE</strong> |<strong>Human</strong> |Balanced   |<strong>135</strong> |15      |None             |
| <strong>RACE</strong> |<strong>Elf</strong> |Speed      |100    |<strong>25</strong> |None             |
| <strong>CLASS</strong> |<strong>Crusader</strong> |Bruiser    |130    |10      |+15% Mace/Hammer |
| <strong>CLASS</strong> |<strong>Swordsman</strong> |Fighter    |110    |15      |+20% Sword       |
| <strong>CLASS</strong> |<strong>Assassin</strong> |Burst DPS  |80     |20      |+35% Dagger      |
| <strong>CLASS</strong> |<strong>Berserker</strong> |Aggro DPS  |75     |18      |+30% Axe         |
| <strong>CLASS</strong> |<strong>Archer</strong> |Ranged DPS |65     |18      |+40% Bow/Xbow    |

***

## Technical Features

*   **One-Time Selection:** UI appears only on first entry.
*   **Paginated UI:** New interface allows navigation between pages of choices.
*   **Persistence:** Choices are stored in `race_cache.txt` (offline-safe) and player components.
*   **Commands:**
*   `/raceinfo [--player <name>]` — Check stats/class.
*   `/racetrade` & `/racereset` — Admin management.

## Installation

1.  Download the mod JAR.
2.  Place it in `UserData/Mods/`.
3.  Launch Hytale and enable the mod.
4.  Join the world and make your choice!

***

**Choose wisely—your selection defines your adventure!**
