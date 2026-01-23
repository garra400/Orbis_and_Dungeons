package com.garra400.racas.races;

import java.util.Collections;
import java.util.List;

public class ElfRace implements RaceDefinition {
    @Override
    public String id() {
        return "elf";
    }

    @Override
    public String displayName() {
        return "Elf";
    }

    @Override
    public String tagline() {
        return "Agile and tireless, moves like the wind.";
    }

    @Override
    public float healthBonus() {
        return 0f;
    }

    @Override
    public float staminaBonus() {
        return 15f;
    }

    @Override
    public List<String> strengths() {
        return List.of(
                "Swift Movement: +15 Stamina (25 total).",
                "High Mobility: More sprinting and dodging ability.",
                "Agile Fighter: Great for hit-and-run tactics."
        );
    }

    @Override
    public List<String> weaknesses() {
        return List.of(
                "Frail Body: No extra health (100 base).",
                "Low Survivability: Must avoid direct combat."
        );
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return Collections.emptyList();
    }
}
