# Referência Completa da API do Hytale
**Data de Extração:** 22 de Janeiro de 2026  
**Versão:** Hytale Early Access (pós Update 1)  
**Fonte:** HytaleServer.jar

---

## 📋 Índice

1. [Módulos Principais](#módulos-principais)
2. [Sistema de Dano (Damage)](#sistema-de-dano-damage)
3. [Sistema de Stats (EntityStats)](#sistema-de-stats-entitystats)
4. [Sistema de Itens (Item)](#sistema-de-itens-item)
5. [Sistema de Interação (Interaction)](#sistema-de-interação-interaction)
6. [Sistema de Física (Physics)](#sistema-de-física-physics)
7. [Sistema de Colisão (Collision)](#sistema-de-colisão-collision)
8. [Componentes ECS](#componentes-ecs)
9. [Eventos Disponíveis](#eventos-disponíveis)
10. [Como Usar](#como-usar)

---

## Módulos Principais

### Lista Completa de Módulos

O Hytale organiza sua API em módulos localizados em `com.hypixel.hytale.server.core.modules.*`:

| Módulo | Pacote | Descrição |
|--------|---------|-----------|
| **AccessControl** | `accesscontrol` | Sistema de bans, whitelist e controle de acesso |
| **Block** | `block` | Gerenciamento de blocos e containers |
| **BlockHealth** | `blockhealth` | Sistema de saúde/durabilidade de blocos |
| **BlockSet** | `blockset` | Conjuntos e grupos de blocos |
| **Camera** | `camera` | Controle de câmera e visão |
| **Collision** | `collision` | Detecção de colisões físicas |
| **Debug** | `debug` | Ferramentas de debug e visualização |
| **Entity** | `entity` | Sistema base de entidades |
| **EntityStats** | `entitystats` | Stats (Health, Stamina, Mana, etc.) |
| **EntityUI** | `entityui` | UI associada a entidades |
| **I18n** | `i18n` | Internacionalização e traduções |
| **Interaction** | `interaction` | Interações de jogador (click, use, etc.) |
| **Item** | `item` | Sistema de itens e receitas |
| **Migrations** | `migrations` | Migrações de dados entre versões |
| **Physics** | `physics` | Física e movimento |
| **Projectile** | `projectile` | Sistema de projéteis |
| **Time** | `time` | Gerenciamento de tempo do mundo |
| **Damage** ⚠️ | `entity.damage` | **Sistema de dano e morte** |

---

## Sistema de Dano (Damage)

### 📍 Localização
```
com.hypixel.hytale.server.core.modules.entity.damage.*
```

### Classes Principais

#### 1. **DamageModule**
Módulo principal que gerencia todo o sistema de dano.

```java
public class DamageModule extends JavaPlugin {
    public static DamageModule get();
    
    // Componentes
    public ComponentType<EntityStore, DeathComponent> getDeathComponentType();
    public ComponentType<EntityStore, DeferredCorpseRemoval> getDeferredCorpseRemovalComponentType();
    
    // Grupos de Sistema (Pipeline de Dano)
    public SystemGroup<EntityStore> getGatherDamageGroup();   // Antes das reduções
    public SystemGroup<EntityStore> getFilterDamageGroup();    // Filtrar/cancelar
    public SystemGroup<EntityStore> getInspectDamageGroup();   // Após cálculo
}
```

**Pipeline de Dano:**
```
Ataque → [GatherDamageGroup] → [FilterDamageGroup] → [InspectDamageGroup] → Aplicação
         ↑ Modificar dano      ↑ Cancelar/reduzir    ↑ Efeitos pós-dano
```

#### 2. **Damage** (Evento)
Evento ECS que representa um dano sendo aplicado.

```java
public class Damage extends CancellableEcsEvent implements IMetaStore<Damage> {
    // Construtor
    public Damage(Source source, DamageCause cause, float amount);
    public Damage(Source source, int damageCauseIndex, float amount);
    
    // Métodos Principais
    public float getAmount();                    // Quantidade de dano
    public void setAmount(float amount);         // Modificar dano
    public float getInitialAmount();             // Dano original
    
    public DamageCause getCause();               // Causa do dano
    public Source getSource();                   // Fonte do dano
    public void setSource(Source source);
    
    // Meta Dados (chaves estáticas)
    public static final MetaKey<Vector4d> HIT_LOCATION;
    public static final MetaKey<Float> HIT_ANGLE;
    public static final MetaKey<Boolean> BLOCKED;              // Bloqueado?
    public static final MetaKey<Float> STAMINA_DRAIN_MULTIPLIER;
    public static final MetaKey<KnockbackComponent> KNOCKBACK_COMPONENT;
}
```

**Damage.Source (Fontes de Dano):**
- `EntitySource` - Dano causado por entidade
- Outros tipos (verificar sub-classes)

#### 3. **DamageEventSystem**
Sistema abstrato para processar eventos de dano.

```java
public abstract class DamageEventSystem 
    extends EntityEventSystem<EntityStore, Damage> {
    
    // Implementar este método para processar dano
    @Override
    public abstract void handle(Damage damage, EntityStore entity);
    
    // Registrar em qual grupo do pipeline
    @Override
    protected abstract void registerSystemGroup();
}
```

**Exemplo de Uso:**
```java
public class MeuSistemaDano extends DamageEventSystem {
    @Override
    public void handle(Damage damage, EntityStore entity) {
        // Verificar fonte
        if (damage.getSource() instanceof Damage.EntitySource src) {
            var attacker = src.getRef().getEntity();
            
            // Modificar dano
            if (alguma_condição) {
                damage.setAmount(damage.getAmount() * 1.5f);
            }
            
            // Cancelar dano
            if (outra_condição) {
                damage.cancel();
            }
        }
    }
    
    @Override
    protected void registerSystemGroup() {
        // Registrar no grupo gather (antes de reduções)
        DamageModule.get().getGatherDamageGroup().addToGroup(this);
    }
}
```

#### 4. **DamageCause**
Representa a causa/tipo de dano.

```java
public class DamageCause {
    // Tipos de dano (verificar assets do jogo)
    // Exemplos: MELEE, FIRE, FALL, DROWNING, etc.
}
```

#### 5. **DeathComponent**
Componente anexado quando uma entidade morre.

```java
public class DeathComponent {
    // Dados sobre a morte da entidade
}
```

#### 6. **DamageSystems**
Sistemas internos de dano do Hytale (referência).

```java
public class DamageSystems {
    // PlayerDamageFilterSystem - Filtra dano de jogadores
    // ArmorDamageReduction - Redução por armadura
    // E outros sistemas internos
}
```

### ⚠️ Status Atual
**As classes existem mas a API não está completamente exposta:**
- ✅ Classes compiladas presentes no JAR
- ❌ Assinaturas de métodos não documentadas
- ❌ Impossível compilar sistemas customizados atualmente
- ⏳ Aguardando documentação oficial em futuras atualizações

---

## Sistema de Stats (EntityStats)

### 📍 Localização
```
com.hypixel.hytale.server.core.modules.entitystats.*
```

### Classes Principais

#### 1. **EntityStatsModule**
Módulo que gerencia stats de entidades.

```java
public class EntityStatsModule extends JavaPlugin {
    public static EntityStatsModule get();
    
    // Obter stats de uma entidade
    @Deprecated // Mas funcional
    public static EntityStatMap get(Entity entity);
    
    // Resolver stats por nome para ID
    public static Int2FloatMap resolveEntityStats(Object2FloatMap<String> stats);
    public static int[] resolveEntityStats(String[] statNames);
    
    // Componentes
    public ComponentType<EntityStore, EntityStatMap> getEntityStatMapComponentType();
}
```

#### 2. **EntityStatMap**
Mapa de stats de uma entidade.

```java
public class EntityStatMap {
    // Obter/modificar stat por nome
    public float getStat(String statName);
    public void setStat(String statName, float value);
    
    // Adicionar modificadores
    public void addModifier(String statName, Modifier modifier);
    public void removeModifier(String statName, Modifier modifier);
    
    // Atualizar cálculos
    public void update();
}
```

#### 3. **Modifier** (Modificadores de Stats)
```java
public interface Modifier {
    float apply(float baseValue);
}

public class StaticModifier implements Modifier {
    public StaticModifier(float value);  // Soma fixa
    // Ex: +10 Health
}

// Outros tipos de modificadores disponíveis
public class DefaultModifiers {
    // Multiplicadores, porcentagens, etc.
}
```

#### 4. **Stats Nativos Disponíveis**
```java
// Pacote: com.hypixel.hytale.server.core.modules.entitystats.asset
public class DefaultEntityStatTypes {
    public static final String HEALTH = "Health";
    public static final String OXYGEN = "Oxygen";
    public static final String STAMINA = "Stamina";
    public static final String MANA = "Mana";
    public static final String SIGNATURE_ENERGY = "SignatureEnergy";
    public static final String AMMO = "Ammo";
}
```

**Valores Base Padrão:**
- Health: 100
- Stamina: 10
- Mana: 100 (presumido)
- Oxygen: 100 (presumido)

### ✅ Status Atual
**Totalmente funcional e usado no mod:**
```java
EntityStatMap stats = EntityStatsModule.get(player);
stats.addModifier("Health", new StaticModifier(75f));  // +75 HP
stats.addModifier("Stamina", new StaticModifier(15f)); // +15 Stamina
stats.update();
```

---

## Sistema de Itens (Item)

### 📍 Localização
```
com.hypixel.hytale.server.core.modules.item.*
com.hypixel.hytale.server.core.inventory.*
```

### Classes Principais

#### 1. **ItemModule**
```java
public class ItemModule extends JavaPlugin {
    public static ItemModule get();
    
    // Verificar se item existe
    public static boolean exists(String itemId);
    
    // Obter categorias de itens
    public List<String> getFlatItemCategoryList();
    
    // Drops aleatórios
    public List<ItemStack> getRandomItemDrops(String lootTable);
}
```

#### 2. **ItemStack**
Representa uma pilha de itens.

```java
// Pacote: com.hypixel.hytale.server.core.inventory.ItemStack
public class ItemStack {
    // Obter informações
    public String getItemId();              // ID do item (ex: "hytale:iron_sword")
    public Item getItem();                  // Objeto Item
    public int getAmount();                 // Quantidade na pilha
    
    // Metadata
    public Metadata getMetadata();
    public ItemStack withMetadata(Metadata meta);
    
    // Item.getCategories() - Categorias/tags do item
    // Ex: ["Sword", "Weapon", "Melee"]
}
```

#### 3. **Item**
Representa o tipo de item.

```java
public class Item {
    public String getId();
    public List<String> getCategories();   // Tags do asset
    // Outros métodos de configuração do item
}
```

### ✅ Status Atual
**Totalmente funcional:**
```java
ItemStack weapon = player.getInventory().getItemInHand();
if (weapon != null) {
    String id = weapon.getItemId();
    List<String> categories = weapon.getItem().getCategories();
    
    if (categories.contains("Hammer")) {
        // Fazer algo com martelos
    }
}
```

---

## Sistema de Interação (Interaction)

### 📍 Localização
```
com.hypixel.hytale.server.core.modules.interaction.*
```

### Classes Principais

#### 1. **InteractionModule**
```java
public class InteractionModule extends JavaPlugin {
    public static InteractionModule get();
    
    // Processar interação de mouse
    public void doMouseInteraction(
        Ref<EntityStore> entityRef,
        ComponentAccessor<EntityStore> accessor,
        MouseInteraction interaction,
        Player player,
        PlayerRef playerRef
    );
    
    // Componentes
    public ComponentType<EntityStore, Interactions> getInteractionsComponentType();
    public ComponentType<EntityStore, InteractionManager> getInteractionManagerComponent();
    
    // Rastreamento de blocos colocados
    public ResourceType<ChunkStore, BlockCounter> getBlockCounterResourceType();
}
```

#### 2. **Tipos de Interação**
```java
public enum InteractionType {
    USE,           // Usar/interagir
    ATTACK,        // Atacar
    BREAK_BLOCK,   // Quebrar bloco
    PLACE_BLOCK,   // Colocar bloco
    // E outros
}
```

### ⚙️ Status Atual
**Funcional mas complexo** - Sistema usado internamente pelo Hytale para processar clicks, ataques, etc.

---

## Sistema de Física (Physics)

### 📍 Localização
```
com.hypixel.hytale.server.core.modules.physics.*
```

### Componentes Principais

#### 1. **Velocity** (Componente)
```java
public class Velocity {
    // Velocidade da entidade em 3D
    public Vector3d getVelocity();
    public void setVelocity(Vector3d velocity);
}
```

#### 2. **PhysicsValues** (Componente)
```java
public class PhysicsValues {
    // Valores físicos como gravidade, fricção, etc.
    public float getGravity();
    public float getFriction();
    // E outros
}
```

#### 3. **ForceProvider**
Sistema para aplicar forças às entidades.

```java
public interface ForceProvider {
    void applyForce(ForceAccumulator accumulator);
}
```

### ✅ Status Atual
**Funcional** - Usado para movimento e física de entidades.

---

## Sistema de Colisão (Collision)

### 📍 Localização
```
com.hypixel.hytale.server.core.modules.collision.*
```

### Classes Principais

#### 1. **CollisionModule**
```java
public class CollisionModule extends JavaPlugin {
    public static CollisionModule get();
    
    // Detecção de colisões
    public static boolean findCollisions(
        Box boundingBox,
        Vector3d position,
        Vector3d movement,
        CollisionResult result,
        ComponentAccessor<EntityStore> accessor
    );
    
    // Validação de posição
    public int validatePosition(
        World world,
        Box boundingBox,
        Vector3d position,
        CollisionResult result
    );
    
    // Constantes de validação
    public static final int VALIDATE_INVALID = -1;
    public static final int VALIDATE_OK = 0;
    public static final int VALIDATE_ON_GROUND = 1;
    public static final int VALIDATE_TOUCH_CEIL = 2;
}
```

#### 2. **CollisionResult**
Resultado de uma verificação de colisão.

```java
public class CollisionResult {
    // Informações sobre colisões detectadas
    public boolean hasCollision();
    public Vector3d getCollisionNormal();
    // E outros dados de colisão
}
```

### ✅ Status Atual
**Funcional** - Sistema usado para física e movimentação.

---

## Componentes ECS

### Sistema de Componentes
Hytale usa o padrão ECS (Entity Component System) com a biblioteca Flecs.

#### Estrutura Básica
```java
// Componente
public class MeuComponente implements Component<EntityStore> {
    private String data;
    
    @Override
    public Component<EntityStore> clone() {
        MeuComponente copy = new MeuComponente();
        copy.data = this.data;
        return copy;
    }
}

// Codec para persistência
public static final Codec<MeuComponente> CODEC = 
    BuilderCodec.of(MeuComponente.class)
        .field("data", Codecs.STRING, c -> c.data, (c, v) -> c.data = v)
        .build();

// Registrar
ComponentType<EntityStore, MeuComponente> type = 
    plugin.getEntityStoreRegistry().registerComponent(
        MeuComponente.class,
        "MeuComponente",
        CODEC
    );

// Usar
Holder holder = playerRef.getHolder();
MeuComponente comp = holder.getComponent(type);
holder.putComponent(type, novoComponente);
```

### Componentes Comuns

| Componente | Pacote | Descrição |
|------------|---------|-----------|
| `BoundingBox` | `entity.component` | Caixa de colisão |
| `DisplayNameComponent` | `entity.component` | Nome exibido |
| `EntityScaleComponent` | `entity.component` | Escala da entidade |
| `ActiveAnimationComponent` | `entity.component` | Animação ativa |
| `Velocity` | `physics.component` | Velocidade |
| `PhysicsValues` | `physics.component` | Valores físicos |
| `EntityStatMap` | `entitystats` | Mapa de stats |
| `DeathComponent` | `entity.damage` | Dados de morte |

---

## Eventos Disponíveis

### Eventos ECS (com.hypixel.hytale.server.core.event.events.ecs)

```java
// Blocos
BreakBlockEvent        - Quando um bloco é quebrado
PlaceBlockEvent        - Quando um bloco é colocado
DamageBlockEvent       - Quando um bloco recebe dano
UseBlockEvent          - Quando um bloco é usado (Pre/Post)

// Itens
DropItemEvent          - Quando um item é dropado
InteractivelyPickupItemEvent - Quando item é coletado
SwitchActiveSlotEvent  - Quando slot ativo muda

// Crafting
CraftRecipeEvent       - Quando receita é craftada (Pre/Post)

// Outros
ChangeGameModeEvent    - Mudança de modo de jogo
DiscoverZoneEvent      - Descoberta de zona
```

### Eventos Globais (com.hypixel.hytale.server.core.event.events)

```java
// Player
PlayerReadyEvent       - Jogador pronto (usado no mod)
PlayerJoinEvent        - Jogador entra
PlayerLeaveEvent       - Jogador sai

// Entidade
EntityEvent            - Evento base de entidade
EntityRemoveEvent      - Entidade removida
LivingEntityInventoryChangeEvent - Inventário muda

// Sistema
BootEvent              - Servidor iniciado
```

### Registrar Eventos
```java
EventRegistry events = plugin.getEventRegistry();

// Evento global
events.registerGlobal(PlayerReadyEvent.class, event -> {
    Player player = event.getPlayer();
    // Processar evento
});

// Evento ECS (via sistema)
public class MeuSistema extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    @Override
    public void handle(BreakBlockEvent event, EntityStore entity) {
        // Processar quebra de bloco
    }
}
```

---

## Como Usar

### 1. Modificar Stats (✅ Funcionando)
```java
// No start() do plugin
EntityStatMap stats = EntityStatsModule.get(player);
stats.addModifier("Health", new StaticModifier(75f));
stats.addModifier("Stamina", new StaticModifier(15f));
stats.update();
```

### 2. Componentes Persistentes (✅ Funcionando)
```java
// Registrar componente
ComponentType<EntityStore, MeuDado> type = 
    getEntityStoreRegistry().registerComponent(
        MeuDado.class,
        "MeuDado",
        MeuDado.CODEC
    );

// Ler/Escrever
Holder holder = playerRef.getHolder();
MeuDado dado = holder.getComponent(type);
holder.putComponent(type, novoDado);
```

### 3. Comandos Customizados (✅ Funcionando)
```java
public class MeuComando extends Command {
    public MeuComando() {
        super("meucomando", "Descrição");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            player.sendMessage("Olá!");
        }
    }
}

// Registrar
getCommandRegistry().registerCommand(new MeuComando());
```

### 4. UI Customizada (✅ Funcionando)
```java
public class MinhaPage extends InteractiveCustomUIPage {
    @Override
    public Codec<EventData> getCodec() {
        return EVENT_DATA_CODEC;
    }
    
    @Override
    protected void onCreate(Ref<EntityStore> ref, Store<EntityStore> store) {
        // Criar UI
    }
}

// Abrir
player.getPageManager().openCustomPage(playerRef, store, new MinhaPage());
```

### 5. Sistema de Dano (⏸️ Aguardando API)
```java
// FUTURO - Quando API for documentada
public class MeuDanoSystem extends DamageEventSystem {
    @Override
    public void handle(Damage damage, EntityStore entity) {
        if (damage.getSource() instanceof Damage.EntitySource src) {
            // Modificar dano baseado em condições
            damage.setAmount(damage.getAmount() * multiplicador);
        }
    }
    
    @Override
    protected void registerSystemGroup() {
        DamageModule.get().getGatherDamageGroup().addToGroup(this);
    }
}
```

---

## Referências

### Documentação Oficial
- **Site**: https://hytale.com/
- **Patch Notes**: https://hytale.com/news
- **Modding Strategy**: https://hytale.com/news/2025/11/hytale-modding-strategy-and-status
- **Suporte**: https://support.hytale.com/
- **Discord**: https://discord.gg/hytale

### Estrutura de Pacotes
```
com.hypixel.hytale
├── server.core
│   ├── modules          # Módulos do jogo
│   │   ├── entity
│   │   │   └── damage   # Sistema de dano
│   │   ├── entitystats  # Stats
│   │   ├── item         # Itens
│   │   ├── interaction  # Interações
│   │   ├── physics      # Física
│   │   └── collision    # Colisão
│   ├── entity           # Classes de entidades
│   ├── command          # Sistema de comandos
│   ├── event            # Sistema de eventos
│   ├── inventory        # Sistema de inventário
│   └── plugin           # Base de plugins
├── component            # Sistema ECS
├── codec                # Serialização
└── protocol             # Protocolos de rede
```

---

## Observações Finais

### ✅ APIs Totalmente Funcionais
- Sistema de Stats (EntityStats)
- Componentes ECS
- Eventos globais
- Comandos
- UI customizada
- Sistema de inventário

### ⏸️ APIs Parcialmente Expostas
- **Sistema de Dano** - Classes existem mas assinaturas não documentadas
- Sistema de interação avançado
- Física avançada

### 🔮 Futuro
O Hytale está em Early Access e a equipe comprometeu-se com:
> "We are committed to maintaining a rapid patching cadence to address issues and improve the game as quickly as possible."

Espera-se que a API de dano e outras funcionalidades sejam completamente documentadas em futuras atualizações.

---

**Gerado automaticamente via exploração do HytaleServer.jar**  
**Última atualização:** 22/01/2026
