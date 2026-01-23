package com.garra400.racas.races;

import java.util.List;

public class SwordsmanRace implements RaceDefinition {
    @Override
    public String id() {
        return "swordsman";
    }

    @Override
    public String displayName() {
        return "Swordsman";
    }

    @Override
    public String tagline() {
        return "Master of the blade, balanced warrior.";
    }

    @Override
    public float healthBonus() {
        return 10f;
    }

    @Override
    public float staminaBonus() {
        return 5f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Blade Mastery: +20% damage with all sword types.",
                "Balanced Fighter: +10 Health and +5 Stamina.",
                "Sword Specialist: Trade-off between stats and damage."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "Weapon Locked: Damage bonus only with swords.",
                "Lower Stats: Less health/stamina than base races."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return List.of(
                new WeaponRule(1.20f, "Weapon_Sword", "weapon_sword", "sword", "Sword")
        );
    }
}
