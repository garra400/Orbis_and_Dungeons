package com.garra400.racas.i18n;

import com.garra400.racas.color.ColorConverter;
import com.hypixel.hytale.server.core.Message;

/**
 * Helper class for translations with color codes
 * Combines TranslationManager and ColorConverter for convenience
 */
public class T {
    
    /**
     * Translate a key and apply color codes
     * @param key Translation key
     * @param args Format arguments
     * @return Message with translation and colors applied
     */
    public static Message t(String key, Object... args) {
        return ColorConverter.message(TranslationManager.translate(key, args));
    }
    
    /**
     * Translate a key without applying colors (raw string)
     * @param key Translation key
     * @param args Format arguments
     * @return Translated string without color processing
     */
    public static String raw(String key, Object... args) {
        return TranslationManager.translate(key, args);
    }
}
