package com.garra400.racas.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File-backed cache of player races.
 * Format: one entry per line -> uuid|username|raceId
 */
public final class RaceStorage {

    private static final Map<UUID, Entry> CACHE = new ConcurrentHashMap<>();
    private static Path storageFile;

    private RaceStorage() {
    }

    public static void init(Path dataDir) {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            storageFile = dataDir.resolve("race_cache.txt");
            load();
        } catch (IOException e) {
            // ignore; will rebuild on next save
        }
    }

    private static void load() {
        if (storageFile == null || !Files.exists(storageFile)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(storageFile, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 3);
                if (parts.length < 3) {
                    continue;
                }
                UUID uuid = UUID.fromString(parts[0]);
                String name = parts[1];
                String race = parts[2];
                CACHE.put(uuid, new Entry(uuid, name, race));
            }
        } catch (Exception e) {
            // ignore corrupt file
        }
    }

    public static void save() {
        if (storageFile == null) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (Entry entry : CACHE.values()) {
                if (entry.raceId == null) {
                    continue;
                }
                sb.append(entry.uuid.toString())
                  .append("|").append(entry.username == null ? "" : entry.username)
                  .append("|").append(entry.raceId)
                  .append("\n");
            }
            Files.writeString(storageFile, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // ignore write errors
        }
    }

    public static void put(UUID uuid, String username, String raceId) {
        if (uuid == null || raceId == null) {
            return;
        }
        CACHE.put(uuid, new Entry(uuid, username, raceId));
        save();
    }

    public static String get(UUID uuid) {
        Entry e = CACHE.get(uuid);
        return e != null ? e.raceId : null;
    }

    public static String getByName(String username) {
        if (username == null) {
            return null;
        }
        for (Entry e : CACHE.values()) {
            if (e.username != null && e.username.equalsIgnoreCase(username)) {
                return e.raceId;
            }
        }
        return null;
    }

    public static void remove(UUID uuid) {
        if (uuid == null) {
            return;
        }
        CACHE.remove(uuid);
        save();
    }

    public record Entry(UUID uuid, String username, String raceId) {}
}
