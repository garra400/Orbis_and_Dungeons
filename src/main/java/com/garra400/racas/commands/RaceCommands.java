package com.garra400.racas.commands;

import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.garra400.racas.RaceManager;
import com.garra400.racas.RaceMod;
import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.components.RaceData;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.races.RaceDefinition;
import com.garra400.racas.races.RaceRegistry;
import com.garra400.racas.storage.loader.RaceConfigLoader;
import com.garra400.racas.ui.RaceSelectionPage;
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

/**
 * Unified Race Command Collection: /race
 * 
 * Subcommands:
 * - /race select              - Opens race selection UI
 * - /race change <race>       - Change race directly
 * - /race reset               - Reset race to none
 * - /race info                - Show current race info
 * - /race reload              - Reload race configurations (admin)
 * 
 * All commands support --player <name> for targeting other players (admin feature)
 */
public class RaceCommands extends AbstractCommandCollection {

    public RaceCommands() {
        super("race", "Race management commands");
        addSubCommand(new SelectCommand());
        addSubCommand(new ChangeCommand());
        addSubCommand(new ResetCommand());
        addSubCommand(new InfoCommand());
        addSubCommand(new ReloadCommand());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    // ==================== /race select ====================
    
    /**
     * /race select [--player <name>]
     * Opens race selection UI for choosing/changing race
     */
    private static class SelectCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> playerArg;

        public SelectCommand() {
            super("select", "Open race selection UI", false);
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
                        TranslationManager.translate("command.race.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.race.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
                if (targetPlayer == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.race.not_online")));
                    return;
                }
                
                // For other players, we need their refs
                targetEntityRef = null;
                targetStore = null;
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.error_player_data")));
                return;
            }

            PageManager pages = targetPlayer.getPageManager();
            
            if (pages.getCustomPage() instanceof RaceSelectionPage) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.select.already_open")));
                return;
            }

            try {
                String currentRace = RaceManager.getPlayerRace(targetPlayer);
                if (currentRace != null && !currentRace.equals("none")) {
                    pages.openCustomPage(targetEntityRef != null ? targetEntityRef : ref, 
                                        targetStore != null ? targetStore : store, 
                                        new RaceSelectionPage(targetRef, currentRace, 0, false, null));
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.race.select.opening_reselect")));
                } else {
                    pages.openCustomPage(targetEntityRef != null ? targetEntityRef : ref, 
                                        targetStore != null ? targetStore : store, 
                                        new RaceSelectionPage(targetRef));
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.race.select.opening")));
                }
            } catch (Exception e) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.select.failed", e.getMessage())));
            }
        }
    }

    // ==================== /race change ====================
    
    /**
     * /race change <race> [--player <name>]
     * Changes race directly without UI
     */
    private static class ChangeCommand extends AbstractPlayerCommand {
        private final RequiredArg<String> raceArg;
        private final OptionalArg<String> playerArg;

        public ChangeCommand() {
            super("change", "Change race directly", false);
            this.raceArg = withRequiredArg("race", "Race to change to (" + listValidRaces() + ")", ArgTypes.STRING);
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
            String raceName = raceArg.get(ctx);
            String raceId = raceName != null ? raceName.toLowerCase() : null;
            
            if (!RaceRegistry.exists(raceId)) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.change.invalid", raceName)));
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.change.valid", listValidRaces())));
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
                        TranslationManager.translate("command.race.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.race.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.not_online")));
                return;
            }

            RaceManager.applyRace(targetPlayer, raceId, targetRef);
            
            RaceDefinition race = RaceRegistry.get(raceId);
            String displayName = race != null ? race.displayName() : raceId;
            
            if (targetName == null || targetName.isEmpty()) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.change.success_self", displayName)));
            } else {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.change.success_other", targetRef.getUsername(), displayName)));
                targetPlayer.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.change.by_admin", displayName)));
            }
        }
    }

    // ==================== /race reset ====================
    
    /**
     * /race reset [--player <name>]
     * Resets race to none and opens race selection UI
     */
    private static class ResetCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> playerArg;

        public ResetCommand() {
            super("reset", "Reset race and open selection UI", false);
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
                        TranslationManager.translate("command.race.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.race.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.not_online")));
                return;
            }

            // Save current class before resetting (so we can keep it after race change)
            String savedClass = RaceManager.getPlayerClass(targetPlayer);
            if (savedClass == null) savedClass = "none";

            boolean success = RaceManager.resetRace(targetPlayer, targetRef);
            
            if (!success) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reset.failed")));
                return;
            }
            
            if (targetName == null || targetName.isEmpty()) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reset.success_self")));
            } else {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reset.success_other", targetRef.getUsername())));
                targetPlayer.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reset.by_admin")));
            }

            // Auto-open race selection UI (race-only mode - keeps existing class)
            try {
                PageManager pages = targetPlayer.getPageManager();
                pages.openCustomPage(ref, store, new RaceSelectionPage(targetRef, true, savedClass));
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reset.opening_ui")));
            } catch (Exception e) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reset.ui_failed")));
            }
        }
    }

    // ==================== /race info ====================
    
    /**
     * /race info [--player <name>]
     * Shows race information
     */
    private static class InfoCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> playerArg;

        public InfoCommand() {
            super("info", "Show race information", false);
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
                        TranslationManager.translate("command.race.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.race.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.not_online")));
                return;
            }

            String raceId = RaceManager.getPlayerRace(targetPlayer);
            String displayTargetName = targetRef.getUsername();

            if (raceId == null || raceId.equals("none")) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.info.no_race", displayTargetName)));
                return;
            }

            RaceDefinition race = RaceRegistry.get(raceId);
            String raceName = race != null ? race.displayName() : raceId;

            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.race.info.title", displayTargetName)));
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.race.info.race", raceName)));

            // Show timestamp if available
            if (RaceMod.getRaceDataType() != null && targetRef.getHolder() != null) {
                RaceData data = targetRef.getHolder().getComponent(RaceMod.getRaceDataType());
                if (data != null && data.getSelectionTimestamp() != null && !data.getSelectionTimestamp().isEmpty()) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.race.info.selected", data.getSelectionDateFormatted())));
                    long days = data.getDaysSinceSelection();
                    if (days >= 0) {
                        ctx.sendMessage(ColorConverter.message(
                            TranslationManager.translate("command.race.info.days_ago", days)));
                    }
                }
            }
        }
    }

    // ==================== /race reload ====================
    
    /**
     * /race reload
     * Reloads race configuration files (admin command)
     */
    private static class ReloadCommand extends AbstractPlayerCommand {

        public ReloadCommand() {
            super("reload", "Reload race configurations", false);
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
            try {
                RaceConfigLoader.reload();
                RaceRegistry.loadFromConfig();
                
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reload.success")));
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reload.updated")));
            } catch (Exception e) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.race.reload.failed", e.getMessage())));
            }
        }
    }

    // ==================== Helper Methods ====================
    
    private static String listValidRaces() {
        return RaceRegistry.all().stream()
                .map(RaceDefinition::id)
                .collect(Collectors.joining(", "));
    }
}
