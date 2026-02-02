package com.garra400.racas.storage.loader;

import com.garra400.racas.storage.config.RaceConfig;
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
 * Manages loading and saving race configurations from/to JSON.
 */
public final class RaceConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configFile;
    private static Map<String, RaceConfig> configs = new LinkedHashMap<>();

    private RaceConfigLoader() {
    }

    public static void init(Path dataDir) {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            configFile = dataDir.resolve("races_config.json");
            
            if (Files.exists(configFile)) {
                load();
            } else {
                generateDefaultConfig();
                save();
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize race config: " + e.getMessage());
            generateDefaultConfig();
        }
    }

    private static void load() throws IOException {
        String json = Files.readString(configFile, StandardCharsets.UTF_8);
        Type listType = new TypeToken<List<RaceConfig>>(){}.getType();
        List<RaceConfig> list = GSON.fromJson(json, listType);
        
        configs.clear();
        if (list != null) {
            for (RaceConfig config : list) {
                configs.put(config.id, config);
            }
        }
    }

    private static void save() throws IOException {
        List<RaceConfig> list = new ArrayList<>(configs.values());
        String json = GSON.toJson(list);
        Files.writeString(configFile, json, StandardCharsets.UTF_8);
    }

    private static void generateDefaultConfig() {
        configs.clear();
        
        // Elf - High stamina, mobility focus
        configs.put("elf", new RaceConfig(
            "elf",
            "Elf",
            "Agile and tireless, moves like the wind.",
            0f,
            15f,
            List.of("+15 Stamina (25 total)"),
            List.of("No health bonus (100 base)"),
            List.of(),
            Map.of() // No resistances
        ));

        // Orc - Maximum health, tank
        configs.put("orc", new RaceConfig(
            "orc",
            "Orc",
            "Brutish and relentless, crushes all opposition.",
            100f,
            -2f,
            List.of("+100 Health (200 total)"),
            List.of("-2 Stamina (8 total)"),
            List.of(),
            Map.of() // No resistances
        ));

        // Human - Balanced baseline
        configs.put("human", new RaceConfig(
            "human",
            "Human",
            "Balanced and adaptable, jack of all trades.",
            35f,
            5f,
            List.of("+35 Health (135 total)", "+5 Stamina (15 total)"),
            List.of("No special abilities"),
            List.of(),
            Map.of() // No resistances
        ));

        // Tiefling - Fire-resistant demon spawn
        configs.put("tiefling", new RaceConfig(
            "tiefling",
            "Tiefling",
            "Demon-touched bloodline, born of fire.",
            -15f,
            8f,
            List.of(
                "+8 Stamina (18 total)",
                "100% Fire immunity",
                "100% Lava immunity"
            ),
            List.of(
                "-15 Health (85 total)",
                "+50% Magic damage taken"
            ),
            List.of(),
            Map.of(
                "Fire", 0.0f,      // 100% fire immunity
                "Lava", 0.0f,      // 100% lava immunity
                "Magic", 1.5f      // 50% more magic damage (weakness)
            )
        ));

        // Dwarf - Extreme tank with damage resistances
        configs.put("dwarf", new RaceConfig(
            "dwarf",
            "Dwarf",
            "Sturdy craftsmen with unbreakable resilience.",
            50f,
            0f,
            List.of("+50 Health (150 total)", "20% Physical damage reduction", "50% Fall damage reduction"),
            List.of("No stamina bonus (10 base)"),
            List.of(),
            Map.of(
                "Physical", 0.8f,  // 20% physical damage reduction
                "Fall", 0.5f       // 50% fall damage reduction
            )
        ));
    }

    public static RaceConfig getConfig(String raceId) {
        return configs.get(raceId);
    }

    public static Collection<RaceConfig> getAllConfigs() {
        return configs.values();
    }

    public static boolean hasConfig(String raceId) {
        return configs.containsKey(raceId);
    }

    public static void reload() {
        try {
            if (configFile != null && Files.exists(configFile)) {
                load();
            }
        } catch (IOException e) {
            System.err.println("Failed to reload race config: " + e.getMessage());
        }
    }
}
