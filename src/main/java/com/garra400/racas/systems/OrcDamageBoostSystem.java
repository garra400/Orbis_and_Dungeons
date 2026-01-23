package com.garra400.racas.systems;

import com.garra400.racas.RaceManager;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Locale;

// Multiplica o dano causado por Orcs quando usam battleaxes ou axes.
public class OrcDamageBoostSystem extends DamageEventSystem {

    private static final Query<EntityStore> QUERY = AllLegacyLivingEntityTypesQuery.INSTANCE;
    private static final float ORC_WEAPON_MULTIPLIER = 1.5f;

    @Override
    public SystemGroup<EntityStore> getGroup() {
        // Executa cedo para garantir que outras reduções ainda usem o valor ampliado.
        return DamageModule.get().getGatherDamageGroup();
    }

    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer, Damage damage) {
        if (damage == null || damage.isCancelled()) {
            return;
        }

        if (!(damage.getSource() instanceof Damage.EntitySource entitySource)) {
            return;
        }

        Ref<EntityStore> attackerRef = entitySource.getRef();
        if (attackerRef == null || !attackerRef.isValid()) {
            return;
        }

        var attacker = EntityUtils.getEntity(attackerRef, commandBuffer);
        if (!(attacker instanceof Player player)) {
            return;
        }

        if (!isOrc(player)) {
            return;
        }

        var inventory = player.getInventory();
        if (inventory == null) {
            return;
        }

        ItemStack weapon = inventory.getItemInHand();
        if (weapon == null || weapon.isEmpty()) {
            return;
        }

        if (isAxeOrBattleAxe(weapon)) {
            damage.setAmount(damage.getAmount() * ORC_WEAPON_MULTIPLIER);
        }
    }

    private boolean isAxeOrBattleAxe(ItemStack stack) {
        Item item = stack.getItem();
        if (item != null) {
            String anim = item.getPlayerAnimationsId();
            if (anim != null && (anim.equalsIgnoreCase("battleaxe") || anim.equalsIgnoreCase("axe"))) {
                return true;
            }

            String itemConfigId = item.getId();
            if (itemConfigId != null) {
                // Case-sensitive first (ex: "Weapon_Battleaxe_Foo"), fallback para lowercase.
                if (itemConfigId.contains("Weapon_Battleaxe") || itemConfigId.contains("Weapon_Axe")) {
                    return true;
                }
                String idLower = itemConfigId.toLowerCase(Locale.ROOT);
                if (idLower.contains("weapon_battleaxe") || idLower.contains("weapon_axe")) {
                    return true;
                }
            }
        }

        String itemId = stack.getItemId();
        if (itemId != null) {
            if (itemId.contains("Weapon_Battleaxe") || itemId.contains("Weapon_Axe")) {
                return true;
            }
            String normalized = itemId.toLowerCase(Locale.ROOT);
            if (normalized.contains("weapon_battleaxe") || normalized.contains("weapon_axe")) {
                return true;
            }
        }

        return false;
    }

    private boolean isOrc(Player player) {
        try {
            RaceManager.Race race = RaceManager.fromKey(RaceManager.getPlayerRace(player));
            if (race == RaceManager.Race.ORC) {
                return true;
            }
        } catch (Exception ignored) {
            // fall back to stat inference
        }

        try {
            EntityStatMap stats = EntityStatsModule.get(player);
            if (stats != null && stats.get("Health") != null) {
                float maxHealth = stats.get("Health").getMax();
                // Orc base: 175 (100 +75). Use threshold to account for other buffs.
                if (maxHealth >= 170f) {
                    return true;
                }
            }
        } catch (Exception ignored) {
            // ignore
        }

        return false;
    }
}
