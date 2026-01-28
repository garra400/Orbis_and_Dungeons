package com.garra400.racas.commands;

import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.TranslationManager;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Command to change the server language: /racesetlanguage <language>
 * Available languages: en (English), ru (Russian), pt_br (Brazilian Portuguese)
 */
public class SetLanguageCommand extends AbstractPlayerCommand {
    
    private final OptionalArg<String> languageArg;
    
    public SetLanguageCommand() {
        super("racesetlanguage", "Change the server language", true);
        this.languageArg = withOptionalArg("language", "Language code (en, ru, pt_br)", ArgTypes.STRING);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        String langCode = languageArg.get(ctx);
        
        // If no argument provided, show current language and available languages
        if (langCode == null || langCode.isEmpty()) {
            String currentLang = TranslationManager.getCurrentLanguage();
            Map<String, String> availableLangs = TranslationManager.getAvailableLanguages();
            String langList = availableLangs.entrySet().stream()
                    .map(e -> e.getKey() + " (" + e.getValue() + ")")
                    .collect(Collectors.joining(", "));
            
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.racesetlanguage.current", 
                            availableLangs.getOrDefault(currentLang, currentLang))));
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.racesetlanguage.available", langList)));
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.racesetlanguage.usage")));
            return;
        }
        
        // Normalize language code
        langCode = langCode.toLowerCase().replace("-", "_");
        
        // Check if language exists
        if (!TranslationManager.isLanguageAvailable(langCode)) {
            Map<String, String> availableLangs = TranslationManager.getAvailableLanguages();
            String langList = availableLangs.keySet().stream().collect(Collectors.joining(", "));
            
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.racesetlanguage.invalid", langCode)));
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.racesetlanguage.available", langList)));
            return;
        }
        
        // Change language
        if (TranslationManager.setLanguage(langCode)) {
            String languageName = TranslationManager.getAvailableLanguages().get(langCode);
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.racesetlanguage.success", languageName)));
        } else {
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.racesetlanguage.invalid", langCode)));
        }
    }
}
