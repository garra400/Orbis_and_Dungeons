package com.garra400.racas.commands;

import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.TranslationManager;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Language Command Collection: /language
 * 
 * Subcommands:
 * - /language set <code>    - Set the server language
 * - /language list          - List available languages
 * - /language current       - Show current language
 */
public class LanguageCommands extends AbstractCommandCollection {

    public LanguageCommands() {
        super("language", "Language management commands");
        addSubCommand(new SetCommand());
        addSubCommand(new ListCommand());
        addSubCommand(new CurrentCommand());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    // ==================== /language set ====================
    
    /**
     * /language set <code>
     * Sets the server language
     */
    private static class SetCommand extends AbstractPlayerCommand {
        private final RequiredArg<String> langArg;

        public SetCommand() {
            super("set", "Set the server language", false);
            this.langArg = withRequiredArg("code", "Language code (en, pt_br, es, ru)", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected void execute(
                @Nonnull CommandContext ctx,
                @Nonnull Store<EntityStore> store,
                @Nonnull Ref<EntityStore> ref,
                @Nonnull PlayerRef playerRef,
                @Nonnull World world
        ) {
            String langCode = langArg.get(ctx);
            
            // Normalize language code
            langCode = langCode.toLowerCase().replace("-", "_");
            
            // Check if language exists
            if (!TranslationManager.isLanguageAvailable(langCode)) {
                Map<String, String> availableLangs = TranslationManager.getAvailableLanguages();
                String langList = availableLangs.keySet().stream().collect(Collectors.joining(", "));
                
                ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.language.set.invalid", langCode)));
                ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.language.set.available", langList)));
                return;
            }
            
            // Change language
            if (TranslationManager.setLanguage(langCode)) {
                String languageName = TranslationManager.getAvailableLanguages().get(langCode);
                ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.language.set.success", languageName)));
            } else {
                ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.language.set.failed")));
            }
        }
    }

    // ==================== /language list ====================
    
    /**
     * /language list
     * Lists all available languages
     */
    private static class ListCommand extends AbstractPlayerCommand {

        public ListCommand() {
            super("list", "List available languages", false);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected void execute(
                @Nonnull CommandContext ctx,
                @Nonnull Store<EntityStore> store,
                @Nonnull Ref<EntityStore> ref,
                @Nonnull PlayerRef playerRef,
                @Nonnull World world
        ) {
            Map<String, String> availableLangs = TranslationManager.getAvailableLanguages();
            
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.language.list.title")));
            
            for (Map.Entry<String, String> entry : availableLangs.entrySet()) {
                String current = entry.getKey().equals(TranslationManager.getCurrentLanguage()) ? " §a(current)" : "";
                ctx.sendMessage(ColorConverter.message(
                        "  &f" + entry.getKey() + " &7- &f" + entry.getValue() + current));
            }
        }
    }

    // ==================== /language current ====================
    
    /**
     * /language current
     * Shows the current language
     */
    private static class CurrentCommand extends AbstractPlayerCommand {

        public CurrentCommand() {
            super("current", "Show current language", false);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected void execute(
                @Nonnull CommandContext ctx,
                @Nonnull Store<EntityStore> store,
                @Nonnull Ref<EntityStore> ref,
                @Nonnull PlayerRef playerRef,
                @Nonnull World world
        ) {
            String currentLang = TranslationManager.getCurrentLanguage();
            String languageName = TranslationManager.getAvailableLanguages().getOrDefault(currentLang, currentLang);
            
            ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.language.current", languageName)));
        }
    }
}
