package com.garra400.racas.races;

import java.util.Collections;
import java.util.List;

public class OrcRace implements RaceDefinition {
    @Override
    public String id() {
        return "orc";
    }

    @Override
    public String displayName() {
        return "Orc";
    }

    @Override
    public String tagline() {
        return "War tank, resists the impossible.";
    }

    @Override
    public float healthBonus() {
        return 75f;
    }

    @Override
    public float staminaBonus() {
        return 0f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Iron Skin: +75 Max Health (175 base).",
                "Tank Build: Synergizes with heavy armor.",
                "Steady Defense: Built for frontline."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "Heavy Build: Base stamina 10 (no bonus).",
                "No offensive bonus: relies on gear."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return Collections.emptyList();
    }
}
