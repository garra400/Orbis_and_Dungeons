package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.storage.config.ClassConfig;
import com.garra400.racas.storage.loader.ClassConfigLoader;
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
import java.util.stream.Collectors;

/**
 * /tradeclass [player] <class>
 * Changes player's class to the specified class
 */
public class TradeClassCommand extends AbstractPlayerCommand {
    private final OptionalArg<String> playerArg;
    private final RequiredArg<String> classArg;

    public TradeClassCommand() {
        super("tradeclass", "Change a player's class", false);
        this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
        this.classArg = withRequiredArg("class", "Class name", ArgTypes.STRING);
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
            // Get class argument (required)
            String className = classArg.get(ctx);
            String classId = className != null ? className.toLowerCase() : null;
            if (!ClassConfigLoader.hasConfig(classId)) {
                ctx.sendMessage(ColorConverter.message("&cInvalid class: " + className + ". Valid: " + listValidClasses()));
                return;
            }

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
                    ctx.sendMessage(ColorConverter.message("&cPlayer not found: " + playerName));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message("&cCannot find player's world"));
                    return;
                }
                UUID uuid = targetRef.getUuid();
                onlinePlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
            }

            if (onlinePlayer == null) {
                ctx.sendMessage(ColorConverter.message("&cPlayer is not online"));
                return;
            }

            // Get current race (preserve it)
            String currentRace = RaceManager.getPlayerRace(onlinePlayer);
            if (currentRace == null || currentRace.equals("none")) {
                ctx.sendMessage(ColorConverter.message("&cYou must select a race first! Use /racetrade <race>"));
                return;
            }

            // Apply new class while keeping current race
            RaceManager.applyRaceAndClass(onlinePlayer, currentRace, classId);

            // Get display names for confirmation
            ClassConfig config = ClassConfigLoader.getConfig(classId);
            String displayClass = config != null ? config.displayName : classId.toUpperCase();
            String targetName = targetRef.getUsername();

            if (targetRef.equals(playerRef)) {
                ctx.sendMessage(ColorConverter.message("&aYour class has been changed to: &e" + displayClass));
            } else {
                ctx.sendMessage(ColorConverter.message("&aChanged " + targetName + "'s class to: &e" + displayClass));
            }

        } catch (Exception ex) {
            ctx.sendMessage(ColorConverter.message("&cError changing class: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }

    private String listValidClasses() {
        return ClassConfigLoader.getAllConfigs().stream()
                .map(c -> c.displayName)
                .collect(Collectors.joining(", "));
    }
}