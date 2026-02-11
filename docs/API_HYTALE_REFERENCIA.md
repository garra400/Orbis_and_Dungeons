# Hytale Modding API Reference

> **Baseado em:** [HytaleModding.dev](https://hytalemodding.dev) - Documentação oficial da comunidade  
> **Última atualização:** Fevereiro 2026

Este documento serve como referência rápida para desenvolvimento de mods no Hytale, com foco especial nas APIs utilizadas pelo mod **Orbis and Dungeons**.

---

## Índice

1. [Arquitetura ECS](#arquitetura-ecs)
2. [Store, Ref e Holder](#store-ref-e-holder)
3. [Componentes de Player](#componentes-de-player)
4. [Sistema de Stats](#sistema-de-stats)
5. [Sistema de Comandos](#sistema-de-comandos)
6. [Sistema de Eventos](#sistema-de-eventos)
7. [Interface UI](#interface-ui)
8. [Persistência de Dados](#persistência-de-dados)
9. [Sistema de Dano](#sistema-de-dano)
10. [Referência de Eventos](#referência-de-eventos)

---

## Arquitetura ECS

O Hytale utiliza uma arquitetura **Entity-Component-System (ECS)** para organizar entidades no mundo. Este modelo separa dados (Componentes) da lógica (Sistemas).

### Conceitos Fundamentais

| Conceito | Descrição |
|----------|-----------|
| **Entity** | Um ID único que representa algo no mundo (jogador, mob, item) |
| **Component** | Dados puros associados a uma entidade (posição, saúde, nome) |
| **System** | Lógica que processa entidades com certos componentes |
| **Store** | Armazena e gerencia todas as entidades e seus componentes |

### Benefícios do ECS

- **Performance**: Dados agrupados em "chunks" para acesso rápido
- **Modularidade**: Componentes podem ser adicionados/removidos dinamicamente
- **Composição**: Entidades são definidas pelos componentes que possuem

---

## Store, Ref e Holder

### Store

O `Store` é o núcleo do sistema ECS. Armazena todas as entidades e seus componentes.

```java
// Acessar componente de uma entidade
Player player = store.getComponent(ref, Player.getComponentType());
TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
```

### EntityStore

`EntityStore` é uma especialização de `Store` que inclui acesso ao `World`:

```java
EntityStore entityStore = store.getExternalData();
World world = entityStore.getWorld();
```

### Ref (Referência)

`Ref` é um "ponteiro seguro" para uma entidade. **Nunca armazene referências diretas a entidades** - sempre use `Ref`.

```java
Ref<EntityStore> playerRef = ctx.senderAsPlayerRef();

// Verificar se a entidade ainda existe
if (playerRef != null && playerRef.isValid()) {
    // Seguro para usar
}
```

### Holder

`Holder` é um "blueprint" para uma entidade. Usado para construir uma entidade antes de adicioná-la ao Store.

```java
Holder holder = playerRef.getHolder();

// Obter ou criar componente
RaceData raceData = holder.ensureAndGetComponent(raceDataType);

// Salvar componente
holder.putComponent(raceDataType, raceData.clone());
```

---

## Componentes de Player

O "jogador" no Hytale é composto por múltiplos componentes:

### PlayerRef (Componente)

Representa a **conexão e identidade** do jogador. Persiste enquanto o jogador está conectado, mesmo ao trocar de mundo.

```java
// Dados disponíveis
playerRef.getUsername()    // Nome do jogador
playerRef.getUuid()        // UUID persistente
playerRef.getLanguage()    // Idioma preferido
```

### Player (Componente)

Representa a **presença física** do jogador. Só existe quando o jogador está "spawnado" em um mundo.

```java
Player player = store.getComponent(ref, Player.getComponentType());

// Métodos úteis
player.sendMessage(Message.raw("Olá!"));
player.getDisplayName();
player.getHudManager();
player.getPageManager();
```

### Outros Componentes Úteis

```java
// UUID
UUIDComponent uuid = store.getComponent(ref, UUIDComponent.getComponentType());

// Posição/Rotação
TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
Vec3 position = transform.getPosition();

// Stats (HP, Stamina, etc)
EntityStatMap stats = store.getComponent(ref, EntityStatMap.getComponentType());
```

---

## Sistema de Stats

Stats são gerenciados pelo módulo `EntityStats`.

### Stats Disponíveis

| Stat | Método | Padrão |
|------|--------|--------|
| Health | `DefaultEntityStatTypes.getHealth()` | 100 |
| Stamina | `DefaultEntityStatTypes.getStamina()` | 10 |
| Mana | `DefaultEntityStatTypes.getMana()` | 100 |
| Oxygen | `DefaultEntityStatTypes.getOxygen()` | - |
| Signature Energy | `DefaultEntityStatTypes.getSignatureEnergy()` | - |
| Ammo | `DefaultEntityStatTypes.getAmmo()` | - |

### Acessar EntityStatMap

```java
// Via Store (preferido)
EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());

// Via EntityStatsModule (deprecated, mas funcional)
EntityStatMap stats = EntityStatsModule.get(player);
```

### Modificar Stats

```java
// Obter stat index
int healthIdx = DefaultEntityStatTypes.getHealth();

// Maximizar um stat
statMap.maximizeStatValue(healthIdx);

// Subtrair valor
statMap.subtractStatValue(healthIdx, 25.0f);

// Adicionar valor
statMap.addValue(healthIdx, 50.0f);
```

### Modificadores de Stats

Modificadores permitem alterar stats de forma **aditiva** ou **multiplicativa**:

```java
// Criar modificador (+75 HP)
Modifier healthMod = new StaticModifier(
    Modifier.ModifierTarget.MAX,           // Afeta valor máximo
    StaticModifier.CalculationType.ADDITIVE, // Soma
    75.0f                                   // Valor
);

// Aplicar modificador
stats.putModifier(healthIdx, "race_mod_Health", healthMod);

// Remover modificador
stats.removeModifier(healthIdx, "race_mod_Health");

// Aplicar mudanças
stats.update();
```

---

## Sistema de Comandos

O Hytale oferece várias classes base para criar comandos:

### Tipos de Comando

| Classe | Uso | Thread |
|--------|-----|--------|
| `AbstractAsyncCommand` | Comandos sem acesso a Store/Ref | Background |
| `AbstractPlayerCommand` | Comandos executados pelo jogador | World Thread |
| `AbstractTargetPlayerCommand` | Comandos que visam outro jogador | World Thread |
| `AbstractTargetEntityCommand` | Comandos que visam entidade olhada | World Thread |

### AbstractPlayerCommand (Mais Usado)

```java
public class MyCommand extends AbstractPlayerCommand {

    public MyCommand() {
        super("mycommand", "Descrição do comando");
    }

    @Override
    protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {
        
        Player player = store.getComponent(ref, Player.getComponentType());
        player.sendMessage(Message.raw("Comando executado!"));
    }
}
```

### Argumentos de Comando

```java
public class HealCommand extends AbstractPlayerCommand {
    private final DefaultArg<Float> amountArg;
    private final OptionalArg<String> messageArg;
    private final FlagArg debugArg;

    public HealCommand() {
        super("heal", "Cura o jogador");
        
        // Argumento com valor padrão
        this.amountArg = this.withDefaultArg(
            "amount",           // Nome
            "Quantidade de HP", // Descrição
            ArgTypes.FLOAT,     // Tipo
            100.0f,             // Valor padrão
            "Padrão: 100"       // Descrição do padrão
        );
        
        // Argumento opcional (--message "texto")
        this.messageArg = this.withOptionalArg(
            "message",
            "Mensagem a exibir",
            ArgTypes.STRING
        );
        
        // Flag booleana (--debug)
        this.debugArg = this.withFlagArg("debug", "Modo debug");
    }

    @Override
    protected void execute(...) {
        Float amount = this.amountArg.get(ctx);
        String message = this.messageArg.get(ctx); // null se não fornecido
        boolean debug = this.debugArg.get(ctx);    // true/false
    }
}
```

### Tipos de Argumento (ArgTypes)

- `ArgTypes.STRING`
- `ArgTypes.INTEGER`
- `ArgTypes.FLOAT`
- `ArgTypes.DOUBLE`
- `ArgTypes.BOOLEAN`
- `ArgTypes.UUID`

### Registrar Comando

```java
@Override
protected void setup() {
    getCommandRegistry().registerCommand(new MyCommand());
    getCommandRegistry().registerCommand(new HealCommand());
}
```

---

## Sistema de Eventos

### Eventos Globais (IEvent)

Eventos que não precisam de acesso ao ECS:

```java
public class MyEventHandler {
    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Message.raw("Bem-vindo!"));
    }
}

// Registrar
@Override
protected void setup() {
    getEventRegistry().registerGlobal(
        PlayerReadyEvent.class,
        MyEventHandler::onPlayerReady
    );
}
```

### Eventos ECS (EcsEvent)

Eventos que precisam de acesso ao Store/Ref:

```java
public class MyDamageSystem extends EntityEventSystem<EntityStore, Damage> {
    
    public MyDamageSystem() {
        super(Damage.class);
    }

    @Override
    public void handle(
            int index,
            @Nonnull ArchetypeChunk<EntityStore> chunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> buffer,
            @Nonnull Damage event) {
        
        // Processar evento de dano
        float damageAmount = event.getDamageAmount();
        
        // Cancelar se necessário
        event.setCancelled(true);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}

// Registrar
@Override
protected void setup() {
    getEntityStoreRegistry().registerSystem(new MyDamageSystem());
}
```

---

## Interface UI

### Estrutura de Arquivos

```
resources/
└── Common/
    └── UI/
        └── Custom/
            ├── my_page.ui
            └── textures/
                └── background.png
```

**Importante:** `manifest.json` deve conter `"IncludesAssetPack": true`

### CustomUIHud (HUD permanente)

```java
public class MyHud extends CustomUIHud {
    
    @Override
    public void build(@Nonnull UICommandBuilder cmd) {
        cmd.append("MyHud.ui");
        cmd.set("#HealthText.Text", "100 HP");
    }
}

// Mostrar
player.getHudManager().setCustomHud(myHud);
```

### InteractiveCustomUIPage (Página interativa)

```java
public class MyPage extends InteractiveCustomUIPage<MyEventData> {

    public static class MyEventData {
        public String action;
        
        public static final BuilderCodec<MyEventData> CODEC = 
            BuilderCodec.builder(MyEventData.class, MyEventData::new)
                .append(
                    new KeyedCodec<>("Action", Codec.STRING),
                    (o, v) -> o.action = v,
                    o -> o.action
                )
                .add()
                .build();
    }

    public MyPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CantClose, MyEventData.CODEC);
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store) {
        
        cmd.append("Pages/my_page.ui");
        cmd.set("#Title.Text", "Minha Página");
        
        evt.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#ConfirmButton",
            new EventData().append("Action", "confirm")
        );
    }

    @Override
    public void handleCustomUIEvent(@Nonnull CustomUIEvent<MyEventData> event) {
        MyEventData data = event.getData();
        if ("confirm".equals(data.action)) {
            // Processar confirmação
            event.closePage();
        }
    }
}

// Abrir página
player.getPageManager().openCustomPage(ref, store, new MyPage(playerRef));
```

---

## Persistência de Dados

### Criar Componente Persistente

```java
public class MyPlayerData implements Component<EntityStore> {
    
    private String selectedRace;
    private int level;
    
    public static final BuilderCodec<MyPlayerData> CODEC =
        BuilderCodec.builder(MyPlayerData.class, MyPlayerData::new)
            .append(
                new KeyedCodec<>("SelectedRace", Codec.STRING),
                (d, v) -> d.selectedRace = v,
                d -> d.selectedRace
            )
            .add()
            .append(
                new KeyedCodec<>("Level", Codec.INTEGER),
                (d, v) -> d.level = v,
                d -> d.level
            )
            .add()
            .build();
    
    public MyPlayerData() {
        this.selectedRace = null;
        this.level = 1;
    }
    
    @Nonnull
    @Override
    public Component<EntityStore> clone() {
        MyPlayerData copy = new MyPlayerData();
        copy.selectedRace = this.selectedRace;
        copy.level = this.level;
        return copy;
    }
    
    // Getters e Setters...
}
```

### Registrar Componente

```java
public class MyPlugin extends JavaPlugin {
    
    private static ComponentType<EntityStore, MyPlayerData> playerDataType;

    @Override
    protected void setup() {
        playerDataType = getEntityStoreRegistry().registerComponent(
            MyPlayerData.class,
            "MyPlayerData",      // ID único para persistência
            MyPlayerData.CODEC
        );
    }
    
    public static ComponentType<EntityStore, MyPlayerData> getPlayerDataType() {
        return playerDataType;
    }
}
```

### Ler/Escrever Dados

```java
// Ler
MyPlayerData data = store.getComponent(ref, MyPlugin.getPlayerDataType());
if (data == null) {
    data = new MyPlayerData();
}

// Modificar
data.setSelectedRace("elf");
data.setLevel(5);

// Salvar
store.putComponent(ref, MyPlugin.getPlayerDataType(), data);
```

---

## Sistema de Dano

### Interceptar Dano (EcsEvent)

```java
public class DamageModifierSystem extends EntityEventSystem<EntityStore, Damage> {
    
    public DamageModifierSystem() {
        super(Damage.class);
    }

    @Override
    public void handle(
            int index,
            @Nonnull ArchetypeChunk<EntityStore> chunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> buffer,
            @Nonnull Damage event) {
        
        Ref<EntityStore> victimRef = event.getVictimRef();
        float originalDamage = event.getDamageAmount();
        
        // Verificar se tem resistência
        MyPlayerData data = store.getComponent(victimRef, MyPlugin.getPlayerDataType());
        if (data != null) {
            float resistance = getResistance(data.getSelectedRace());
            float newDamage = originalDamage * resistance;
            event.setDamageAmount(newDamage);
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
```

---

## Referência de Eventos

### Eventos Globais (IEvent)

| Evento | Descrição |
|--------|-----------|
| `PlayerReadyEvent` | Jogador entrou e está pronto |
| `PlayerConnectEvent` | Jogador conectou ao servidor |
| `PlayerDisconnectEvent` | Jogador desconectou |
| `BootEvent` | Servidor iniciado |
| `ShutdownEvent` | Servidor desligando |
| `EntityRemoveEvent` | Entidade removida do mundo |
| `AddWorldEvent` | Novo mundo criado |
| `RemoveWorldEvent` | Mundo removido |

### Eventos ECS (Canceláveis)

| Evento | Descrição |
|--------|-----------|
| `Damage` | Entidade recebendo dano |
| `BreakBlockEvent` | Bloco sendo quebrado |
| `PlaceBlockEvent` | Bloco sendo colocado |
| `CraftRecipeEvent.Pre` | Antes de craftar |
| `CraftRecipeEvent.Post` | Depois de craftar |
| `DropItemEvent` | Item sendo dropado |
| `ChangeGameModeEvent` | Modo de jogo mudando |

---

## Links Úteis

### Documentação
- [HytaleModding.dev](https://hytalemodding.dev) - Documentação da comunidade
- [Guia de Comandos](https://hytalemodding.dev/en/docs/plugin/creating-commands)
- [Guia de ECS](https://hytalemodding.dev/en/docs/plugin/ecs/hytale-ecs-theory)
- [Guia de UI](https://hytalemodding.dev/en/docs/plugin/ui)

### Vídeos
- [Kaupenjoe's Modding Videos](https://www.youtube.com/@ModdingByKaupenjoe)
- [TroubleDEV's Modding Videos](https://www.youtube.com/@TroubleDEV)

### Ferramentas
- [Editor Visual de UI](https://hytale.ellie.au/)
- [HyUI - Criar UI via Java](https://www.curseforge.com/hytale/mods/hyui)

---

*Este documento é uma referência rápida. Para documentação completa, consulte [hytalemodding.dev](https://hytalemodding.dev).*
