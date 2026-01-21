package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.RaceMod;
import com.garra400.racas.components.RaceData;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Command to view race info: /raceinfo [--player <name>]
 * Uses AbstractPlayerCommand for reliability (like Basic UIs)
 * Supports optional --player argument to target other players
 */
public class RaceInfoCommand extends AbstractPlayerCommand {
    
    private final OptionalArg<String> playerArg;
    
    public RaceInfoCommand() {
        super("raceinfo", "Show race information", false);
        this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
    }
    
    @Override
    protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        // Check if targeting another player
        String targetPlayerName = playerArg.get(ctx);
        PlayerRef targetRef;
        Player targetPlayer;
        
        if (targetPlayerName == null || targetPlayerName.isEmpty()) {
            // Self-targeting
            targetRef = playerRef;
            targetPlayer = store.getComponent(ref, Player.getComponentType());
            if (targetPlayer == null) {
                ctx.sendMessage(Message.raw("Error: Could not get player data"));
                return;
            }
        } else {
            // Target another player
            targetRef = Universe.get().getPlayerByUsername(targetPlayerName, NameMatching.EXACT_IGNORE_CASE);
            if (targetRef == null) {
                ctx.sendMessage(Message.raw("Player not found: " + targetPlayerName));
                return;
            }
            
            // Get target player entity
            UUID worldUuid = targetRef.getWorldUuid();
            if (worldUuid == null) {
                ctx.sendMessage(Message.raw("Target player is not in a world"));
                return;
            }
            
            UUID uuid = targetRef.getUuid();
            targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
            if (targetPlayer == null) {
                ctx.sendMessage(Message.raw("Target player is not online"));
                return;
            }
        }
        
        try {
            // Get race info
            String info = RaceManager.getPlayerRaceInfo(targetPlayer);
            String playerName = targetRef.getUsername();
            
            ctx.sendMessage(Message.raw("=== Race Info for " + playerName + " ==="));
            ctx.sendMessage(Message.raw(info));
            
            // Show timestamp if available
            if (targetRef.getHolder() != null) {
                RaceData data = targetRef.getHolder().getComponent(RaceMod.getRaceDataType());
                if (data != null && data.getSelectionTimestamp() != null && !data.getSelectionTimestamp().isEmpty()) {
                    ctx.sendMessage(Message.raw("Selected: " + data.getSelectionDateFormatted()));
                    long days = data.getDaysSinceSelection();
                    if (days >= 0) {
                        ctx.sendMessage(Message.raw("Days ago: " + days));
                    }
                }
            }
            
            ctx.sendMessage(Message.raw("=============================="));
        } catch (Exception e) {
            ctx.sendMessage(Message.raw("Error getting race info: " + e.getMessage()));
        }
    }
}
