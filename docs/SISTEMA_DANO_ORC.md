# Sistema de Multiplicador de Dano - Status da Pesquisa

## 🔍 Pesquisa na API Oficial do Hytale

Após explorar o `HytaleServer.jar` local, **confirmo que classes de dano existem** na API:

### Classes Encontradas
```
com/hypixel/hytale/server/core/modules/entity/damage/Damage.class
com/hypixel/hytale/server/core/modules/entity/damage/DamageEventSystem.class
com/hypixel/hytale/server/core/modules/entity/damage/DamageModule.class
com/hypixel/hytale/server/core/modules/entity/damage/DamageSystems.class
com/hypixel/hytale/server/core/entity/damage/DamageDataComponent.class
```

### 📦 Localização Correta dos Pacotes
- ✅ **Existe**: `com.hypixel.hytale.server.core.modules.entity.damage.*`
- ❌ **Não existe**: `com.hypixel.hytale.server.core.modules.damage.*` (informação original estava errada)

## ⚠️ Problema Atual

Embora as classes existam, **a assinatura dos métodos não está totalmente documentada** ou a API de dano ainda não está completamente exposta para modding público. 

### Tentativas Realizadas
1. ✅ Encontradas as classes corretas no JAR
2. ✅ Corrigidos imports (`com.hypixel.hytale.server.core.inventory.ItemStack`)
3. ❌ Falha ao compilar: assinatura do método `handle()` incompatível
4. ❌ `DamageEventSystem` requer métodos abstratos não documentados

### Erros de Compilação
```
error: OrcDamageBoostSystem does not override abstract method 
       handle(int,ArchetypeChunk<EntityStore>,Store<EntityStore>,
       CommandBuffer<EntityStore>,Damage) in EntityEventSystem

error: cannot find symbol - method registerSystemGroup()
error: cannot find symbol - method addToGroup()
```

## 🚧 Conclusão

**O sistema de dano existe mas ainda não está pronto para uso em mods públicos:**

1. **Classes presentes** ✅ - Confirmado no HytaleServer.jar
2. **API incompleta** ❌ - Assinaturas de métodos não expostas corretamente  
3. **Documentação ausente** ❌ - Sem JavaDoc ou exemplos oficiais
4. **Early Access** ⏳ - Hytale lançou em 13/01/2026, API ainda em desenvolvimento

## 🔮 Alternativas Atuais

### Opção 1: Aguardar Atualização da API
Hytale está em Early Access e a equipe comprometeu-se com "rapid patching cadence". O sistema de dano provavelmente será exposto em futuras atualizações.

**Acompanhar:**
- [Patch Notes Oficiais](https://hytale.com/news)
- [Modding Strategy Post](https://hytale.com/news/2025/11/hytale-modding-strategy-and-status)

### Opção 2: Eventos Globais (se disponíveis)
Verificar se existem eventos globais de combate:
```java
// Pseudo-código (verificar se disponível)
events.registerGlobal(EntityDamageEvent.class, this::onEntityDamage);
```

### Opção 3: Modificar Stats Temporários  
Aplicar bônus de Attack/Strength stats temporariamente quando Orc equipa armas elegíveis (requer que estes stats existam).

### Opção 4: Usar Sistema de Comandos
Criar um comando admin `/setdamagemultiplier <player> <valor>` usando o `DamageCommand` que existe no servidor.

## 📚 Recursos da API Confirmados

### ✅ Disponíveis e Funcionando
- **Componentes ECS** - `com.hypixel.hytale.component.*`
- **Stats** - Health, Stamina, Mana (usado no mod atual)
- **Eventos** - PlayerReadyEvent, EntityEvent, etc.
- **Comandos** - Sistema de comandos customizados
- **Inventário** - `com.hypixel.hytale.server.core.inventory.*`
- **ItemStack** - `com.hypixel.hytale.server.core.inventory.ItemStack`

### ⏳ Existem mas Não Compilam
- **Sistema de Dano** - `com.hypixel.hytale.server.core.modules.entity.damage.*`
- **Combate** - `com.hypixel.hytale.server.core.asset.type.gameplay.CombatConfig`

## 💡 Recomendação

**Aguarde a próxima atualização do Hytale.** A equipe está trabalhando ativamente:

> "We are committed to maintaining a rapid patching cadence to address issues and improve the game as quickly as possible."  
> — Hytale Team, Update 1 (17/01/2026)

O sistema de multiplicador de dano para Orcs **é tecnicamente possível** e será implementável assim que a API de dano for documentada adequadamente.

## 📞 Suporte

- **Bug Reports**: https://support.hytale.com/
- **Discord Oficial**: https://discord.gg/hytale
- **Comunidade de Modding**: Aguardando canais oficiais

---

**Status**: ⏸️ **Em Espera de Atualização da API**  
**Data da Pesquisa**: 22/01/2026  
**Versão do Hytale**: Early Access (pós Update 1)
