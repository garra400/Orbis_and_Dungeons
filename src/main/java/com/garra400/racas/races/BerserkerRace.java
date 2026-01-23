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
        return 0f;
    }

    @Override
    public float staminaBonus() {
        return 0f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Savage Strikes: +50% damage with Weapon_Axe and Weapon_Battleaxe.",
                "Specialization: Bonus only while axes are equipped."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "No defensive boost: base health 100.",
                "No stamina bonus: base stamina 10."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return List.of(
                new WeaponRule(1.5f, "Weapon_Battleaxe", "weapon_battleaxe", "Weapon_Axe", "weapon_axe")
        );
    }
}
