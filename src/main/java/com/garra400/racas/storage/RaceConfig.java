package com.garra400.racas.storage;

import java.util.List;
import java.util.Map;

/**
 * Configuration for a single race, loaded from JSON.
 */
public class RaceConfig {
    public String id;
    public String displayName;
    public String tagline;
    public float healthBonus;
    public float staminaBonus;
    public List<String> strengths;
    public List<String> weaknesses;
    public List<WeaponConfig> weapons;

    public static class WeaponConfig {
        public List<String> types;
        public float damageMultiplier;

        public WeaponConfig() {
        }

        public WeaponConfig(List<String> types, float multiplier) {
            this.types = types;
            this.damageMultiplier = multiplier;
        }
    }

    public RaceConfig() {
    }

    public RaceConfig(String id, String displayName, String tagline, 
                      float health, float stamina,
                      List<String> strengths, List<String> weaknesses,
                      List<WeaponConfig> weapons) {
        this.id = id;
        this.displayName = displayName;
        this.tagline = tagline;
        this.healthBonus = health;
        this.staminaBonus = stamina;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.weapons = weapons;
    }
}
