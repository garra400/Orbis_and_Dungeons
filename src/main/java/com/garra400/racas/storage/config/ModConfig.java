package com.garra400.racas.storage.config;

/**
 * General mod configuration settings.
 * Controls compatibility options with other mods.
 */
public class ModConfig {
    
    /**
     * Whether to apply health/stamina/mana stat modifiers.
     * Set to false if using other mods that manage stats (RPGLeveling, MMO Skilltree, etc.)
     * Default: true (enabled)
     */
    public boolean applyStatModifiers = true;
    
    /**
     * Whether to apply health modifiers specifically.
     * Set to false if another mod manages health.
     * Only used if applyStatModifiers is true.
     * Default: true (enabled)
     */
    public boolean applyHealthModifier = true;
    
    /**
     * Whether to apply stamina modifiers specifically.
     * Set to false if another mod manages stamina.
     * Only used if applyStatModifiers is true.
     * Default: true (enabled)
     */
    public boolean applyStaminaModifier = true;
    
    /**
     * Whether to apply mana modifiers specifically.
     * Set to false if another mod manages mana.
     * Only used if applyStatModifiers is true.
     * Default: true (enabled)
     */
    public boolean applyManaModifier = true;
    
    /**
     * Delay in milliseconds before applying race stats after player joins.
     * Increase this if you experience issues with stats being overwritten by other mods.
     * Set to 0 for immediate application.
     * Default: 500ms (half second delay)
     */
    public int statApplicationDelayMs = 500;
    
    /**
     * Whether to use compatibility mode with other stat mods.
     * When enabled:
     * - Stats are applied AFTER other mods have finished (with delay)
     * - Stats use additive modifiers instead of replacing values
     * - Integrates better with RPGLeveling, MMO Skilltree, Endless Levelling
     * Default: true (enabled for better compatibility)
     */
    public boolean compatibilityMode = true;
    
    /**
     * Whether to automatically detect and defer to other stat mods.
     * When enabled, if RPGLeveling or similar mods are detected,
     * health/stamina modifiers will be automatically disabled.
     * Default: true (auto-detect enabled)
     */
    public boolean autoDetectStatMods = true;
    
    /**
     * List of mod IDs to detect for auto-disabling stat modifiers.
     * Add mod class names here to extend auto-detection.
     */
    public String[] knownStatMods = new String[] {
        "org.zuxaw.plugin.api.RPGLevelingAPI",       // RPG Leveling
        "com.airjiko.endless.EndlessLevellingAPI",   // Endless Levelling
        "com.ziggfreed.mmo.skilltree.SkillTreeAPI",  // MMO Skilltree
        "com.example.plugin.HardcoreModePlugin"      // Hardcore Mode
    };
    
    /**
     * Whether weapon damage multipliers from classes are enabled.
     * Set to false to disable class weapon bonuses.
     * Default: true (enabled)
     */
    public boolean applyWeaponDamageMultipliers = true;
    
    /**
     * Whether damage resistance from races/classes is enabled.
     * Set to false to disable damage resistance modifiers.
     * Default: true (enabled)
     */
    public boolean applyDamageResistance = true;
    
    /**
     * Whether to show race selection UI on first join.
     * Set to false to only use commands for race selection.
     * Default: true (show UI)
     */
    public boolean showRaceUiOnFirstJoin = true;
    
    /**
     * Whether to heal player to full after applying race stats.
     * Prevents instant death when stats are first applied.
     * Default: true (heal after applying)
     */
    public boolean healAfterStatApplication = true;
    
    /**
     * Debug mode - prints extra information to console.
     * Default: false (disabled)
     */
    public boolean debugMode = false;
    
    public ModConfig() {}
    
    /**
     * Creates a config preset for maximum compatibility with other stat mods.
     * Disables all stat modifiers, keeps only weapon/damage systems.
     */
    public static ModConfig createCompatibilityPreset() {
        ModConfig config = new ModConfig();
        config.applyStatModifiers = false;
        config.applyHealthModifier = false;
        config.applyStaminaModifier = false;
        config.applyManaModifier = false;
        config.compatibilityMode = true;
        config.autoDetectStatMods = true;
        config.statApplicationDelayMs = 1000;
        return config;
    }
    
    /**
     * Creates a config preset for standalone mode (no other stat mods).
     * Enables all features.
     */
    public static ModConfig createStandalonePreset() {
        ModConfig config = new ModConfig();
        config.applyStatModifiers = true;
        config.applyHealthModifier = true;
        config.applyStaminaModifier = true;
        config.applyManaModifier = true;
        config.compatibilityMode = false;
        config.autoDetectStatMods = false;
        config.statApplicationDelayMs = 0;
        return config;
    }
}
