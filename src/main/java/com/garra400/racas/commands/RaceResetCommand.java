package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
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
 * Command to reset race: /racereset [--player <name>]
 * Uses AbstractPlayerCommand for reliability (like Basic UIs)
 * Supports optional --player argument to target other players
 */
public class RaceResetCommand extends AbstractPlayerCommand {
    
    private final OptionalArg<String> playerArg;
    
    public RaceResetCommand() {
        super("racereset", "Reset race selection", false);
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
        
        // Reset race
        try {
            boolean success = RaceManager.resetRace(targetPlayer, targetRef);
            if (success) {
                if (targetPlayerName == null || targetPlayerName.isEmpty()) {
                    ctx.sendMessage(Message.raw("Your race has been reset!"));
                    ctx.sendMessage(Message.raw("Reconnect to select a new race."));
                } else {
                    ctx.sendMessage(Message.raw("Reset " + targetRef.getUsername() + "'s race!"));
                    targetPlayer.sendMessage(Message.raw("Your race has been reset by an administrator. Reconnect to select a new race."));
                }
            } else {
                ctx.sendMessage(Message.raw("Failed to reset race data."));
            }
        } catch (Exception e) {
            ctx.sendMessage(Message.raw("Error resetting race: " + e.getMessage()));
        }
    }
}
