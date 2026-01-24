package com.garra400.racas.races;

import com.garra400.racas.storage.RaceConfig;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Race definition that loads its configuration from JSON.
 */
public class ConfigurableRace implements RaceDefinition {

    private final RaceConfig config;
    private final List<WeaponRule> weaponRules;

    public ConfigurableRace(RaceConfig config) {
        this.config = config;
        this.weaponRules = buildWeaponRules(config);
    }

    private static List<WeaponRule> buildWeaponRules(RaceConfig config) {
        if (config.weapons == null || config.weapons.isEmpty()) {
            return Collections.emptyList();
        }

        List<WeaponRule> rules = new ArrayList<>();
        for (RaceConfig.WeaponConfig weaponConfig : config.weapons) {
            if (weaponConfig.types != null && !weaponConfig.types.isEmpty()) {
                // Convert List<String> to String[] for WeaponRule constructor
                String[] fragments = weaponConfig.types.toArray(new String[0]);
                rules.add(new WeaponRule(weaponConfig.damageMultiplier, fragments));
            }
        }
        return rules;
    }

    @Override
    public String id() {
        return config.id;
    }

    @Override
    public String displayName() {
        return config.displayName;
    }

    @Override
    public String tagline() {
        return config.tagline;
    }

    @Override
    public float healthBonus() {
        return config.healthBonus;
    }

    @Override
    public float staminaBonus() {
        return config.staminaBonus;
    }

    @Override
    public List<String> strengths() {
        return config.strengths != null ? config.strengths : Collections.emptyList();
    }

    @Override
    public List<String> weaknesses() {
        return config.weaknesses != null ? config.weaknesses : Collections.emptyList();
    }

    @Override
    public List<WeaponRule> weaponRules() {
        return weaponRules;
    }
}
