package com.garra400.racas.races;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.Locale;

public record WeaponRule(float multiplier, String... idFragments) {
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        String itemId = stack.getItemId();
        if (itemId != null && containsAny(itemId)) {
            return true;
        }
        Item item = stack.getItem();
        if (item != null) {
            String configId = item.getId();
            if (configId != null && containsAny(configId)) {
                return true;
            }
            String anim = item.getPlayerAnimationsId();
            if (anim != null && containsAny(anim)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        for (String frag : idFragments) {
            if (frag == null || frag.isEmpty()) {
                continue;
            }
            if (value.contains(frag) || lower.contains(frag.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
