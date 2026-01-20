package com.garra400.racas.ui;

import com.garra400.racas.RaceManager;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import org.bson.BsonDocument;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pagina de selecao de racas. Usa o arquivo common/ui/custom/pages/race_selection.ui.
 * Mantem a tela travada (CantClose) ate clicar em "Selecionar".
 */
public class RaceSelectionPage extends CustomUIPage {

    private static final class RaceDetails {
        final String title;
        final String tagline;
        final List<String> positives;
        final List<String> negatives;

        RaceDetails(String title, String tagline, List<String> positives, List<String> negatives) {
            this.title = title;
            this.tagline = tagline;
            this.positives = positives;
            this.negatives = negatives;
        }
    }

    private static final Map<String, RaceDetails> RACES;
    static {
        Map<String, RaceDetails> tmp = new LinkedHashMap<>();
        tmp.put("elf", new RaceDetails(
                "Elfo",
                "Agil e preciso, otima sinergia com arco e magia leve.",
                List.of(
                        "Corpse Smell: Undead e Phantoms ficam neutros.",
                        "Keen Senses: bonus de critico a distancia.",
                        "Wind Step: ligeiro bonus de velocidade ao pular."
                ),
                List.of(
                        "Fragile Frame: recebe mais dano fisico.",
                        "Lean Appetite: fome drena um pouco mais rapido."
                )
        ));
        tmp.put("orc", new RaceDetails(
                "Orc",
                "Forca bruta, frontliner que aguenta pancada.",
                List.of(
                        "Brutal Strikes: dano corpo-a-corpo aumentado.",
                        "Thick Skin: resistencia fisica melhorada.",
                        "Battle Hunger: cura leve ao eliminar inimigos."
                ),
                List.of(
                        "Blunt Mind: penalidade leve em magia.",
                        "Hearty Diet: consome mais comida por tick."
                )
        ));
        tmp.put("human", new RaceDetails(
                "Humano",
                "Versatil e equilibrado, se adapta a qualquer funcao.",
                List.of(
                        "Adaptive: bonus moderado em todas as proficiencias.",
                        "Industrious: pequenas reducoes de tempo de craft.",
                        "Diplomatic: melhor relacao com NPCs neutros."
                ),
                List.of(
                        "Average Body: sem resistencias naturais.",
                        "No Specialty: bonus menores que racas focadas."
                )
        ));
        RACES = Collections.unmodifiableMap(tmp);
    }

    private String selectedRace = "elf";

    public RaceSelectionPage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CantClose);
    }

    @Override
    public void build(Ref<EntityStore> playerStoreRef, UICommandBuilder command, UIEventBuilder events, Store<EntityStore> store) {
        // 1. Carrega o arquivo .ui (caminho relativo a custom/pages/)
        // Lookup padrão do Hytale: "race_selection" => common/ui/custom/pages/race_selection.ui
        command.append("Custom/Pages/race_selection");

        // 2. Vincula cliques
        events.addEventBinding(CustomUIEventBindingType.Activating, "RaceButtonElf",
                EventData.of("event", "select").append("race", "elf"), true);
        events.addEventBinding(CustomUIEventBindingType.Activating, "RaceButtonOrc",
                EventData.of("event", "select").append("race", "orc"), true);
        events.addEventBinding(CustomUIEventBindingType.Activating, "RaceButtonHuman",
                EventData.of("event", "select").append("race", "human"), true);

        // Confirmar
        events.addEventBinding(CustomUIEventBindingType.Activating, "ConfirmSelection",
                EventData.of("event", "confirm").append("race", selectedRace), true);

        applySelection(command, selectedRace);
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> playerStoreRef, Store<EntityStore> store, String rawJson) {
        Map<String, String> data = decodeEvent(rawJson);
        String event = data.get("event");
        if ("select".equals(event)) {
            String race = data.get("race");
            if (race != null && RACES.containsKey(race)) {
                selectedRace = race;
                UICommandBuilder cmd = new UICommandBuilder();
                applySelection(cmd, race);
                sendUpdate(cmd, false);
            }
            return;
        }

        if ("confirm".equals(event)) {
            String raceFromEvent = data.get("race");
            if (raceFromEvent != null) {
                selectedRace = raceFromEvent;
            }
            Player player = store != null ? store.getComponent(playerStoreRef, Player.getComponentType()) : null;
            RaceManager.Race race = RaceManager.fromKey(selectedRace);
            if (player != null) {
                RaceManager.applyRace(player, race);
            }
            close();
        }
    }

    private Map<String, String> decodeEvent(String rawJson) {
        if (rawJson == null || rawJson.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            ExtraInfo extra = ExtraInfo.THREAD_LOCAL.get();
            Map<String, String> map = MapCodec.STRING_HASH_MAP_CODEC.decode(BsonDocument.parse(rawJson), extra);
            return map != null ? map : Collections.emptyMap();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private void applySelection(UICommandBuilder command, String raceKey) {
        RaceDetails details = RACES.getOrDefault(raceKey, RACES.get("human"));
        command.set("SelectedRaceName.Text", details.title);
        command.set("SelectedRaceTagline.Text", details.tagline);

        // Positivos
        setListLine(command, "PositiveLine1", details.positives, 0);
        setListLine(command, "PositiveLine2", details.positives, 1);
        setListLine(command, "PositiveLine3", details.positives, 2);

        // Negativos
        setListLine(command, "NegativeLine1", details.negatives, 0);
        setListLine(command, "NegativeLine2", details.negatives, 1);
    }

    private void setListLine(UICommandBuilder command, String elementId, List<String> lines, int index) {
        String value = index < lines.size() ? lines.get(index) : "";
        command.set(elementId + ".Text", value);
    }
}
