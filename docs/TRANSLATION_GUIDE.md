# Translation Guide

## Overview

Orbis and Dungeons features a comprehensive multi-language translation system that allows server administrators and players to customize the mod's language or add new translations.

## Supported Languages

| Code | Language | Status |
|------|----------|--------|
| `en` | English | ✅ Complete (Default) |
| `pt_br` | Português (Brasil) | ✅ Complete |
| `ru` | Русский (Russian) | ✅ Complete |
| `es` | Español (Spanish) | ✅ Complete |

## Translation File Location

### Important: Correct Path

Translation files are **per-world** and must be placed in the correct location:

```
UserData/Saves/[YourWorldName]/mods/OrbisAndDungeons_RaceSelection/languages/
```

### Common Mistake

❌ **WRONG**: `UserData/Mods/` (this is for JAR files, not translations)

✅ **CORRECT**: `UserData/Saves/[WorldName]/mods/OrbisAndDungeons_RaceSelection/languages/`

### Why Per-World?

Hytale's mod system uses per-world data directories. This allows:
- Different language settings per world
- Independent configurations for each save
- Safer testing without affecting other worlds
- Better multiplayer server management

### Finding Your World Directory

1. Navigate to `UserData/Saves/`
2. Find your world folder (e.g., `"New World"`, `"Survival Server"`, etc.)
3. Inside, find the `mods/` folder
4. Look for `OrbisAndDungeons_RaceSelection/`
5. The `languages/` folder should be inside

**Full Example Path**:
```
C:\Users\YourName\AppData\Roaming\Hytale\UserData\Saves\My World\mods\OrbisAndDungeons_RaceSelection\languages\
```

## Changing Language

### Command

```
/racesetlanguage --confirm --language=<code>
```

### Examples

```bash
# Change to English
/racesetlanguage --confirm --language=en

# Change to Spanish
/racesetlanguage --confirm --language=es

# Change to Portuguese
/racesetlanguage --confirm --language=pt_br

# Change to Russian
/racesetlanguage --confirm --language=ru
```

### Checking Available Languages

Run the command without `--confirm` to see available languages:

```bash
/racesetlanguage
```

Output:
```
Available languages: en, pt_br, ru, es
Current language: en
```

## Adding Custom Translations

### Step 1: Create Translation File

1. Navigate to your world's languages folder
2. Create a new `.json` file (e.g., `fr.json` for French)
3. Use UTF-8 encoding (important for special characters)

### Step 2: Copy Template

Use `en.json` as a template. Copy it and rename:

```bash
# Linux/Mac
cp en.json fr.json

# Windows (PowerShell)
Copy-Item en.json fr.json
```

### Step 3: Translate Content

Open the file and translate all values (not keys):

```json
{
    "language.name": "Français",
    "language.code": "fr",
    "command.raceinfo.title": "&6=== Infos de Race pour &f%s &6===",
    "ui.race_selection.title": "Sélectionnez Votre Race",
    ...
}
```

**Important Rules**:
- Keep the keys (left side) in English
- Only translate the values (right side)
- Preserve color codes (e.g., `&6`, `&f`)
- Preserve format placeholders (e.g., `%s`, `%d`)
- Use UTF-8 encoding for special characters

### Step 4: Load Translation

#### Option A: Server Restart
Simply restart the server. The mod will auto-detect new language files.

#### Option B: Hot Reload (No Restart)
Use the reload command:

```bash
/racereload
```

This reloads both configurations and translations without restarting.

### Step 5: Select Your Language

```bash
/racesetlanguage --confirm --language=fr
```

## Translation File Structure

### Complete Structure

A translation file contains 127 keys organized into categories:

#### Language Metadata
```json
"language.name": "English",
"language.code": "en"
```

#### Command Messages
```json
"command.raceinfo.title": "&6=== Race Info for &f%s &6===",
"command.racetrade.changed_self": "&6Your race has been changed to &f%s&6!",
```

#### UI Elements
```json
"ui.race_selection.title": "Select Your Race",
"ui.race_selection.strengths": "STRENGTHS",
"ui.class_selection.confirm": "Confirm Selection"
```

#### Race Translations
```json
"race.human.name": "Human",
"race.human.tagline": "Balanced and adaptable.",
"race.human.strength.1": "+35 Health (135 total)",
"race.human.weakness.1": "No special abilities"
```

#### Class Translations
```json
"class.berserker.name": "Berserker",
"class.berserker.tagline": "Rage-fueled destruction.",
"class.mage.name": "Mage"
```

### Color Codes

Minecraft/Hytale color codes used in messages:

| Code | Color | Usage |
|------|-------|-------|
| `&6` | Gold | Titles, highlights |
| `&f` | White | Normal text, names |
| `&a` | Green | Success messages |
| `&c` | Red | Error messages |
| `&7` | Gray | Secondary info |
| `&e` | Yellow | Emphasis |

**Example**:
```json
"command.raceinfo.title": "&6=== Race Info for &f%s &6==="
```
Result: "**===** Race Info for **PlayerName** **===**" (gold with white name)

### Format Placeholders

Placeholders are replaced with dynamic values:

| Placeholder | Meaning | Example |
|-------------|---------|---------|
| `%s` | String | Player name, race name, class name |
| `%d` | Integer | Days, counts |

**Example**:
```json
"command.raceinfo.race": "&6Race: &f%s"
```
Result: "Race: **Orc**" (the `%s` becomes the race name)

**Important**: Always keep placeholders in the same order and count!

## Custom Race/Class Translations

### For Custom Races

When you add a custom race to `races_config.json`:

