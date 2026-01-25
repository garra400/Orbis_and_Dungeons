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
 * /resetclass [player]
 * Resets a player's class to NONE
 */
public class ResetClassCommand extends AbstractPlayerCommand {
    private final OptionalArg<String> playerArg;

    public ResetClassCommand() {
        super("resetclass", "Reset a player's class to NONE", false);
        this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
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
            // Get target player (optional, defaults to command sender)
            String playerName = playerArg.get(ctx);
            PlayerRef targetRef;
            Player onlinePlayer;
            
            if (playerName == null || playerName.isEmpty()) {
                // Self-targeting
                targetRef = playerRef;
                onlinePlayer = store.getComponent(ref, Player.getComponentType());
            } else {
                // Target another player by username
                targetRef = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
                if (targetRef == null) {
                    ctx.sendMessage(Message.raw("§cPlayer not found: " + playerName));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(Message.raw("§cCannot find player's world"));
                    return;
                }
                UUID uuid = targetRef.getUuid();
                onlinePlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
            }

            if (onlinePlayer == null) {
                ctx.sendMessage(Message.raw("§cPlayer is not online"));
                return;
            }

            // Get current race (preserve it)
            String currentRace = RaceManager.getPlayerRace(onlinePlayer);
            if (currentRace == null || currentRace.equals("none")) {
                ctx.sendMessage(Message.raw("§cYou must select a race first! Use /racetrade <race>"));
                return;
            }

            // Reset class to "none" while keeping current race
            RaceManager.applyRaceAndClass(onlinePlayer, currentRace, "none");

            String targetName = targetRef.getUsername();

            if (targetRef.equals(playerRef)) {
                ctx.sendMessage(Message.raw("§aYour class has been reset"));
            } else {
                ctx.sendMessage(Message.raw("§aReset " + targetName + "'s class"));
            }

        } catch (Exception ex) {
            ctx.sendMessage(Message.raw("§cError resetting class: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }
}