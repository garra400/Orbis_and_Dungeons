package com.garra400.racas.races;

import java.util.Collections;
import java.util.List;

public class HumanRace implements RaceDefinition {
    @Override
    public String id() {
        return "human";
    }

    @Override
    public String displayName() {
        return "Human";
    }

    @Override
    public String tagline() {
        return "Versatile and balanced, adapts to everything.";
    }

    @Override
    public float healthBonus() {
        return 35f;
    }

    @Override
    public float staminaBonus() {
        return 5f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Balanced Build: +35 Health and +5 Stamina.",
                "Adaptable: 135 health / 15 stamina total.",
                "All-Rounder: Works in any role."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "Jack of All Trades: Doesn't excel at anything.",
                "Average: Less specialized than other races."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return Collections.emptyList();
    }
}
