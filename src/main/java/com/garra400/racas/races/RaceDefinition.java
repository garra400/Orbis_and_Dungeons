package com.garra400.racas.races;

import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public interface RaceDefinition {
    String id();
    String displayName();
    String tagline();
    float healthBonus();
    float staminaBonus();
    List<String> strengths();
    List<String> weaknesses();
    List<WeaponRule> weaponRules();

    default float resolveWeaponMultiplier(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 1.0f;
        }
        List<WeaponRule> rules = weaponRules();
        if (rules == null) {
            return 1.0f;
        }
        for (WeaponRule rule : rules) {
            if (rule.matches(stack)) {
                return rule.multiplier();
            }
        }
        return 1.0f;
    }

    static List<String> listOf(String... items) {
        return List.of(items);
    }

    default List<WeaponRule> weaponRulesOrEmpty() {
        List<WeaponRule> rules = weaponRules();
        return rules != null ? rules : Collections.emptyList();
    }
}
