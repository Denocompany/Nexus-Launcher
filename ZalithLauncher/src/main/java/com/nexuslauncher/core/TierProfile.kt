package com.nexuslauncher.core

/**
 * TierProfile — Presets automáticos baseados no Tier do dispositivo.
 * Define configurações ótimas de FPS, resolução, shaders e partículas.
 */
data class TierPreset(
    val tier: NexusTier,
    val fps: Int,
    val resolution: Float,
    val shadersEnabled: Boolean,
    val particleDensity: Int,
    val bloomEnabled: Boolean,
    val antiAlias: Boolean,
    val renderDistance: Int,
    val effectsLevel: EffectsLevel,
    val renderer: RendererType
)

enum class EffectsLevel { ULTRA, HIGH, MEDIUM, LOW, MINIMAL }
enum class RendererType  { VULKAN, OPENGL_ES3, OPENGL_ES2, CANVAS }

object TierProfile {

    private val PRESET_T1 = TierPreset(
        tier            = NexusTier.T1_ULTRA,
        fps             = 120,
        resolution      = 1.0f,
        shadersEnabled  = true,
        particleDensity = 2000,
        bloomEnabled    = true,
        antiAlias       = true,
        renderDistance  = 32,
        effectsLevel    = EffectsLevel.ULTRA,
        renderer        = RendererType.VULKAN
    )

    private val PRESET_T2 = TierPreset(
        tier            = NexusTier.T2_ALTO,
        fps             = 90,
        resolution      = 1.0f,
        shadersEnabled  = true,
        particleDensity = 1200,
        bloomEnabled    = true,
        antiAlias       = true,
        renderDistance  = 24,
        effectsLevel    = EffectsLevel.HIGH,
        renderer        = RendererType.OPENGL_ES3
    )

    private val PRESET_T3 = TierPreset(
        tier            = NexusTier.T3_AVANCADO,
        fps             = 60,
        resolution      = 0.9f,
        shadersEnabled  = false,
        particleDensity = 600,
        bloomEnabled    = false,
        antiAlias       = true,
        renderDistance  = 16,
        effectsLevel    = EffectsLevel.MEDIUM,
        renderer        = RendererType.OPENGL_ES3
    )

    private val PRESET_T4 = TierPreset(
        tier            = NexusTier.T4_MEDIO,
        fps             = 45,
        resolution      = 0.75f,
        shadersEnabled  = false,
        particleDensity = 250,
        bloomEnabled    = false,
        antiAlias       = false,
        renderDistance  = 8,
        effectsLevel    = EffectsLevel.LOW,
        renderer        = RendererType.OPENGL_ES2
    )

    private val PRESET_T5 = TierPreset(
        tier            = NexusTier.T5_BAIXO,
        fps             = 30,
        resolution      = 0.5f,
        shadersEnabled  = false,
        particleDensity = 80,
        bloomEnabled    = false,
        antiAlias       = false,
        renderDistance  = 4,
        effectsLevel    = EffectsLevel.MINIMAL,
        renderer        = RendererType.CANVAS
    )

    fun presetFor(tier: NexusTier): TierPreset = when (tier) {
        NexusTier.T1_ULTRA    -> PRESET_T1
        NexusTier.T2_ALTO     -> PRESET_T2
        NexusTier.T3_AVANCADO -> PRESET_T3
        NexusTier.T4_MEDIO    -> PRESET_T4
        NexusTier.T5_BAIXO    -> PRESET_T5
    }

    fun recommendations(result: TierResult): List<String> = buildList {
        if (!result.hasVulkan)         add("Atualize o Android para ≥7 para suporte a Vulkan")
        if (result.ramGb < 3f)         add("RAM baixa: reduza mods carregados simultaneamente")
        if (!result.thermalOk)         add("Dispositivo superaquecendo: ative o modo Bateria")
        if (result.cpuCores < 4)       add("CPU limitada: reduza a distância de renderização")
        if (result.tier.isLowEnd)      add("Modo Baixo ativo: shaders e efeitos desativados")
        if (result.tier == NexusTier.T1_ULTRA) add("Hardware excelente: todos os efeitos disponíveis!")
    }
}
