package com.garra400.racas;

import com.garra400.racas.ui.RaceSelectionPage;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.HashSet;
import java.util.Set;

/**
 * Entry point do mod de racas: registra o listener para abrir a UI assim que o jogador estiver pronto.
 * Abre a UI apenas uma vez - na primeira vez que o jogador entra no mundo.
 */
public class RaceMod extends JavaPlugin {

    // Armazena os PlayerRefs dos jogadores que ja escolheram uma raca
    // Persiste durante a sessao do servidor
    private static final Set<PlayerRef> playersWithRace = new HashSet<>();

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
        
        // Verifica se o jogador ja escolheu uma raca antes
        if (playersWithRace.contains(playerRef)) {
            return; // Ja escolheu, nao abre mais
        }

        var ref = event.getPlayerRef();
        var store = ref != null ? ref.getStore() : null;
        if (store == null) {
            return;
        }

        pages.openCustomPage(ref, store, new RaceSelectionPage(playerRef));
    }

    /**
     * Marca que o jogador ja escolheu uma raca.
     * Deve ser chamado quando a raca for aplicada.
     */
    public static void markRaceAsSelected(PlayerRef playerRef) {
        if (playerRef != null) {
            playersWithRace.add(playerRef);
        }
    }
}
