<div align="center">

# ⚡ NEXUS LAUNCHER

**A professional Android Minecraft launcher built on ZalithLauncher/PojavLauncher**  
*Forge, Fabric, Quilt, NeoForge · Solar System Navigation UI · Real-time Telemetry*

[![Version](https://img.shields.io/badge/version-2.0.0.0-00E5FF?style=for-the-badge)](https://github.com/Denocompany/Nexus-Launcher/releases)
[![Platform](https://img.shields.io/badge/platform-Android%208.0+-green?style=for-the-badge&logo=android)](https://developer.android.com)
[![Engine](https://img.shields.io/badge/engine-ZalithLauncher-FF6D00?style=for-the-badge)](https://github.com/ZalithLauncher/ZalithLauncher)
[![License](https://img.shields.io/badge/license-GPL--3.0-blue?style=for-the-badge)](LICENSE)

</div>

---

## 🪐 What is Nexus Launcher?

Nexus Launcher is a fully-featured Minecraft: Java Edition launcher for Android, built on top of the proven [ZalithLauncher](https://github.com/ZalithLauncher/ZalithLauncher) (PojavLauncher fork) engine. It replaces the stock launcher UI with a completely custom **Solar System navigation interface** built in Jetpack Compose, where each "planet" represents a module of the launcher.

### Key Differentiators

| Feature | Nexus Launcher | ZalithLauncher |
|---------|---------------|----------------|
| Navigation UI | Solar System (Compose) | Fragment-based |
| Instance Management | Isolated folder structure | Shared .minecraft |
| Performance Monitor | Real-time CPU/RAM/FPS | Basic |
| Boost Engine | 1-click optimization | Manual |
| Session Tracker | Full history + export | None |
| Theme System | LUMINA design system | Material |

---

## 🌌 Solar System Navigation

Each planet in the solar system maps to a launcher module:

| Planet | Module | Description |
|--------|--------|-------------|
| ☀️ **Núcleo Nexus** | Sun / Hub | Central navigation |
| 🔵 **NEXUS PRIME** | Home | Last instance, quick launch, system status |
| 🟠 **AETHERION** | Performance | CPU/RAM/FPS monitor, Nexus Boost |
| 🔷 **LUMINA** | Visual | Shaders, HDR, themes, accessibility |
| 🟢 **MODARA** | Mods | Real mod scanning (Fabric/Forge/Quilt metadata) |
| 🔴 **CURSEFORGE ORBITAL** | Download | Version/mod installer |
| ⚫ **INSTARRION** | Instances | Create/manage game instances |
| ⬜ **CHRONOS** | Reports | Session history, FPS analytics |
| 🔵 **PERSONA** | Accounts | Offline + Microsoft OAuth accounts |
| 🌤 **CLOUD NEXUS** | Backups | Save backup & restore |
| 🟣 **LAB-X** | Experimental | Beta features |
| 🟡 **HELIOS CONTROL** | Settings | Full launcher configuration |

---

## ✨ Features

### Instance Management (`NexusInstanceManager`)
- Full isolated `.minecraft` folder structure per instance
- Versions: Vanilla, Fabric, Forge, NeoForge, Quilt
- Supports: `1.7.10` through `1.21.4`
- Import existing PojavLauncher instances automatically
- Favorite, rename, duplicate, remove instances
- Custom base directory (internal or external storage)

### Download System (`NexusDownloadManager`)
- Integrates with `GameInstaller` from ZalithLauncher
- Downloads vanilla client + libraries + assets via Mojang API
- Installs Fabric/Forge/Quilt/NeoForge loaders
- Real-time progress bar in UI

### Mod Manager (`NexusModManager`)
- Scans real `.jar` files in `/mods` folder
- Parses metadata from `fabric.mod.json`, `META-INF/mods.toml`, `quilt.mod.json`
- Enable/disable mods by moving between `/mods` and `/mods.disabled`
- Shows: name, version, author, file size, loader type

### Account System (`NexusAccountManager`)
- Offline accounts (create/remove/switch)
- Microsoft OAuth (bridges to ZalithLauncher's `MicrosoftBackgroundLogin`)
- Active account persisted via DataStore

### System Monitor (`NexusSystemMonitor`)
- Real `/proc/stat` CPU parsing
- RAM via `ActivityManager.MemoryInfo`
- GPU usage estimation
- Thermal throttling detection
- FPS tracking via frame callback

### Session Tracker (`NexusSessionTracker`)
- Records start/end time, instance name, FPS avg/min/max
- Persists to JSON in launcher base dir (last 50 sessions)
- Exports to formatted text report
- Powers CHRONOS analytics dashboard

### Performance Tier Engine
- `TierDecisionEngine`: auto-detects device tier (T1–T5)
- `TierProfile`: optimal presets per tier (FPS, renderer, effects)
- `NexusBoostEngine`: 1-click GC + trim + preset application

---

## 🛠 Architecture

```
ZalithLauncher/src/main/java/
├── com/nexuslauncher/
│   ├── NexusMainActivity.kt         ← Entry point (Compose)
│   ├── core/
│   │   ├── NexusInstanceManager.kt  ← Instance CRUD + folder structure
│   │   ├── NexusDownloadManager.kt  ← Version + loader download
│   │   ├── NexusLaunchManager.kt    ← Game launch bridge
│   │   ├── NexusModManager.kt       ← Real mod scanning (JAR metadata)
│   │   ├── NexusAccountManager.kt   ← Account bridge to ZalithLauncher
│   │   ├── NexusSessionTracker.kt   ← Session recording + JSON persist
│   │   ├── NexusSetupChecker.kt     ← First-run checklist
│   │   ├── NexusSystemMonitor.kt    ← CPU/RAM/FPS real-time monitor
│   │   ├── NexusBoostEngine.kt      ← 1-click optimization
│   │   ├── TierDecisionEngine.kt    ← Hardware tier detection
│   │   └── TierProfile.kt           ← Tier presets
│   ├── datastore/
│   │   ├── NexusDataStore.kt        ← Jetpack DataStore (all settings)
│   │   └── NexusKeys.kt             ← DataStore preference keys
│   ├── navigation/
│   │   ├── NexusNavHost.kt          ← Compose NavHost
│   │   ├── NexusRoute.kt            ← Route definitions
│   │   └── PlanetId.kt              ← Planet enum
│   ├── ui/
│   │   ├── solar/
│   │   │   ├── SolarSystemScreen.kt ← Animated solar system canvas
│   │   │   ├── SolarSystemViewModel.kt
│   │   │   └── PlanetNode.kt        ← Planet data model
│   │   ├── HomeScreen.kt            ← NEXUS PRIME
│   │   ├── PerformanceScreen.kt     ← AETHERION
│   │   ├── VisualScreen.kt          ← LUMINA
│   │   ├── ModsScreen.kt            ← MODARA
│   │   ├── InstancesScreen.kt       ← INSTARRION
│   │   ├── ReportsScreen.kt         ← CHRONOS
│   │   ├── AccountsScreen.kt        ← PERSONA
│   │   └── SettingsScreen.kt        ← HELIOS CONTROL
│   └── theme/
│       └── NexusTheme.kt            ← NEXUS design system
└── com/movtery/zalithlauncher/       ← ZalithLauncher engine (unchanged)
    ├── launch/LaunchGame.kt
    ├── feature/version/              ← VersionsManager, GameInstaller
    ├── feature/accounts/             ← AccountsManager, LocalAccountUtils
    └── tasks/MinecraftDownloader.kt
```

---

## 🚀 Building

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK API 34
- NDK r25c (for native libraries)

### Build Steps

```bash
# Clone
git clone https://github.com/Denocompany/Nexus-Launcher.git
cd Nexus-Launcher

# Debug APK
./gradlew :ZalithLauncher:assembleDebug

# Release APK (requires signing config)
./gradlew :ZalithLauncher:assembleRelease
```

### Signing Release APK

```bash
# Generate keystore (first time only)
keytool -genkey -v -keystore nexus-release.jks \
  -alias nexus -keyalg RSA -keysize 2048 -validity 10000

# Configure in ZalithLauncher/build.gradle:
# signingConfigs { release { ... } }
```

---

## 📋 Requirements

| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| Android | 8.0 (API 26) | 11.0+ (API 30+) |
| RAM | 3 GB | 6 GB+ |
| Storage | 1 GB free | 4 GB+ free |
| CPU | 4 cores | 8 cores (Snapdragon 865+) |
| GPU | OpenGL ES 3.0 | Vulkan 1.1+ |

---

## 🎮 Usage

1. **First Launch**: Nexus Launcher guides you through a setup checklist in HELIOS CONTROL
2. **Set Game Path**: Configure where instances are stored (Settings → Game)
3. **Create Account**: Go to PERSONA → Add Offline or Microsoft account
4. **Create Instance**: Go to INSTARRION → "+ Nova" → select version + loader
5. **Install**: The launcher downloads Minecraft + loader automatically
6. **Play**: NEXUS PRIME → INICIAR JOGO

---

## 📦 Application Info

| Field | Value |
|-------|-------|
| Application ID | `com.denocompany.nexuslauncher` |
| Version Name | `2.0.0.0` |
| Version Code | `200000` |
| Min SDK | `26` (Android 8.0) |
| Target SDK | `34` (Android 14) |
| Base Engine | ZalithLauncher 1.5.0.0 |

---

## 🙏 Credits

- **[ZalithLauncher](https://github.com/ZalithLauncher/ZalithLauncher)** — launcher engine, renderers, game integration
- **[PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher)** — original Android Minecraft launcher
- **[Fabric](https://fabricmc.net/)** / **[Forge](https://files.minecraftforge.net/)** / **[Quilt](https://quiltmc.org/)** / **[NeoForge](https://neoforged.net/)** — mod loaders

---

## 📄 License

This project is licensed under the **GNU General Public License v3.0**.  
See [LICENSE](LICENSE) for details.

---

<div align="center">
Made with ⚡ by <a href="https://github.com/Denocompany">Deno Company</a>
</div>