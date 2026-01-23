package com.garra400.racas.races;

import java.util.List;

public class ArcherRace implements RaceDefinition {
    @Override
    public String id() {
        return "archer";
    }

    @Override
    public String displayName() {
        return "Archer";
    }

    @Override
    public String tagline() {
        return "Deadly at range, vulnerable up close.";
    }

    @Override
    public float healthBonus() {
        return -35f;
    }

    @Override
    public float staminaBonus() {
        return 8f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Marksman: +40% damage with bows and crossbows.",
                "Range Advantage: Strong at distance.",
                "Mobile Shooter: +8 Stamina for repositioning."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "Very Fragile: -35 Health (only 65 HP total).",
                "Melee Weakness: Poor survivability up close.",
                "Distance Required: Must maintain range to be effective."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return List.of(
                new WeaponRule(1.40f, "Weapon_Bow", "weapon_bow", "bow", "Bow", "Shortbow", "shortbow", 
                        "crossbow", "Crossbow", "Weapon_Crossbow", "weapon_crossbow")
        );
    }
}
