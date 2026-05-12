package com.nexuslauncher.datastore

import androidx.datastore.preferences.core.*

object NexusKeys {
    // Performance
    val PERF_PRESET             = intPreferencesKey("perf_preset")
    val PERF_BOOST_ENABLED      = booleanPreferencesKey("perf_boost_enabled")
    val PERF_TARGET_FPS         = floatPreferencesKey("perf_target_fps")

    // Visual
    val VISUAL_QUALITY          = intPreferencesKey("visual_quality")
    val VISUAL_HDR              = booleanPreferencesKey("visual_hdr")
    val VISUAL_THEME            = intPreferencesKey("visual_theme")
    val VISUAL_COLOR_ACCENT     = intPreferencesKey("visual_color_accent")
    val VISUAL_SHADERS          = booleanPreferencesKey("visual_shaders")
    val VISUAL_SHADER_IDX       = intPreferencesKey("visual_shader_idx")
    val VISUAL_BRIGHTNESS       = floatPreferencesKey("visual_brightness")
    val VISUAL_SATURATION       = floatPreferencesKey("visual_saturation")
    val VISUAL_CONTRAST         = floatPreferencesKey("visual_contrast")

    // Settings gerais
    val SETTINGS_AUTO_UPDATE    = booleanPreferencesKey("settings_auto_update")
    val SETTINGS_TELEMETRY      = booleanPreferencesKey("settings_telemetry")
    val SETTINGS_EXPERIMENTAL   = booleanPreferencesKey("settings_experimental")
    val SETTINGS_AUTO_SAVE      = booleanPreferencesKey("settings_auto_save")
    val SETTINGS_CRASH_REPORT   = booleanPreferencesKey("settings_crash_report")
    val SETTINGS_FULLSCREEN     = booleanPreferencesKey("settings_fullscreen")
    val SETTINGS_VSYNC          = booleanPreferencesKey("settings_vsync")
    val SETTINGS_RESOLUTION     = intPreferencesKey("settings_resolution")
    val SETTINGS_TOUCH_SENS     = intPreferencesKey("settings_touch_sensitivity")
    val SETTINGS_HAPTIC         = booleanPreferencesKey("settings_haptic")
    val SETTINGS_GAMEPAD        = booleanPreferencesKey("settings_gamepad")
    val SETTINGS_BG_LOAD        = booleanPreferencesKey("settings_bg_load")
    val SETTINGS_LANGUAGE       = stringPreferencesKey("settings_language")
    val SETTINGS_THEME          = intPreferencesKey("settings_theme")
    val SETTINGS_NEXUS_AI       = booleanPreferencesKey("settings_nexus_ai")
    val SETTINGS_PREDICTIVE_BOOST = booleanPreferencesKey("settings_predictive_boost")
    val SETTINGS_GPU_OVERCLOCK  = booleanPreferencesKey("settings_gpu_overclock")
    val SETTINGS_BETA           = booleanPreferencesKey("settings_beta")
    val SETTINGS_GAME_PATH      = stringPreferencesKey("settings_game_path")

    // Mods
    val MODS_ENABLED            = stringSetPreferencesKey("mods_enabled")
    val RESOURCE_PACKS_ENABLED  = stringSetPreferencesKey("resourcepacks_enabled")

    // Instâncias
    val LAST_INSTANCE_ID        = stringPreferencesKey("last_instance_id")
    val FAVORITE_INSTANCES      = stringSetPreferencesKey("favorite_instances")

    // Contas
    val ACTIVE_ACCOUNT          = stringPreferencesKey("active_account")
    val OFFLINE_ACCOUNTS        = stringSetPreferencesKey("offline_accounts")
    val ACTIVE_SKIN             = stringPreferencesKey("active_skin")

    // Sistema Solar
    val LAST_PLANET             = stringPreferencesKey("last_planet")
}
