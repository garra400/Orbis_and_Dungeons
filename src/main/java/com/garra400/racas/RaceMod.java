package com.garra400.racas;

import com.garra400.racas.ui.RaceSelectionPage;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

/**
 * Entry point do mod de racas: registra o listener para abrir a UI assim que o jogador estiver pronto.
 */
public class RaceMod extends JavaPlugin {

    public RaceMod(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        EventRegistry events = getEventRegistry();
        events.registerGlobal(PlayerReadyEvent.class, this::openRacePageOnJoin);
    }

    private void openRacePageOnJoin(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        PageManager pages = player.getPageManager();

        // Garante que so abre uma vez e que o playerRef tem store associado.
        if (pages.getCustomPage() instanceof RaceSelectionPage) {
            return;
        }
        var playerRef = player.getPlayerRef();
        var ref = event.getPlayerRef();
        var store = ref != null ? ref.getStore() : null;
        if (store == null) {
            return;
        }

        pages.openCustomPage(ref, store, new RaceSelectionPage(playerRef));
    }
}
