package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
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
 * Command to change race: /racetrade <race> [--player <name>]
 * Uses AbstractPlayerCommand for reliability (like Basic UIs)
 * Supports optional --player argument to target other players
 */
public class RaceTradeCommand extends AbstractPlayerCommand {
    
    private final RequiredArg<String> raceArg;
    private final OptionalArg<String> playerArg;
    
    public RaceTradeCommand() {
        super("racetrade", "Change race (HUMAN/ELF/ORC)", false);
        this.raceArg = withRequiredArg("race", "Race name (HUMAN/ELF/ORC)", ArgTypes.STRING);
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
        // Get race argument
        String raceName = raceArg.get(ctx);
        RaceManager.Race race;
        try {
            race = RaceManager.Race.valueOf(raceName.toUpperCase());
        } catch (IllegalArgumentException e) {
            ctx.sendMessage(Message.raw("Invalid race: " + raceName));
            ctx.sendMessage(Message.raw("Valid races: HUMAN, ELF, ORC"));
            return;
        }
        
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
        
        // Apply race
        try {
            RaceManager.applyRace(targetPlayer, race, targetRef);
            
            if (targetPlayerName == null || targetPlayerName.isEmpty()) {
                ctx.sendMessage(Message.raw("Your race has been changed to " + race.name() + "!"));
            } else {
                ctx.sendMessage(Message.raw("Changed " + targetRef.getUsername() + "'s race to " + race.name() + "!"));
                targetPlayer.sendMessage(Message.raw("Your race has been changed to " + race.name() + " by an administrator."));
            }
        } catch (Exception e) {
            ctx.sendMessage(Message.raw("Error changing race: " + e.getMessage()));
        }
    }
}
