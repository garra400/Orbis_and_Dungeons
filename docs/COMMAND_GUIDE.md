
# Command Guide - Orbis and Dungeons

> **Version:** 2026.2.11  
> **Based on:** [Hytale Command API](https://hytalemodding.dev/en/docs/plugin/creating-commands)

This document details all available commands in the Orbis and Dungeons mod.

---

## Table of Contents

1. [Race Commands](#race-commands-race)
2. [Class Commands](#class-commands-class)
3. [Language Commands](#language-commands-language)
4. [Quick Reference](#quick-reference)
5. [Permissions](#permissions)

---

## Race Commands (`/race`)

Collection of commands for race management.

### `/race select`

Opens the race selection UI.

```
/race select [--player <name>]
```

| Argument | Type | Description |
|----------|------|-------------|
| `--player` | Optional | Target player name (admin) |

**Examples:**
```
/race select                    # Opens UI for you
/race select --player Steve     # Opens UI for Steve (admin)
```

---

### `/race change`

Muda a raça diretamente sem UI.

```
/race change <raça> [--player <nome>]
```

| Argumento | Tipo | Descrição |
|-----------|------|-----------|
| `raça` | Obrigatório | ID da raça (elf, orc, human) |
| `--player` | Opcional | Nome do jogador alvo (admin) |

**Exemplos:**
```
/race change elf                   # Muda sua raça para Elfo
/race change orc --player Alex     # Muda a raça de Alex para Orc
```

---

### `/race reset`

Remove a raça atual e reseta os stats.

```
/race reset [--player <nome>]
```

| Argumento | Tipo | Descrição |
|-----------|------|-----------|
| `--player` | Opcional | Nome do jogador alvo (admin) |

**Exemplos:**
```
/race reset                     # Reseta sua raça
/race reset --player Steve      # Reseta a raça de Steve
```

---

### `/race info`

Mostra informações sobre a raça atual.

```
/race info [--player <nome>]
```

| Argumento | Tipo | Descrição |
|-----------|------|-----------|
| `--player` | Opcional | Nome do jogador alvo |

**Saída:**
```
=== Informações de Raça ===
Raça: Orc
HP Bonus: +75
Stamina Bonus: +0
Forças: Força brutal, Alta resistência
Fraquezas: Magia fraca, Pouca agilidade
```

---

### `/race reload`

Recarrega as configurações de raças do JSON.

```
/race reload
```

> **Nota:** Este comando é apenas para administradores.

---

## Comandos de Classe (`/class`)

Coleção de comandos para gerenciamento de classes.

### `/class select`

Abre a interface de seleção de classe.

```
/class select [--player <nome>]
```

| Argumento | Tipo | Descrição |
|-----------|------|-----------|
| `--player` | Opcional | Nome do jogador alvo (admin) |

**Exemplos:**
```
/class select                    # Abre UI para você
/class select --player Steve     # Abre UI para Steve (admin)
```

---

### `/class change`

Muda a classe diretamente sem UI.

```
/class change <classe> [--player <nome>]
```

| Argumento | Tipo | Descrição |
|-----------|------|-----------|
| `classe` | Obrigatório | ID da classe (berserker, archer, etc.) |
| `--player` | Opcional | Nome do jogador alvo (admin) |

**Exemplos:**
```
/class change berserker             # Muda sua classe para Berserker
/class change archer --player Alex  # Muda a classe de Alex para Archer
```

---

### `/class reset`

Remove a classe atual e reseta os bônus.

```
/class reset [--player <nome>]
```

| Argumento | Tipo | Descrição |
|-----------|------|-----------|
| `--player` | Opcional | Nome do jogador alvo (admin) |

---

### `/class info`

Mostra informações sobre a classe atual.

```
/class info [--player <nome>]
```

| Argumento | Tipo | Descrição |
|-----------|------|-----------|
| `--player` | Opcional | Nome do jogador alvo |

**Saída:**
```
=== Informações de Classe ===
Classe: Berserker
HP Modifier: -25
Stamina Modifier: +8
Bônus de Arma: +30% machado/machado de batalha
Forças: Dano massivo, Fúria
Fraquezas: Defesa baixa
```

---

## Comandos de Idioma (`/language`)

Coleção de comandos para gerenciamento de idioma.

### `/language set`

Define o idioma do servidor.

```
/language set <código>
```

| Argumento | Tipo | Descrição |
|-----------|------|-----------|
| `código` | Obrigatório | Código do idioma (en, pt_br, es, ru) |

**Códigos Disponíveis:**

| Código | Idioma |
|--------|--------|
| `en` | English |
| `pt_br` | Português (Brasil) |
| `es` | Español |
| `ru` | Русский |

**Exemplos:**
```
/language set pt_br    # Define para Português
/language set en       # Define para Inglês
```

---

### `/language list`

Lista todos os idiomas disponíveis.

```
/language list
```

**Saída:**
```
=== Idiomas Disponíveis ===
en - English
pt_br - Português (Brasil)
es - Español
ru - Русский
```

---

### `/language current`

Mostra o idioma atual do servidor.

```
/language current
```

---

## Referência Rápida

### Tabela de Comandos

| Comando | Descrição | Admin |
|---------|-----------|-------|
| `/race select` | Abre UI de seleção de raça | ❌ |
| `/race change <raça>` | Muda raça sem UI | ❌ |
| `/race reset` | Remove raça atual | ❌ |
| `/race info` | Mostra informações da raça | ❌ |
| `/race reload` | Recarrega configurações | ✅ |
| `/class select` | Abre UI de seleção de classe | ❌ |
| `/class change <classe>` | Muda classe sem UI | ❌ |
| `/class reset` | Remove classe atual | ❌ |
| `/class info` | Mostra informações da classe | ❌ |
| `/language set <código>` | Define idioma | ✅ |
| `/language list` | Lista idiomas | ❌ |
| `/language current` | Mostra idioma atual | ❌ |

### Raças Disponíveis

| ID | Nome | HP | Stamina |
|----|------|-----|---------|
| `elf` | Elfo | +0 | +15 |
| `orc` | Orc | +75 | +0 |
| `human` | Humano | +35 | +5 |

### Classes Disponíveis

| ID | Nome | HP | Stamina | Arma |
|----|------|-----|---------|------|
| `berserker` | Berserker | -25 | +8 | +30% machado |
| `swordsman` | Espadachim | +10 | +5 | +20% espada |
| `crusader` | Cruzado | +30 | +0 | +15% maça |
| `assassin` | Assassino | -20 | +10 | +35% adaga |
| `archer` | Arqueiro | -35 | +8 | +40% arco |

---

## Permissões

O mod usa o sistema de permissões nativo do Hytale.

### Comandos de Admin

Os comandos que afetam outros jogadores (`--player`) requerem permissões de administrador:

```
/perm add <jogador> orbis.admin
```

### Estrutura de Permissões

| Permissão | Descrição |
|-----------|-----------|
| `orbis.admin` | Acesso completo a todos os comandos |
| `orbis.race.reload` | Permissão para recarregar configs |
| `orbis.language.set` | Permissão para mudar idioma |

---

## Implementação Técnica

### Arquitetura de Comandos

O mod usa `AbstractCommandCollection` para organizar subcomandos:

```java
public class RaceCommands extends AbstractCommandCollection {
    public RaceCommands() {
        super("race", "Race management commands");
        addSubCommand(new SelectCommand());
        addSubCommand(new ChangeCommand());
        // ...
    }
}
```

### Tipos de Argumentos Usados

```java
// Argumento obrigatório
RequiredArg<String> raceArg = withRequiredArg("race", "Race ID", ArgTypes.STRING);

// Argumento opcional (--player)
OptionalArg<String> playerArg = withOptionalArg("player", "Target player", ArgTypes.STRING);
```

Para mais detalhes sobre a API de comandos, veja [API_HYTALE_REFERENCIA.md](API_HYTALE_REFERENCIA.md#sistema-de-comandos).

---

*Documentação gerada em Fevereiro 2026*
