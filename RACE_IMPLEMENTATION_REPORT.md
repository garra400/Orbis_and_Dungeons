# Relatório de Implementação - Sistema de Raças
**Mod: Orbis and Dungeons**  
**Data: 2026-01-20**

---

## ⚠️ AVISO IMPORTANTE

**O QUE O JOGADOR VÊ vs O QUE ESTÁ REALMENTE IMPLEMENTADO**

As descrições mostradas na UI sugerem vários efeitos de gameplay (dano aumentado, resistências, velocidade, etc.), mas **ATUALMENTE APENAS 3 STATS BÁSICOS SÃO MODIFICADOS**:
- Health (Vida máxima)
- Stamina (Stamina máxima)  
- Mana (Mana máxima)

---

## 🔍 Análise Detalhada por Raça

### 1. ELFO

#### O que o jogador vê na UI:
```
Positivos:
- Corpse Smell: Undead e Phantoms ficam neutros
- Keen Senses: bonus de critico a distancia
- Wind Step: ligeiro bonus de velocidade ao pular

Negativos:
- Fragile Frame: recebe mais dano fisico
- Lean Appetite: fome drena um pouco mais rapido
```

#### O que está REALMENTE implementado:
```java
Health:  +0  (nenhuma mudança)
Stamina: +8  (máximo de stamina aumenta em 8)
Mana:    +15 (máximo de mana aumenta em 15)
```

#### ❌ NÃO IMPLEMENTADO:
- ❌ Undead/Phantoms ficarem neutros
- ❌ Bônus de crítico a distância
- ❌ Bônus de velocidade ao pular
- ❌ Receber mais dano físico
- ❌ Fome drenar mais rápido

---

### 2. ORC

#### O que o jogador vê na UI:
```
Positivos:
- Brutal Strikes: dano corpo-a-corpo aumentado
- Thick Skin: resistencia fisica melhorada
- Battle Hunger: cura leve ao eliminar inimigos

Negativos:
- Blunt Mind: penalidade leve em magia
- Hearty Diet: consome mais comida por tick
```

#### O que está REALMENTE implementado:
```java
Health:  +25 (máximo de vida aumenta em 25)
Stamina: +12 (máximo de stamina aumenta em 12)
Mana:    -8  (máximo de mana REDUZ em 8)
```

#### ❌ NÃO IMPLEMENTADO:
- ❌ Dano corpo-a-corpo aumentado
- ❌ Resistência física melhorada
- ❌ Cura ao eliminar inimigos
- ❌ Penalidade em magia (além da redução de mana)
- ❌ Consumir mais comida

#### ⚠️ PROBLEMA REPORTADO:
> "testei a orc que diz que aumenta a força em golpes fisicos e ela acabou travando o dano"

**Possível causa**: Como apenas Health/Stamina/Mana são modificados, não há alteração de dano. O "travamento" pode ser:
1. Um bug na aplicação dos stats que sobrescreve valores base do player
2. A modificação de stats interferindo com o sistema de combate
3. O modificador sendo aplicado a stats incorretos

---

### 3. HUMANO

#### O que o jogador vê na UI:
```
Positivos:
- Adaptive: bonus moderado em todas as proficiencias
- Industrious: pequenas reducoes de tempo de craft
- Diplomatic: melhor relacao com NPCs neutros

Negativos:
- Average Body: sem resistencias naturais
- No Specialty: bonus menores que racas focadas
```

#### O que está REALMENTE implementado:
```java
Health:  +12 (máximo de vida aumenta em 12)
Stamina: +10 (máximo de stamina aumenta em 10)
Mana:    +6  (máximo de mana aumenta em 6)
```

#### ❌ NÃO IMPLEMENTADO:
- ❌ Bônus em proficiências
- ❌ Redução de tempo de craft
- ❌ Melhor relação com NPCs

---

## 📋 Resumo Técnico

### Implementação Atual (RaceManager.java)

```java
private static final Map<Race, Map<String, Float>> RACE_BONUSES = Map.of(
    Race.ELF, Map.of(
        "Health", 0f,      // +0 vida
        "Stamina", 8f,     // +8 stamina
        "Mana", 15f        // +15 mana
    ),
    Race.ORC, Map.of(
        "Health", 25f,     // +25 vida
        "Stamina", 12f,    // +12 stamina
        "Mana", -8f        // -8 mana
    ),
    Race.HUMAN, Map.of(
        "Health", 12f,     // +12 vida
        "Stamina", 10f,    // +10 stamina
        "Mana", 6f         // +6 mana
    )
);
```

### Método de Aplicação

```java
Modifier.ModifierTarget.MAX    // Modifica o MÁXIMO do stat
StaticModifier.CalculationType.ADDITIVE  // Adiciona/subtrai valor fixo
```

