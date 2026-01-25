package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.RaceMod;
import com.garra400.racas.components.RaceData;
import com.garra400.racas.races.RaceDefinition;
import com.garra400.racas.races.RaceRegistry;
import com.garra400.racas.storage.config.ClassConfig;
import com.garra400.racas.storage.loader.ClassConfigLoader;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Race command collection: /race with subcommands
 * - trade [player] <race>: Change race
 * - reset [player]: Reset race and reopen UI
 * - info [player]: Show race information
 * 
 * Uses Hytale's argument system discovered from RPGLeveling mod:
 * - RequiredArg for mandatory arguments
 * - OptionalArg for optional arguments
 * - .get(context) to retrieve values
 */
public class RaceCommands extends AbstractCommandCollection {

    public RaceCommands() {
        super("race", "Commands for managing player races");
        addSubCommand(new TradeCommand());
        addSubCommand(new ResetCommand());
        addSubCommand(new InfoCommand());
        addSubCommand(new TradeClassCommand());
        addSubCommand(new ResetClassCommand());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    /**
     * /race trade [player] <race>
     * Changes player's race to ELF, ORC, DWARF, or HUMAN
     */
    private static class TradeCommand extends AbstractAsyncCommand {
        private final OptionalArg<String> playerArg;
        private final RequiredArg<String> raceArg;

        public TradeCommand() {
            super("trade", "Change a player's race");
            this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
            this.raceArg = withRequiredArg("race", "Race name (HUMAN/ELF/DWARF/ORC)", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected CompletableFuture<Void> executeAsync(CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                try {
                    // Get race argument (required)
                    String raceName = raceArg.get(context);
                    String raceId = raceName != null ? raceName.toLowerCase() : null;
                    if (!RaceRegistry.exists(raceId)) {
                        context.sendMessage(Message.raw("§cInvalid race: " + raceName + ". Valid: " + listValid()));
                        return;
                    }

                    // Get target player (optional, defaults to command sender)
                    String playerName = playerArg.get(context);
                    PlayerRef targetRef;
                    
                    if (playerName == null || playerName.isEmpty()) {
                        // Self-targeting - sender is already a PlayerRef
                        if (!(context.sender() instanceof PlayerRef)) {
                            context.sendMessage(Message.raw("§cOnly players can use this command"));
                            return;
                        }
                        targetRef = (PlayerRef) context.sender();
                    } else {
                        // Target another player by username
                        targetRef = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
                        if (targetRef == null) {
                            context.sendMessage(Message.raw("§cPlayer not found: " + playerName));
                            return;
                        }
                    }

                    // Get online player instance from Universe
                    UUID worldUuid = targetRef.getWorldUuid();
                    if (worldUuid == null) {
                        context.sendMessage(Message.raw("§cCannot find player's world"));
                        return;
                    }
                    UUID uuid = targetRef.getUuid();
                    Player player = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
                    if (player == null) {
                        context.sendMessage(Message.raw("§cPlayer is not online"));
                        return;
                    }

                    // Apply race
                    RaceManager.applyRace(player, raceId, targetRef);
                    
                    // Send confirmation
                    if (playerName == null || playerName.isEmpty()) {
                        context.sendMessage(Message.raw("§aYour race has been changed to " + raceId + "!"));
                    } else {
                        context.sendMessage(Message.raw("§aChanged " + targetRef.getUsername() + "'s race to " + raceId + "!"));
                        player.sendMessage(Message.raw("§eYour race has been changed to " + raceId + " by an administrator."));
                    }
                    
                } catch (Exception e) {
                    context.sendMessage(Message.raw("§cError changing race: " + e.getMessage()));
                }
            });
        }
    }

    /**
     * /race reset [player]
     * Resets player's race and reopens selection UI
     */
    private static class ResetCommand extends AbstractAsyncCommand {
        private final OptionalArg<String> playerArg;

        public ResetCommand() {
            super("reset", "Reset a player's race and reopen selection UI");
            this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected CompletableFuture<Void> executeAsync(CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                try {
                    // Get target player (optional, defaults to command sender)
                    String playerName = playerArg.get(context);
                    PlayerRef targetRef;
                    
                    if (playerName == null || playerName.isEmpty()) {
                        // Self-targeting - sender is already a PlayerRef
                        if (!(context.sender() instanceof PlayerRef)) {
                            context.sendMessage(Message.raw("§cOnly players can use this command"));
                            return;
                        }
                        targetRef = (PlayerRef) context.sender();
                    } else {
                        // Target another player by username
                        targetRef = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
                        if (targetRef == null) {
                            context.sendMessage(Message.raw("§cPlayer not found: " + playerName));
                            return;
                        }
                    }

                    // Get online player instance from Universe
                    UUID worldUuid = targetRef.getWorldUuid();
                    if (worldUuid == null) {
                        context.sendMessage(Message.raw("§cCannot find player's world"));
                        return;
                    }
                    UUID uuid = targetRef.getUuid();
                    Player player = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
                    if (player == null) {
                        context.sendMessage(Message.raw("§cPlayer is not online"));
                        return;
                    }

                    // Reset race
                    boolean success = RaceManager.resetRace(player, targetRef);
                    if (!success) {
                        context.sendMessage(Message.raw("§cFailed to reset race data."));
                        return;
                    }

                    // Send confirmation
                    if (playerName == null || playerName.isEmpty()) {
                        context.sendMessage(Message.raw("§aYour race has been reset. Reconnect to select a new race."));
                    } else {
                        context.sendMessage(Message.raw("§aReset " + targetRef.getUsername() + "'s race."));
                        player.sendMessage(Message.raw("§eYour race has been reset by an administrator. Reconnect to select a new race."));
                    }
                    
                } catch (Exception e) {
                    context.sendMessage(Message.raw("§cError resetting race: " + e.getMessage()));
                }
            });
        }
    }

