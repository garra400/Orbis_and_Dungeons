package com.garra400.racas.races;

import java.util.List;

public class BerserkerRace implements RaceDefinition {
    @Override
    public String id() {
        return "berserker";
    }

    @Override
    public String displayName() {
        return "Berserker";
    }

    @Override
    public String tagline() {
        return "Frenzied axe wielder.";
    }

    @Override
    public float healthBonus() {
        return -25f;
    }

    @Override
    public float staminaBonus() {
        return 8f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Savage Strikes: +30% damage with axes and battleaxes.",
                "Aggressive: +8 Stamina for sustained combat.",
                "High Risk, High Reward: Strong offense, weak defense."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "Reckless: -25 Health (only 75 HP total).",
                "All-In Fighter: Must kill before being killed.",
                "Axe Dependent: Bonus only with axes equipped."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return List.of(
                new WeaponRule(1.5f, "Weapon_Battleaxe", "weapon_battleaxe", "Weapon_Axe", "weapon_axe")
        );
    }
}