**Isso significa**: Os valores são adicionados/subtraídos aos máximos de Health, Stamina e Mana. Não afeta dano, defesa, velocidade, ou qualquer outro atributo.

---

## 🐛 Problemas Identificados

### 1. **Discrepância UI vs Implementação**
- A UI promete 15+ efeitos diferentes
- Apenas 3 stats são realmente modificados
- **Impacto**: Expectativa do jogador não corresponde à realidade

### 2. **Bug de "Travamento de Dano"**
- Sistema pode estar interferindo com mecânicas base do Hytale
- Modificadores podem estar sobrescrevendo valores incorretos
- **Necessário**: Testes para verificar se `EntityStatsModule.get()` afeta combat stats

### 3. **API Deprecated**
```java
EntityStatMap stats = EntityStatsModule.get(player); // deprecated in API
```
- Usando API marcada para remoção
- Pode causar comportamentos inesperados

---

## 🔧 Recomendações

### Opção A: Ajustar UI para Realidade Atual
Alterar descrições para refletir apenas os stats modificados:

**Elfo:**
- Positivos: Stamina e Mana aumentadas
- Negativos: Vida base normal

**Orc:**
- Positivos: Vida e Stamina muito aumentadas
- Negativos: Mana reduzida

**Humano:**
- Positivos: Todos os stats moderadamente aumentados
- Negativos: Nenhum se destaca

### Opção B: Implementar Efeitos Prometidos
Requer implementar sistemas adicionais:
- Sistema de modificadores de dano
- Sistema de relação com mobs
- Hooks em eventos de combate/movimento
- Sistema de velocidade personalizado
- etc.

### Opção C: Híbrido
1. Manter os 3 stats atuais funcionais
2. Remover promessas não implementadas da UI
3. Adicionar disclaimer: "Bônus adicionais em desenvolvimento"

---

## 📊 Status de Implementação

| Feature | Prometido | Implementado | Status |
|---------|-----------|--------------|--------|
| Health modificado | ✅ | ✅ | ✅ Funcional |
| Stamina modificado | ✅ | ✅ | ✅ Funcional |
| Mana modificado | ✅ | ✅ | ✅ Funcional |
| Modificadores de dano | ✅ | ❌ | ❌ Não implementado |
| Resistências | ✅ | ❌ | ❌ Não implementado |
| Velocidade | ✅ | ❌ | ❌ Não implementado |
| Comportamento de mobs | ✅ | ❌ | ❌ Não implementado |
| Sistema de fome | ✅ | ❌ | ❌ Não implementado |
| Sistema de cura | ✅ | ❌ | ❌ Não implementado |
| Proficiências | ✅ | ❌ | ❌ Não implementado |
| Crafting | ✅ | ❌ | ❌ Não implementado |
| NPCs | ✅ | ❌ | ❌ Não implementado |

**Taxa de Implementação: 20% (3/15 features)**

---

## 🔍 Investigação Necessária

### Bug do "Dano Travado"

Possíveis causas a investigar:

1. **Stats incorretos sendo modificados**
   - Verificar se "Health", "Stamina", "Mana" são os nomes corretos na API
   - Checar lista completa de stats disponíveis: `stats.getAll()`

2. **Timing de aplicação**
   - Stats sendo aplicados antes do player estar totalmente inicializado
   - Conflito com outros sistemas que definem valores base

3. **Modificador incorreto**
   - `ModifierTarget.MAX` vs `ModifierTarget.BASE` vs `ModifierTarget.CURRENT`
   - `CalculationType.ADDITIVE` vs `CalculationType.MULTIPLICATIVE`

4. **Persistência não implementada**
   - Stats são redefinidos ao fazer login/respawn?
   - Modificadores precisam ser reaplicados em certos eventos?

### Código de Debug Sugerido

```java
public static void debugStats(Player player) {
    EntityStatMap stats = EntityStatsModule.get(player);
    if (stats == null) {
        System.out.println("Stats is null!");
        return;
    }
    
    // Listar todos os stats disponíveis
    for (var stat : stats.getAll()) {
        System.out.println("Stat: " + stat.getName() + 
            " | Base: " + stat.getBase() + 
            " | Current: " + stat.getCurrent() + 
            " | Max: " + stat.getMax());
    }
}
```

---

## 📝 Conclusão

O sistema atual é um **prototype funcional** que modifica com sucesso os stats básicos de Health, Stamina e Mana. No entanto, há uma grande lacuna entre as expectativas criadas pela UI e a funcionalidade real.

Para resolver o problema de "dano travado" do Orc, é necessário:
1. Verificar se os nomes dos stats estão corretos
2. Adicionar logging para debug
3. Testar se o sistema de stats afeta combat inadvertidamente
4. Considerar usar uma API não-deprecated

Para uma experiência completa, será necessário implementar os 12 efeitos restantes ou ajustar as descrições da UI para corresponder à realidade atual.