    /**
     * /race info [player]
     * Shows race information for a player
     */
    private static class InfoCommand extends AbstractAsyncCommand {
        private final OptionalArg<String> playerArg;

        public InfoCommand() {
            super("info", "Show race information for a player");
            this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected CompletableFuture<Void> executeAsync(CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                try {
                    // Get target player (optional, defaults to command sender)
                    String playerName = playerArg.get(context);
                    PlayerRef targetRef;
                    
                    if (playerName == null || playerName.isEmpty()) {
                        // Self-targeting - sender is already a PlayerRef
                        if (!(context.sender() instanceof PlayerRef)) {
                            context.sendMessage(Message.raw("§cOnly players can use this command"));
                            return;
                        }
                        targetRef = (PlayerRef) context.sender();
                    } else {
                        // Target another player by username
                        targetRef = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
                        if (targetRef == null) {
                            context.sendMessage(Message.raw("§cPlayer not found: " + playerName));
                            return;
                        }
                    }

                    // Get online player instance from Universe
                    UUID worldUuid = targetRef.getWorldUuid();
                    if (worldUuid == null) {
                        context.sendMessage(Message.raw("§cCannot find player's world"));
                        return;
                    }
                    UUID uuid = targetRef.getUuid();
                    Player player = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
                    if (player == null) {
                        context.sendMessage(Message.raw("§cPlayer is not online"));
                        return;
                    }

                    // Get race and class info
                    String raceId = RaceManager.getPlayerRace(player);
                    String classId = RaceManager.getPlayerClass(player);
                    String targetName = targetRef.getUsername();

                    if (raceId == null) {
                        context.sendMessage(Message.raw("§cNo race selected for " + targetName));
                        return;
                    }

                    RaceDefinition race = RaceRegistry.get(raceId);
                    String raceName = race != null ? race.displayName() : raceId;
                    
                    String className = "None";
                    if (classId != null && !classId.equals("none")) {
                        ClassConfig classConfig = ClassConfigLoader.getClass(classId);
                        if (classConfig != null) {
                            className = classConfig.displayName;
                        }
                    }

                    context.sendMessage(Message.raw("§6=== Race Info for " + targetName + " ==="));
                    context.sendMessage(Message.raw("§eRace: " + raceName));
                    context.sendMessage(Message.raw("§eClass: " + className));

                    // Show timestamp if available
                    if (RaceMod.getRaceDataType() != null && targetRef.getHolder() != null) {
                        RaceData data = targetRef.getHolder().getComponent(RaceMod.getRaceDataType());
                        if (data != null && data.getSelectionTimestamp() != null && !data.getSelectionTimestamp().isEmpty()) {
                            context.sendMessage(Message.raw("§7Selected: " + data.getSelectionDateFormatted()));
                            long days = data.getDaysSinceSelection();
                            if (days >= 0) {
                                context.sendMessage(Message.raw("§7Days ago: " + days));
                            }
                        }
                    }

                    context.sendMessage(Message.raw("§6=============================="));
                    
                } catch (Exception e) {
                    context.sendMessage(Message.raw("§cError getting race info: " + e.getMessage()));
                }
            });
        }
    }

    /**
     * /race tradeclass [player] <class>
     * Changes player's class without affecting race
     */
    private static class TradeClassCommand extends AbstractAsyncCommand {
        private final OptionalArg<String> playerArg;
        private final RequiredArg<String> classArg;

        public TradeClassCommand() {
            super("class", "Change a player's class");
            this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
            this.classArg = withRequiredArg("class", "Class name (NONE/BERSERKER/SWORDSMAN/CRUSADER/ASSASSIN/ARCHER)", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected CompletableFuture<Void> executeAsync(CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                try {
                    // Get class argument (required)
                    String className = classArg.get(context);
                    String classId = className != null ? className.toLowerCase() : null;
                    if (!ClassConfigLoader.hasConfig(classId)) {
                        context.sendMessage(Message.raw("§cInvalid class: " + className + ". Valid: " + listValidClasses()));
                        return;
                    }

                    // Get target player (optional, defaults to command sender)
                    String playerName = playerArg.get(context);
                    PlayerRef targetRef;
                    
                    if (playerName == null || playerName.isEmpty()) {
                        if (!(context.sender() instanceof PlayerRef)) {
                            context.sendMessage(Message.raw("§cOnly players can use this command"));
                            return;
                        }
                        targetRef = (PlayerRef) context.sender();
                    } else {
                        targetRef = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
                        if (targetRef == null) {
                            context.sendMessage(Message.raw("§cPlayer not found: " + playerName));
                            return;
                        }
                    }

                    // Get online player instance
                    UUID worldUuid = targetRef.getWorldUuid();
                    if (worldUuid == null) {
                        context.sendMessage(Message.raw("§cCannot find player's world"));
                        return;
                    }
                    UUID uuid = targetRef.getUuid();
                    Player player = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
                    if (player == null) {
                        context.sendMessage(Message.raw("§cPlayer is not online"));
                        return;
                    }

                    // Get current race
                    String raceId = RaceManager.getPlayerRace(player);
                    if (raceId == null) {
                        context.sendMessage(Message.raw("§cPlayer must select a race first!"));
                        return;
                    }

                    // Apply race + new class
                    RaceManager.applyRaceAndClass(player, raceId, classId);
                    
                    // Send confirmation
                    ClassConfig newClass = ClassConfigLoader.getClass(classId);
                    String newClassName = newClass != null ? newClass.displayName : classId;
                    
                    if (playerName == null || playerName.isEmpty()) {
                        context.sendMessage(Message.raw("§aYour class has been changed to " + newClassName + "!"));
                    } else {
                        context.sendMessage(Message.raw("§aChanged " + targetRef.getUsername() + "'s class to " + newClassName + "!"));
                        player.sendMessage(Message.raw("§eYour class has been changed to " + newClassName + " by an administrator."));
                    }
                    
                } catch (Exception e) {
                    context.sendMessage(Message.raw("§cError changing class: " + e.getMessage()));
                }
            });
        }
    }

    /**
     * /race clearclass [player]
     * Resets a player's class to NONE
     */
    private static class ResetClassCommand extends AbstractAsyncCommand {
        private final OptionalArg<String> playerArg;

        public ResetClassCommand() {
            super("clearclass", "Reset a player's class to NONE");
            this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected CompletableFuture<Void> executeAsync(CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                try {
                    // Get target player (optional, defaults to command sender)
                    String playerName = playerArg.get(context);
                    PlayerRef targetRef;
                    
                    if (playerName == null || playerName.isEmpty()) {
                        if (!(context.sender() instanceof PlayerRef)) {
                            context.sendMessage(Message.raw("§cOnly players can use this command"));
                            return;
                        }
                        targetRef = (PlayerRef) context.sender();
                    } else {
                        targetRef = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
                        if (targetRef == null) {
                            context.sendMessage(Message.raw("§cPlayer not found: " + playerName));
                            return;
                        }
                    }

                    // Get online player instance
                    UUID worldUuid = targetRef.getWorldUuid();
                    if (worldUuid == null) {
                        context.sendMessage(Message.raw("§cCannot find player's world"));
                        return;
                    }
                    UUID uuid = targetRef.getUuid();
                    Player player = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
                    if (player == null) {
                        context.sendMessage(Message.raw("§cPlayer is not online"));
                        return;
                    }

                    // Get current race
                    String raceId = RaceManager.getPlayerRace(player);
                    if (raceId == null) {
                        context.sendMessage(Message.raw("§cPlayer must select a race first!"));
                        return;
                    }

                    // Reset class to "none"
                    RaceManager.applyRaceAndClass(player, raceId, "none");
                    
                    // Send confirmation
                    if (playerName == null || playerName.isEmpty()) {
                        context.sendMessage(Message.raw("§aYour class has been reset to None!"));
                    } else {
                        context.sendMessage(Message.raw("§aReset " + targetRef.getUsername() + "'s class to None!"));
                        player.sendMessage(Message.raw("§eYour class has been reset to None by an administrator."));
                    }
                    
                } catch (Exception e) {
                    context.sendMessage(Message.raw("§cError resetting class: " + e.getMessage()));
                }
            });
        }
    }

    private static String listValid() {
        return RaceRegistry.all().stream()
                .map(RaceDefinition::id)
                .collect(Collectors.joining(", "));
    }

    private static String listValidClasses() {
        return ClassConfigLoader.getAllClasses().stream()
                .map(c -> c.id)
                .collect(Collectors.joining(", "));
    }
}
