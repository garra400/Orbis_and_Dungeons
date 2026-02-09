package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.storage.config.ClassConfig;
import com.garra400.racas.storage.loader.ClassConfigLoader;
import com.garra400.racas.ui.ClassSelectionPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Unified Class Command Collection: /class
 * 
 * Subcommands:
 * - /class select              - Opens class selection UI
 * - /class change <class>      - Change class directly
 * - /class reset               - Reset class to none
 * - /class info                - Show current class info
 * 
 * All commands support --player <name> for targeting other players (admin feature)
 */
public class ClassCommands extends AbstractCommandCollection {

    public ClassCommands() {
        super("class", "Class management commands");
        addSubCommand(new SelectCommand());
        addSubCommand(new ChangeCommand());
        addSubCommand(new ResetCommand());
        addSubCommand(new InfoCommand());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    // ==================== /class select ====================
    
    /**
     * /class select [--player <name>]
     * Opens class selection UI for choosing/changing class
     */
    private static class SelectCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> playerArg;

        public SelectCommand() {
            super("select", "Open class selection UI", false);
            this.playerArg = withOptionalArg("player", "Target player (admin only)", ArgTypes.STRING);
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
            String targetName = playerArg.get(ctx);
            
            // Get target player
            PlayerRef targetRef;
            Player targetPlayer;
            Ref<EntityStore> targetEntityRef;
            Store<EntityStore> targetStore;
            
            if (targetName == null || targetName.isEmpty()) {
                targetRef = playerRef;
                targetPlayer = store.getComponent(ref, Player.getComponentType());
                targetEntityRef = ref;
                targetStore = store;
            } else {
                targetRef = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
                if (targetRef == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
                if (targetPlayer == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.not_online")));
                    return;
                }
                
                targetEntityRef = null;
                targetStore = null;
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.error_player_data")));
                return;
            }

            // Check if player has a race first
            String currentRace = RaceManager.getPlayerRace(targetPlayer);
            if (currentRace == null || currentRace.equals("none")) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.need_race_first")));
                return;
            }

            PageManager pages = targetPlayer.getPageManager();
            
            if (pages.getCustomPage() instanceof ClassSelectionPage) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.select.already_open")));
                return;
            }

            try {
                pages.openCustomPage(targetEntityRef != null ? targetEntityRef : ref, 
                                    targetStore != null ? targetStore : store, 
                                    new ClassSelectionPage(targetRef, currentRace));
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.select.opening")));
            } catch (Exception e) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.select.failed", e.getMessage())));
            }
        }
    }

    // ==================== /class change ====================
    
    /**
     * /class change <class> [--player <name>]
     * Changes class directly without UI
     */
    private static class ChangeCommand extends AbstractPlayerCommand {
        private final RequiredArg<String> classArg;
        private final OptionalArg<String> playerArg;

        public ChangeCommand() {
            super("change", "Change class directly", false);
            this.classArg = withRequiredArg("class", "Class to change to (" + listValidClasses() + ")", ArgTypes.STRING);
            this.playerArg = withOptionalArg("player", "Target player (admin only)", ArgTypes.STRING);
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
            String className = classArg.get(ctx);
            String classId = className != null ? className.toLowerCase() : null;
            
            if (!ClassConfigLoader.hasConfig(classId)) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.change.invalid", className)));
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.change.valid", listValidClasses())));
                return;
            }

            String targetName = playerArg.get(ctx);
            PlayerRef targetRef;
            Player targetPlayer;
            
            if (targetName == null || targetName.isEmpty()) {
                targetRef = playerRef;
                targetPlayer = store.getComponent(ref, Player.getComponentType());
            } else {
                targetRef = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
                if (targetRef == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.not_online")));
                return;
            }

            // Check if player has a race first
            String currentRace = RaceManager.getPlayerRace(targetPlayer);
            if (currentRace == null || currentRace.equals("none")) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.need_race_first")));
                return;
            }

            RaceManager.applyRaceAndClass(targetPlayer, currentRace, classId);
            
            ClassConfig classConfig = ClassConfigLoader.getClass(classId);
            String displayName = classConfig != null ? classConfig.displayName : classId;
            
            if (targetName == null || targetName.isEmpty()) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.change.success_self", displayName)));
            } else {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.change.success_other", targetRef.getUsername(), displayName)));
                targetPlayer.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.change.by_admin", displayName)));
            }
        }
    }

    // ==================== /class reset ====================
    
    /**
     * /class reset [--player <name>]
     * Resets class to none (keeps race)
     */
    private static class ResetCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> playerArg;

        public ResetCommand() {
            super("reset", "Reset class to none", false);
            this.playerArg = withOptionalArg("player", "Target player (admin only)", ArgTypes.STRING);
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
            String targetName = playerArg.get(ctx);
            PlayerRef targetRef;
            Player targetPlayer;
            
            if (targetName == null || targetName.isEmpty()) {
                targetRef = playerRef;
                targetPlayer = store.getComponent(ref, Player.getComponentType());
            } else {
                targetRef = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
                if (targetRef == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.not_online")));
                return;
            }

            // Check if player has a race first
            String currentRace = RaceManager.getPlayerRace(targetPlayer);
            if (currentRace == null || currentRace.equals("none")) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.need_race_first")));
                return;
            }

            // Reset class to "none"
            RaceManager.applyRaceAndClass(targetPlayer, currentRace, "none");
            
            if (targetName == null || targetName.isEmpty()) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.reset.success_self")));
            } else {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.reset.success_other", targetRef.getUsername())));
                targetPlayer.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.reset.by_admin")));
            }
        }
    }

    // ==================== /class info ====================
    
    /**
     * /class info [--player <name>]
     * Shows class information
     */
    private static class InfoCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> playerArg;

        public InfoCommand() {
            super("info", "Show class information", false);
            this.playerArg = withOptionalArg("player", "Target player", ArgTypes.STRING);
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
            String targetName = playerArg.get(ctx);
            PlayerRef targetRef;
            Player targetPlayer;
            
            if (targetName == null || targetName.isEmpty()) {
                targetRef = playerRef;
                targetPlayer = store.getComponent(ref, Player.getComponentType());
            } else {
                targetRef = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
                if (targetRef == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.class.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.not_online")));
                return;
            }

            String classId = RaceManager.getPlayerClass(targetPlayer);
            String displayTargetName = targetRef.getUsername();

            if (classId == null || classId.equals("none")) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.class.info.no_class", displayTargetName)));
                return;
            }

            ClassConfig classConfig = ClassConfigLoader.getClass(classId);
            String className = classConfig != null ? classConfig.displayName : classId;

            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.class.info.title", displayTargetName)));
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.class.info.class", className)));
        }
    }

    // ==================== Helper Methods ====================
    
    private static String listValidClasses() {
        return ClassConfigLoader.getAllClasses().stream()
                .map(c -> c.id)
                .collect(Collectors.joining(", "));
    }
}
