package com.garra400.racas.storage.loader;

import com.garra400.racas.storage.config.ModConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Manages loading and saving the main mod configuration.
 * This config controls compatibility with other mods.
 */
public final class ModConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configFile;
    private static ModConfig config;
    private static boolean detectedStatMods = false;

    private ModConfigLoader() {}

    public static void init(Path dataDir) {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            configFile = dataDir.resolve("mod_config.json");
            
            if (Files.exists(configFile)) {
                load();
            } else {
                // First run - create default config
                config = new ModConfig();
                save();
                System.out.println("[Orbis] Created mod_config.json with default settings");
            }
            
            // Auto-detect stat mods if enabled
            if (config.autoDetectStatMods) {
                detectStatMods();
            }
            
        } catch (IOException e) {
            System.err.println("[Orbis] Failed to initialize mod config: " + e.getMessage());
            config = new ModConfig();
        }
    }

    private static void load() throws IOException {
        String json = Files.readString(configFile, StandardCharsets.UTF_8);
        config = GSON.fromJson(json, ModConfig.class);
        if (config == null) {
            config = new ModConfig();
        }
        System.out.println("[Orbis] Loaded mod_config.json:");
        System.out.println("[Orbis]   - applyStatModifiers: " + config.applyStatModifiers);
        System.out.println("[Orbis]   - compatibilityMode: " + config.compatibilityMode);
        System.out.println("[Orbis]   - autoDetectStatMods: " + config.autoDetectStatMods);
    }

    private static void save() throws IOException {
        String json = GSON.toJson(config);
        Files.writeString(configFile, json, StandardCharsets.UTF_8);
    }

    /**
     * Auto-detect other stat mods and adjust settings accordingly.
     */
    private static void detectStatMods() {
        if (config == null || config.knownStatMods == null) {
            return;
        }

        StringBuilder detected = new StringBuilder();
        for (String modClass : config.knownStatMods) {
            try {
                Class.forName(modClass);
                detected.append(modClass).append(", ");
                detectedStatMods = true;
            } catch (ClassNotFoundException ignored) {
                // Mod not present
            }
        }

        if (detectedStatMods) {
            String modList = detected.toString();
            if (modList.endsWith(", ")) {
                modList = modList.substring(0, modList.length() - 2);
            }
            System.out.println("[Orbis] Detected stat mods: " + modList);
            System.out.println("[Orbis] Stat modifiers will respect these mods (compatibility mode active)");
            
            // If compatibility mode is on and we detected stat mods, use longer delay
            if (config.compatibilityMode && config.statApplicationDelayMs < 500) {
                config.statApplicationDelayMs = 500;
                System.out.println("[Orbis] Increased stat application delay to 500ms for compatibility");
            }
        } else {
            System.out.println("[Orbis] No conflicting stat mods detected - running in standalone mode");
        }
    }

    public static ModConfig getConfig() {
        if (config == null) {
            config = new ModConfig();
        }
        return config;
    }

    public static void reload() {
        try {
            if (configFile != null && Files.exists(configFile)) {
                load();
                if (config.autoDetectStatMods) {
                    detectStatMods();
                }
            }
        } catch (IOException e) {
            System.err.println("[Orbis] Failed to reload mod config: " + e.getMessage());
        }
    }

    /**
     * Check if stat modifiers should be applied.
     * Considers both config setting and auto-detection.
     */
    public static boolean shouldApplyStatModifiers() {
        return config != null && config.applyStatModifiers;
    }

    /**
     * Check if health modifier should be applied.
     */
    public static boolean shouldApplyHealthModifier() {
        return config != null && config.applyStatModifiers && config.applyHealthModifier;
    }

    /**
     * Check if stamina modifier should be applied.
     */
    public static boolean shouldApplyStaminaModifier() {
        return config != null && config.applyStatModifiers && config.applyStaminaModifier;
    }

    /**
     * Check if mana modifier should be applied.
     */
    public static boolean shouldApplyManaModifier() {
        return config != null && config.applyStatModifiers && config.applyManaModifier;
    }

    /**
     * Check if weapon damage multipliers should be applied.
     */
    public static boolean shouldApplyWeaponDamage() {
        return config != null && config.applyWeaponDamageMultipliers;
    }

    /**
     * Check if damage resistance should be applied.
     */
    public static boolean shouldApplyDamageResistance() {
        return config != null && config.applyDamageResistance;
    }

    /**
     * Check if race UI should show on first join.
     */
    public static boolean shouldShowRaceUiOnFirstJoin() {
        return config != null && config.showRaceUiOnFirstJoin;
    }

    /**
     * Check if player should be healed after stat application.
     */
    public static boolean shouldHealAfterStatApplication() {
        return config != null && config.healAfterStatApplication;
    }

    /**
     * Get the delay before applying stats (in milliseconds).
     */
    public static int getStatApplicationDelay() {
        return config != null ? config.statApplicationDelayMs : 0;
    }

    /**
     * Check if compatibility mode is enabled.
     */
    public static boolean isCompatibilityMode() {
        return config != null && config.compatibilityMode;
    }

    /**
     * Check if debug mode is enabled.
     */
    public static boolean isDebugMode() {
        return config != null && config.debugMode;
    }

    /**
     * Check if other stat mods were detected.
     */
    public static boolean hasDetectedStatMods() {
        return detectedStatMods;
    }
}
