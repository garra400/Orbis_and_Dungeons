package com.garra400.racas.storage;

import java.util.List;

/**
 * Configuration for a combat class, loaded from JSON.
 * Classes provide weapon specialization and playstyle.
 */
public class ClassConfig {
    public String id;
    public String displayName;
    public String tagline;
    public float healthModifier;  // Additional HP modifier from class
    public float staminaModifier; // Additional stamina modifier from class
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

    public ClassConfig() {
    }

    public ClassConfig(String id, String displayName, String tagline, 
                       float health, float stamina,
                       List<String> strengths, List<String> weaknesses,
                       List<WeaponConfig> weapons) {
        this.id = id;
        this.displayName = displayName;
        this.tagline = tagline;
        this.healthModifier = health;
        this.staminaModifier = stamina;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.weapons = weapons;
    }
}
