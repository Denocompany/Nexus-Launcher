package com.nexuslauncher.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class NexusDataStore(private val context: Context) {

    private val ds get() = context.nexusDataStore

    private fun <T> flow(key: androidx.datastore.preferences.core.Preferences.Key<T>, default: T): Flow<T> =
        ds.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map { it[key] ?: default }

    private suspend fun <T> save(key: androidx.datastore.preferences.core.Preferences.Key<T>, value: T) {
        ds.edit { it[key] = value }
    }

    // ── Performance ──────────────────────────────────────────────────────────
    fun getPerfPreset(): Flow<Int>           = flow(NexusKeys.PERF_PRESET, 2)
    fun getBoostEnabled(): Flow<Boolean>     = flow(NexusKeys.PERF_BOOST_ENABLED, false)
    fun getTargetFps(): Flow<Float>          = flow(NexusKeys.PERF_TARGET_FPS, 60f)

    suspend fun setPerfPreset(v: Int)        { save(NexusKeys.PERF_PRESET, v) }
    suspend fun setBoostEnabled(v: Boolean)  { save(NexusKeys.PERF_BOOST_ENABLED, v) }
    suspend fun setTargetFps(v: Float)       { save(NexusKeys.PERF_TARGET_FPS, v) }

    // ── Visual ───────────────────────────────────────────────────────────────
    fun getVisualQuality(): Flow<Int>        = flow(NexusKeys.VISUAL_QUALITY, 2)
    fun getVisualHdr(): Flow<Boolean>        = flow(NexusKeys.VISUAL_HDR, false)
    fun getVisualTheme(): Flow<Int>          = flow(NexusKeys.VISUAL_THEME, 0)
    fun getVisualColorAccent(): Flow<Int>    = flow(NexusKeys.VISUAL_COLOR_ACCENT, 0)
    fun getVisualShaders(): Flow<Boolean>    = flow(NexusKeys.VISUAL_SHADERS, true)
    fun getVisualShaderIdx(): Flow<Int>      = flow(NexusKeys.VISUAL_SHADER_IDX, 0)
    fun getVisualBrightness(): Flow<Float>   = flow(NexusKeys.VISUAL_BRIGHTNESS, 0.7f)
    fun getVisualSaturation(): Flow<Float>   = flow(NexusKeys.VISUAL_SATURATION, 0.5f)
    fun getVisualContrast(): Flow<Float>     = flow(NexusKeys.VISUAL_CONTRAST, 0.5f)

    suspend fun setVisualQuality(v: Int)        { save(NexusKeys.VISUAL_QUALITY, v) }
    suspend fun setVisualHdr(v: Boolean)        { save(NexusKeys.VISUAL_HDR, v) }
    suspend fun setVisualTheme(v: Int)          { save(NexusKeys.VISUAL_THEME, v) }
    suspend fun setVisualColorAccent(v: Int)    { save(NexusKeys.VISUAL_COLOR_ACCENT, v) }
    suspend fun setVisualShaders(v: Boolean)    { save(NexusKeys.VISUAL_SHADERS, v) }
    suspend fun setVisualShaderIdx(v: Int)      { save(NexusKeys.VISUAL_SHADER_IDX, v) }
    suspend fun setVisualBrightness(v: Float)   { save(NexusKeys.VISUAL_BRIGHTNESS, v) }
    suspend fun setVisualSaturation(v: Float)   { save(NexusKeys.VISUAL_SATURATION, v) }
    suspend fun setVisualContrast(v: Float)     { save(NexusKeys.VISUAL_CONTRAST, v) }

    // ── Mods ─────────────────────────────────────────────────────────────────
    fun getModsEnabled(): Flow<Set<String>>           = flow(NexusKeys.MODS_ENABLED, emptySet())
    fun getResourcePacksEnabled(): Flow<Set<String>>  = flow(NexusKeys.RESOURCE_PACKS_ENABLED, emptySet())

    suspend fun setModsEnabled(v: Set<String>)           { save(NexusKeys.MODS_ENABLED, v) }
    suspend fun setResourcePacksEnabled(v: Set<String>)  { save(NexusKeys.RESOURCE_PACKS_ENABLED, v) }

    // ── Instances ────────────────────────────────────────────────────────────
    fun getLastInstance(): Flow<String>          = flow(NexusKeys.LAST_INSTANCE_ID, "")
    fun getFavoriteInstances(): Flow<Set<String>> = flow(NexusKeys.FAVORITE_INSTANCES, emptySet())

    suspend fun setLastInstance(id: String)            { save(NexusKeys.LAST_INSTANCE_ID, id) }
    suspend fun setFavoriteInstances(v: Set<String>)   { save(NexusKeys.FAVORITE_INSTANCES, v) }

    // ── Accounts ─────────────────────────────────────────────────────────────
    fun getActiveAccount(): Flow<String>         = flow(NexusKeys.ACTIVE_ACCOUNT, "")
    fun getOfflineAccounts(): Flow<Set<String>>  = flow(NexusKeys.OFFLINE_ACCOUNTS, emptySet())
    fun getActiveSkin(): Flow<String>            = flow(NexusKeys.ACTIVE_SKIN, "")

    suspend fun setActiveAccount(id: String)          { save(NexusKeys.ACTIVE_ACCOUNT, id) }
    suspend fun setOfflineAccounts(v: Set<String>)    { save(NexusKeys.OFFLINE_ACCOUNTS, v) }
    suspend fun setActiveSkin(id: String)             { save(NexusKeys.ACTIVE_SKIN, id) }

    // ── Settings ─────────────────────────────────────────────────────────────
    fun getAutoUpdate(): Flow<Boolean>      = flow(NexusKeys.SETTINGS_AUTO_UPDATE, true)
    fun getTelemetry(): Flow<Boolean>       = flow(NexusKeys.SETTINGS_TELEMETRY, false)
    fun getExperimental(): Flow<Boolean>    = flow(NexusKeys.SETTINGS_EXPERIMENTAL, false)
    fun getAutoSave(): Flow<Boolean>        = flow(NexusKeys.SETTINGS_AUTO_SAVE, true)
    fun getCrashReport(): Flow<Boolean>     = flow(NexusKeys.SETTINGS_CRASH_REPORT, true)
    fun getFullscreen(): Flow<Boolean>      = flow(NexusKeys.SETTINGS_FULLSCREEN, false)
    fun getVsync(): Flow<Boolean>           = flow(NexusKeys.SETTINGS_VSYNC, true)
    fun getResolution(): Flow<Int>          = flow(NexusKeys.SETTINGS_RESOLUTION, 0)
    fun getTouchSens(): Flow<Int>           = flow(NexusKeys.SETTINGS_TOUCH_SENS, 1)
    fun getHaptic(): Flow<Boolean>          = flow(NexusKeys.SETTINGS_HAPTIC, true)
    fun getGamepad(): Flow<Boolean>         = flow(NexusKeys.SETTINGS_GAMEPAD, false)
    fun getBgLoad(): Flow<Boolean>          = flow(NexusKeys.SETTINGS_BG_LOAD, true)
    fun getLanguage(): Flow<String>         = flow(NexusKeys.SETTINGS_LANGUAGE, "Português (Brasil)")
    fun getSettingsTheme(): Flow<Int>       = flow(NexusKeys.SETTINGS_THEME, 0)
    fun getNexusAI(): Flow<Boolean>         = flow(NexusKeys.SETTINGS_NEXUS_AI, false)
    fun getPredictiveBoost(): Flow<Boolean> = flow(NexusKeys.SETTINGS_PREDICTIVE_BOOST, false)
    fun getGpuOverclock(): Flow<Boolean>    = flow(NexusKeys.SETTINGS_GPU_OVERCLOCK, false)
    fun getBeta(): Flow<Boolean>            = flow(NexusKeys.SETTINGS_BETA, false)
    fun getGamePath(): Flow<String>         = flow(NexusKeys.SETTINGS_GAME_PATH, "")

    suspend fun setAutoUpdate(v: Boolean)        { save(NexusKeys.SETTINGS_AUTO_UPDATE, v) }
    suspend fun setTelemetry(v: Boolean)         { save(NexusKeys.SETTINGS_TELEMETRY, v) }
    suspend fun setExperimental(v: Boolean)      { save(NexusKeys.SETTINGS_EXPERIMENTAL, v) }
    suspend fun setAutoSave(v: Boolean)          { save(NexusKeys.SETTINGS_AUTO_SAVE, v) }
    suspend fun setCrashReport(v: Boolean)       { save(NexusKeys.SETTINGS_CRASH_REPORT, v) }
    suspend fun setFullscreen(v: Boolean)        { save(NexusKeys.SETTINGS_FULLSCREEN, v) }
    suspend fun setVsync(v: Boolean)             { save(NexusKeys.SETTINGS_VSYNC, v) }
    suspend fun setResolution(v: Int)            { save(NexusKeys.SETTINGS_RESOLUTION, v) }
    suspend fun setTouchSens(v: Int)             { save(NexusKeys.SETTINGS_TOUCH_SENS, v) }
    suspend fun setHaptic(v: Boolean)            { save(NexusKeys.SETTINGS_HAPTIC, v) }
    suspend fun setGamepad(v: Boolean)           { save(NexusKeys.SETTINGS_GAMEPAD, v) }
    suspend fun setBgLoad(v: Boolean)            { save(NexusKeys.SETTINGS_BG_LOAD, v) }
    suspend fun setLanguage(v: String)           { save(NexusKeys.SETTINGS_LANGUAGE, v) }
    suspend fun setSettingsTheme(v: Int)         { save(NexusKeys.SETTINGS_THEME, v) }
    suspend fun setNexusAI(v: Boolean)           { save(NexusKeys.SETTINGS_NEXUS_AI, v) }
    suspend fun setPredictiveBoost(v: Boolean)   { save(NexusKeys.SETTINGS_PREDICTIVE_BOOST, v) }
    suspend fun setGpuOverclock(v: Boolean)      { save(NexusKeys.SETTINGS_GPU_OVERCLOCK, v) }
    suspend fun setBeta(v: Boolean)              { save(NexusKeys.SETTINGS_BETA, v) }
    suspend fun setGamePath(v: String)           { save(NexusKeys.SETTINGS_GAME_PATH, v) }

    // ── Solar System ─────────────────────────────────────────────────────────
    fun getLastPlanet(): Flow<String>    = flow(NexusKeys.LAST_PLANET, "")
    suspend fun setLastPlanet(id: String){ save(NexusKeys.LAST_PLANET, id) }
}
