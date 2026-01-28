package com.garra400.racas.i18n;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Translation Manager for Orbis and Dungeons
 * Manages language files and provides translation services
 */
public class TranslationManager {
    private static final Gson GSON = new Gson();
    private static final String DEFAULT_LANGUAGE = "en";
    private static String currentLanguage = DEFAULT_LANGUAGE;
    private static final Map<String, Map<String, String>> translations = new HashMap<>();
    private static File languagesDir;

    /**
     * Initialize the translation manager
     * Creates the languages directory if it doesn't exist and loads default translations
     */
    public static void initialize(File modsDir) {
        languagesDir = new File(modsDir, "languages");
        
        // Create languages directory if it doesn't exist
        if (!languagesDir.exists()) {
            languagesDir.mkdirs();
        }

        // Extract default language files from resources if they don't exist
        extractDefaultLanguageFiles();

        // Load all available translations
        loadAllTranslations();
        
        // Load language preference from config
        loadLanguagePreference();
    }

    /**
     * Extract default language files from resources to the languages folder
     */
    private static void extractDefaultLanguageFiles() {
        String[] languages = {"en", "ru", "pt_br"};
        
        for (String lang : languages) {
            File langFile = new File(languagesDir, lang + ".json");
            if (!langFile.exists()) {
                try {
                    // Try to load from resources
                    InputStream resourceStream = TranslationManager.class.getResourceAsStream("/languages/" + lang + ".json");
                    if (resourceStream != null) {
                        String content = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
                        Files.writeString(langFile.toPath(), content, StandardCharsets.UTF_8);
                        System.out.println("[Orbis] Extracted default language file: " + lang + ".json");
                    } else {
                        // Create empty file with default structure
                        createEmptyLanguageFile(langFile);
                    }
                } catch (IOException e) {
                    System.err.println("[Orbis] Failed to extract language file " + lang + ".json: " + e.getMessage());
                    createEmptyLanguageFile(langFile);
                }
            }
        }
    }

    /**
     * Create an empty language file with basic structure
     */
    private static void createEmptyLanguageFile(File file) {
        try {
            JsonObject emptyLang = new JsonObject();
            emptyLang.addProperty("language.name", "Unknown");
            emptyLang.addProperty("language.code", file.getName().replace(".json", ""));
            
            FileWriter writer = new FileWriter(file);
            GSON.toJson(emptyLang, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load all translation files from the languages directory
     */
    private static void loadAllTranslations() {
        translations.clear();
        
        if (!languagesDir.exists() || !languagesDir.isDirectory()) {
            System.err.println("[Orbis] Languages directory not found!");
            return;
        }

        File[] files = languagesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.err.println("[Orbis] No language files found!");
            return;
        }

        for (File file : files) {
            String langCode = file.getName().replace(".json", "");
            try {
                loadLanguageFile(langCode, file);
                System.out.println("[Orbis] Loaded language: " + langCode);
            } catch (IOException e) {
                System.err.println("[Orbis] Failed to load language file " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Load a specific language file
     */
    private static void loadLanguageFile(String langCode, File file) throws IOException {
        FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();

        Map<String, String> langMap = new HashMap<>();
        jsonObject.keySet().forEach(key -> {
            langMap.put(key, jsonObject.get(key).getAsString());
        });

        translations.put(langCode, langMap);
    }

    /**
     * Load language preference from config file
     */
    private static void loadLanguagePreference() {
        File configFile = new File(languagesDir.getParentFile(), "language_config.json");
        if (configFile.exists()) {
            try {
                FileReader reader = new FileReader(configFile, StandardCharsets.UTF_8);
                JsonObject config = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
                
                if (config.has("current_language")) {
                    String lang = config.get("current_language").getAsString();
                    if (translations.containsKey(lang)) {
                        currentLanguage = lang;
                        System.out.println("[Orbis] Loaded language preference: " + lang);
                    }
                }
            } catch (IOException e) {
                System.err.println("[Orbis] Failed to load language preference: " + e.getMessage());
            }
        }
    }

    /**
     * Save language preference to config file
     */
    private static void saveLanguagePreference() {
        File configFile = new File(languagesDir.getParentFile(), "language_config.json");
        try {
            JsonObject config = new JsonObject();
            config.addProperty("current_language", currentLanguage);
            
            FileWriter writer = new FileWriter(configFile);
            GSON.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("[Orbis] Failed to save language preference: " + e.getMessage());
        }
    }

    /**
     * Get a translated string by key
     * Falls back to English if key not found in current language
     * Falls back to key itself if not found in any language
     */
    public static String translate(String key, Object... args) {
        String translation = getTranslation(key);
        
        // Format with arguments if provided
        if (args.length > 0) {
            try {
                return String.format(translation, args);
            } catch (Exception e) {
                System.err.println("[Orbis] Failed to format translation for key: " + key);
                return translation;
            }
        }
        
        return translation;
    }

    /**
     * Get raw translation without formatting
     */
    private static String getTranslation(String key) {
        // Try current language
        Map<String, String> currentLangMap = translations.get(currentLanguage);
        if (currentLangMap != null && currentLangMap.containsKey(key)) {
            return currentLangMap.get(key);
        }

        // Fall back to English
        if (!currentLanguage.equals(DEFAULT_LANGUAGE)) {
            Map<String, String> defaultLangMap = translations.get(DEFAULT_LANGUAGE);
            if (defaultLangMap != null && defaultLangMap.containsKey(key)) {
                return defaultLangMap.get(key);
            }
        }

        // Fall back to key itself
        System.err.println("[Orbis] Missing translation key: " + key);
        return key;
    }

    /**
     * Set the current language
     * @param langCode Language code (e.g., "en", "ru", "pt_br")
     * @return true if language was successfully changed, false otherwise
     */
    public static boolean setLanguage(String langCode) {
        if (translations.containsKey(langCode)) {
            currentLanguage = langCode;
            saveLanguagePreference();
            System.out.println("[Orbis] Changed language to: " + langCode);
            return true;
        }
        System.err.println("[Orbis] Language not found: " + langCode);
        return false;
    }

    /**
     * Get the current language code
     */
    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Get all available languages
     */
    public static Map<String, String> getAvailableLanguages() {
        Map<String, String> languages = new HashMap<>();
        for (String langCode : translations.keySet()) {
            String languageName = translations.get(langCode).getOrDefault("language.name", langCode);
            languages.put(langCode, languageName);
        }
        return languages;
    }

    /**
     * Reload all translation files
     */
    public static void reload() {
        loadAllTranslations();
        loadLanguagePreference();
    }

    /**
     * Check if a language is available
     */
    public static boolean isLanguageAvailable(String langCode) {
        return translations.containsKey(langCode);
    }
}