```json
{
  "id": "vampire",
  "displayName": "Vampire",
  "tagline": "Creature of the night"
}
```

You **DON'T** need to add translations! The UI reads directly from config.

However, if you want translated names:

```json
// In es.json
"race.vampire.name": "Vampiro",
"race.vampire.tagline": "Criatura de la noche"
```

The UI will use translations if available, otherwise falls back to config values.

### For Custom Classes

Same principle applies to classes in `classes_config.json`.

## Fallback System

The translation system has a smart fallback chain:

1. **Current Language**: Check selected language
2. **English Fallback**: If key missing, use English
3. **Key Itself**: If missing in English too, show the key

**Example**:
```
Selected: es (Spanish)
Key: "new.custom.key"

1. Check es.json → not found
2. Check en.json → not found
3. Display: "new.custom.key" (the key itself)
```

This prevents crashes and helps identify missing translations.

## Troubleshooting

### Issue: Language not changing

**Possible Causes**:
1. Translation file in wrong location
2. JSON syntax error
3. File not UTF-8 encoded
4. Need to reload

**Solutions**:
1. Verify correct path (see "Translation File Location" above)
2. Validate JSON syntax: https://jsonlint.com/
3. Save file as UTF-8 (most text editors have this option)
4. Run `/racereload` or restart server

### Issue: Special characters appear wrong

**Problem**: "Español" shows as "Espa�ol"

**Solution**: Save file with UTF-8 encoding

**In Notepad++**:
- Encoding → Convert to UTF-8 (without BOM)

**In VSCode**:
- Bottom right → Select encoding → UTF-8

**In Notepad (Windows 11)**:
- Save As → Encoding: UTF-8

### Issue: Translation file not detected

**Checklist**:
- [ ] File extension is `.json` (not `.json.txt`)
- [ ] File is in correct directory
- [ ] File has valid JSON syntax
- [ ] Language code matches filename (e.g., `fr.json` → code `"fr"`)
- [ ] Ran `/racereload` or restarted server

### Issue: Colors not showing

**Problem**: Text shows `"&6Gold Text"` literally

**Cause**: Color codes are processed by Hytale. If showing literally, it's a display issue.

**Note**: Color codes work in:
- Chat messages
- Command outputs
- System messages

They may not work in:
- UI labels (these use style definitions)
- External tools

## Best Practices

### For Translators

1. **Preserve Intent**: Translate meaning, not word-for-word
2. **Keep Length Similar**: UI has limited space
3. **Maintain Tone**: Keep the fantasy RPG style
4. **Test Thoroughly**: Check all UI screens
5. **Use Native Terms**: Use proper gaming terminology in your language

### For Server Admins

1. **Back Up Originals**: Keep a copy of default translations
2. **Version Control**: Track changes to custom translations
3. **Test Before Production**: Try on test server first
4. **Document Changes**: Note what you customized
5. **Share With Community**: Help others with your translations

### For Mod Developers

1. **Don't Hardcode Text**: Always use translation keys
2. **Provide Context**: Comment what each key is for
3. **Use Clear Keys**: `ui.race_selection.title` not `t1`
4. **Group by Feature**: Keep related keys together
5. **Document Format**: Explain placeholders and color codes

## Contributing Translations

Want to add a new language to the official mod?

1. Create complete translation file
2. Test thoroughly in-game
3. Verify all 127 keys translated
4. Check special characters display correctly
5. Submit to mod repository with:
   - Translation file
   - Language name and code
   - Your name/credit (if desired)

## Example: Adding French

### 1. Create File
Create `fr.json` in languages folder

### 2. Add Content
```json
{
    "language.name": "Français",
    "language.code": "fr",
    "command.raceinfo.title": "&6=== Infos de Race pour &f%s &6===",
    ...
    (all 127 keys)
}
```

### 3. Reload
```bash
/racereload
```

### 4. Verify
```bash
/racesetlanguage
# Should show: "Available languages: en, pt_br, ru, es, fr"
```

### 5. Activate
```bash
/racesetlanguage --confirm --language=fr
```

### 6. Test
- Open race selection UI
- Use various commands
- Check all text displays correctly
- Verify special characters work

## Technical Details

### How It Works

1. **Initialization**: On mod startup, creates `languages/` folder
2. **Extraction**: Copies default translations from JAR to folder
3. **Loading**: Scans folder for all `.json` files
4. **Parsing**: Loads each file into memory as key-value map
5. **Selection**: Reads language preference from `language_config.json`
6. **Translation**: When text needed, looks up key in current language

### File Storage

- **Location**: Per-world data directory
- **Format**: JSON (key-value pairs)
- **Encoding**: UTF-8
- **Size**: ~10-15 KB per language
- **Persistence**: Survives server restarts

### Performance

- All translations loaded into memory at startup
- O(1) lookup time (HashMap)
- No disk I/O during translation
- Minimal performance impact
- Hot-reload support via `/racereload`

## Reference

### All Translation Keys

For a complete list of all 127 translation keys, see:
- `src/main/resources/languages/en.json` (in mod source)
- Your world's `languages/en.json` (after first run)

### Related Commands

- `/racesetlanguage` - Change language
- `/racereload` - Reload translations and configs
- `/raceinfo` - Test translated command output

### Related Files

- `languages/*.json` - Translation files
- `language_config.json` - Current language preference
- `races_config.json` - Race configuration
- `classes_config.json` - Class configuration

---

**Last Updated**: 2026-02-09
**Supported Languages**: 4 (en, pt_br, ru, es)
**Total Translation Keys**: 127
