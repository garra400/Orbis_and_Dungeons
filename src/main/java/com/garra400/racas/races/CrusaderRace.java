package com.garra400.racas.races;

import java.util.List;

public class CrusaderRace implements RaceDefinition {
    @Override
    public String id() {
        return "crusader";
    }

    @Override
    public String displayName() {
        return "Crusader";
    }

    @Override
    public String tagline() {
        return "Holy warrior, built like a fortress.";
    }

    @Override
    public float healthBonus() {
        return 30f;
    }

    @Override
    public float staminaBonus() {
        return 0f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Iron Will: +30 Health for durability.",
                "Mace Crusher: +15% damage with maces and hammers.",
                "Steady Fighter: Balanced between tank and damage."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "No Mobility: Base stamina only (10).",
                "Heavy Build: Can't chase or escape easily.",
                "Weapon Dependent: Bonus only with maces/hammers."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return List.of(
                new WeaponRule(1.15f, "Weapon_Mace", "weapon_mace", "mace", "Mace", "hammer", "Hammer")
        );
    }
}
