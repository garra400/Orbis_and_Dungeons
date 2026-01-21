package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.RaceMod;
import com.garra400.racas.components.RaceData;
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
                    RaceManager.Race race;
                    try {
                        race = RaceManager.Race.valueOf(raceName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        context.sendMessage(Message.raw("§cInvalid race: " + raceName + ". Valid: HUMAN, ELF, ORC"));
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
                    RaceManager.applyRace(player, race, targetRef);
                    
                    // Send confirmation
                    if (playerName == null || playerName.isEmpty()) {
                        context.sendMessage(Message.raw("§aYour race has been changed to " + race.name() + "!"));
                    } else {
                        context.sendMessage(Message.raw("§aChanged " + targetRef.getUsername() + "'s race to " + race.name() + "!"));
                        player.sendMessage(Message.raw("§eYour race has been changed to " + race.name() + " by an administrator."));
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

                    // Get race info
                    String info = RaceManager.getPlayerRaceInfo(player);
                    String targetName = targetRef.getUsername();

                    context.sendMessage(Message.raw("§6=== Race Info for " + targetName + " ==="));
                    context.sendMessage(Message.raw("§e" + info));

                    // Show timestamp if available
                    if (targetRef.getHolder() != null) {
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
}
