package com.garra400.racas.races;

import java.util.Collections;
import java.util.List;

/**
 * Dwarf Race - Sturdy and resilient, masters of craftsmanship
 * Focused on high health and moderate stamina
 * Based on traditional dwarf archetype: tough, enduring, skilled craftsmen
 */
public class DwarfRace implements RaceDefinition {
    @Override
    public String id() {
        return "dwarf";
    }

    @Override
    public String displayName() {
        return "Dwarf";
    }

    @Override
    public String tagline() {
        return "Sturdy craftsmen with unbreakable resilience.";
    }

    @Override
    public float healthBonus() {
        // Very high health bonus - dwarves are extremely tough tanks
        return 50f;  // 150 total HP
    }

    @Override
    public float staminaBonus() {
        // No stamina bonus - slow and steady
        return 0f;  // 10 base stamina
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Mountain Endurance: +50 Health (150 total).",
                "Stone Skin: 30% Physical damage resistance.",
                "Sure-footed: 50% Fall damage resistance.",
                "Unbreakable: Extreme survivability and toughness.",
                "Tank Specialist: Perfect for frontline combat."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "Short Stature: Reduced reach in combat.",
                "Heavy Build: 10 Stamina (base) - very low mobility.",
                "Slow Movement: Poor at chasing or escaping."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        // No specific weapon restrictions or bonuses at race level
        // (bonuses come from class selection)
        return Collections.emptyList();
    }
}
