package com.garra400.racas.storage.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public float manaModifier;    // Additional mana modifier from class
    public List<String> strengths;
    public List<String> weaknesses;
    public List<WeaponConfig> weapons;
    public Map<String, Float> damageResistances; // damage type -> resistance multiplier (0.0 = immune, 1.0 = normal)

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
                       float health, float stamina, float mana,
                       List<String> strengths, List<String> weaknesses,
                       List<WeaponConfig> weapons,
                       Map<String, Float> damageResistances) {
        this.id = id;
        this.displayName = displayName;
        this.tagline = tagline;
        this.healthModifier = health;
        this.staminaModifier = stamina;
        this.manaModifier = mana;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.weapons = weapons;
        this.damageResistances = damageResistances != null ? damageResistances : new HashMap<>();
    }
}
