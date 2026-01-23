package com.garra400.racas.races;

import java.util.List;

public class AssassinRace implements RaceDefinition {
    @Override
    public String id() {
        return "assassin";
    }

    @Override
    public String displayName() {
        return "Assassin";
    }

    @Override
    public String tagline() {
        return "Swift shadow, deadly precision.";
    }

    @Override
    public float healthBonus() {
        return -20f;
    }

    @Override
    public float staminaBonus() {
        return 10f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Lethal Strikes: +35% damage with daggers.",
                "Agile: +10 Stamina for mobility.",
                "Critical Fighter: Higher damage but very risky."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "Glass Cannon: -20 Health (only 80 HP total).",
                "Very Fragile: Dies quickly in direct combat.",
                "Dagger Only: Requires close-range combat."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return List.of(
                new WeaponRule(1.35f, "Weapon_Dagger", "weapon_dagger", "dagger", "Dagger", "knife", "Knife")
        );
    }
}
