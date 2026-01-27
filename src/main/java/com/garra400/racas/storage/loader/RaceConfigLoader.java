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
            List.of("100 HP (base)", "25 Stamina (+15)", "Extended mobility"),
            List.of("No health bonus", "Stamina-dependent playstyle"),
            List.of(),
            Map.of() // No resistances
        ));

        // Orc - Maximum health, tank
        configs.put("orc", new RaceConfig(
            "orc",
            "Orc",
            "Brutish and relentless, crushes all opposition.",
            75f,
            0f,
            List.of("175 HP (+75)", "Massive health pool", "Tank role"),
            List.of("10 Stamina (base)", "Limited mobility"),
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
            List.of("135 HP (+35)", "15 Stamina (+5)", "Well-rounded stats"),
            List.of("No specialization", "Average at everything"),
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
                "85 HP (-15)",
                "18 Stamina (+8)",
                "Immune to Fire Damage",
                "Immune to Lava Damage",
                "Infernal heritage"
            ),
            List.of(
                "Fragile physique",
                "Vulnerable to Magic damage (+50%)",
                "Mistrusted by common folk"
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
            List.of("150 HP (+50)", "10 Stamina (base)", "30% Physical resistance", "50% Fall resistance", "Tank Specialist"),
            List.of("Short Stature (reduced reach)", "Very Low Mobility", "Slow Movement"),
            List.of(),
            Map.of(
                "Physical", 0.7f,  // 30% physical damage reduction
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
