package com.garra400.racas.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Manages loading and saving class configurations from/to JSON.
 */
public final class ClassConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configFile;
    private static Map<String, ClassConfig> configs = new LinkedHashMap<>();

    private ClassConfigLoader() {
    }

    public static void init(Path dataDir) {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            configFile = dataDir.resolve("classes_config.json");
            
            if (Files.exists(configFile)) {
                load();
            } else {
                generateDefaultConfig();
                save();
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize class config: " + e.getMessage());
            generateDefaultConfig();
        }
    }

    private static void load() throws IOException {
        String json = Files.readString(configFile, StandardCharsets.UTF_8);
        Type listType = new TypeToken<List<ClassConfig>>(){}.getType();
        List<ClassConfig> list = GSON.fromJson(json, listType);
        
        configs.clear();
        if (list != null) {
            for (ClassConfig config : list) {
                configs.put(config.id, config);
            }
        }
    }

    private static void save() throws IOException {
        List<ClassConfig> list = new ArrayList<>(configs.values());
        String json = GSON.toJson(list);
        Files.writeString(configFile, json, StandardCharsets.UTF_8);
    }

    private static void generateDefaultConfig() {
        configs.clear();
        
        // None/Base - No class (pure race stats)
        configs.put("none", new ClassConfig(
            "none",
            "None",
            "Pure racial traits with no specialization.",
            0f,
            0f,
            List.of("No class restrictions", "Pure racial bonuses", "Versatile"),
            List.of("No weapon specialization", "No additional bonuses"),
            List.of()
        ));
        
        // Berserker - High risk, high damage with axes
        configs.put("berserker", new ClassConfig(
            "berserker",
            "Berserker",
            "Reckless warrior who trades defense for raw power.",
            -25f,  // EHP = 100 - 25 + (8×5) = 115
            8f,
            List.of("+30% axe damage", "18 Stamina (+8)", "High burst damage", "Aggressive playstyle"),
            List.of("75 HP (-25)", "Glass cannon", "High risk gameplay", "Exposed in combat"),
            List.of(new ClassConfig.WeaponConfig(List.of("axe", "battleaxe"), 1.30f))
        ));

        // Swordsman - Balanced warrior  
        configs.put("swordsman", new ClassConfig(
            "swordsman",
            "Swordsman",
            "Balanced warrior with sword mastery.",
            10f,  // EHP = 100 + 10 + (5×5) = 135
            5f,
            List.of("+20% sword damage", "110 HP (+10)", "15 Stamina (+5)", "Balanced stats"),
            List.of("Requires swords to shine", "No defensive specialization", "Average in all areas"),
            List.of(new ClassConfig.WeaponConfig(List.of("sword", "longsword", "greatsword"), 1.20f))
        ));

        // Crusader - Tank with heavy weapons
        configs.put("crusader", new ClassConfig(
            "crusader",
            "Crusader",
            "Armored champion wielding heavy weapons.",
            30f,  // EHP = 100 + 30 + (0×5) = 130
            0f,
            List.of("+15% mace/hammer damage", "130 HP (+30)", "Frontline tank", "High survivability"),
            List.of("10 Stamina (+0)", "Slow playstyle", "Low mobility", "Moderate damage output"),
            List.of(new ClassConfig.WeaponConfig(List.of("mace", "hammer", "warhammer"), 1.15f))
        ));

        // Assassin - Rebalanced based on community feedback
        configs.put("assassin", new ClassConfig(
            "assassin",
            "Assassin",
            "Silent killer who strikes from the shadows.",
            -35f,  // Increased penalty from -20 to -35 (same as Archer for safety)
            10f,
            List.of("+22% dagger damage", "20 Stamina (+10)", "High mobility", "Hit-n-run specialist"),
            List.of("65 HP (-35)", "Very fragile", "High skill floor", "Melee glass cannon"),
            List.of(new ClassConfig.WeaponConfig(List.of("dagger"), 1.22f))  // Reduced from 1.35 to 1.22
        ));

        // Archer - Ranged specialist
        configs.put("archer", new ClassConfig(
            "archer",
            "Archer",
            "Master of ranged combat, deadly from afar.",
            -35f,  // EHP = 100 - 35 + (8×5) = 105
            8f,
            List.of("+40% bow/crossbow damage", "18 Stamina (+8)", "Ranged superiority", "Safe distance fighting"),
            List.of("65 HP (-35)", "Extremely fragile", "Positioning critical", "Useless in melee"),
            List.of(new ClassConfig.WeaponConfig(List.of("bow", "crossbow", "longbow"), 1.40f))
        ));
    }

    public static ClassConfig getClass(String classId) {
        return configs.get(classId);
    }

    public static ClassConfig getConfig(String classId) {
        return getClass(classId);
    }

    public static Collection<ClassConfig> getAllConfigs() {
        return configs.values();
    }

    public static Collection<ClassConfig> getAllClasses() {
        return getAllConfigs();
    }

    public static boolean hasConfig(String classId) {
        return configs.containsKey(classId);
    }

    public static void reload() {
        try {
            if (configFile != null && Files.exists(configFile)) {
                load();
            }
        } catch (IOException e) {
            System.err.println("Failed to reload class config: " + e.getMessage());
        }
    }
}
