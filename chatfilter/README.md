# ChatFilter Mod for Minecraft 1.20.4 (Fabric)

Replaces profanity in Minecraft chat with clean substitutes, ported from your Advanced Profanity Filter browser extension config.

## How to Build (Option A — GitHub, no installs needed)

1. Create a free account at https://github.com
2. Click **+** → **New repository**, name it `chatfilter`, set it to Public, click Create
3. Upload all these files (drag and drop them into the GitHub web UI)
4. Go to the **Actions** tab → click **Build Mod** → click **Run workflow**
5. Once it finishes (takes ~3-5 min), click the run → scroll down to **Artifacts** → download **chatfilter-mod**
6. Unzip the download — you'll find `chatfilter-1.0.0.jar` inside
7. Drop that `.jar` into your Minecraft `mods/` folder

## How to Build (Option B — Local, requires Java 17+)

You do NOT need to install Gradle. Just run:

**Windows:**
```
gradlew.bat build
```

**Mac/Linux:**
```
chmod +x gradlew
./gradlew build
```

The built `.jar` will be at `build/libs/chatfilter-1.0.0.jar`.
Drop it into your Minecraft `mods/` folder.

## Where is the mods folder?

- **Windows:** `%AppData%\.minecraft\mods\`
- **Mac:** `~/Library/Application Support/minecraft/mods/`
- **Linux:** `~/.minecraft/mods/`

Make sure you also have **Fabric Loader 0.15+** and **Fabric API** installed for 1.20.4.

## Word List

All substitutions are based on your Advanced Profanity Filter export. The rules use
case-insensitive whole-word matching (word boundaries), just like APF's "Whole Word" mode.

To change rules, edit `src/main/java/com/chatfilter/ChatFilterMod.java` and rebuild.
