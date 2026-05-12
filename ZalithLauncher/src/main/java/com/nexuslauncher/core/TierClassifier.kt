package com.nexuslauncher.core

/**
 * TierClassifier — classifica o dispositivo em um tier de desempenho.
 * Usado futuramente para aplicar configurações automáticas de desempenho.
 *
 * Tiers:
 *  - LOW    : dispositivos de entrada (< 3 GB RAM ou < 4 cores)
 *  - MID    : dispositivos intermediários (3-6 GB RAM, 4-6 cores)
 *  - HIGH   : dispositivos topo de linha (> 6 GB RAM, > 6 cores)
 */
enum class PerformanceTier { LOW, MID, HIGH }

object TierClassifier {

    /**
     * Determina o tier de desempenho com base em RAM e núcleos de CPU.
     *
     * @param ramMb    RAM total em megabytes.
     * @param cpuCores Número de núcleos disponíveis.
     */
    fun classify(ramMb: Long, cpuCores: Int): PerformanceTier = when {
        ramMb > 6144 && cpuCores > 6 -> PerformanceTier.HIGH
        ramMb > 3072 && cpuCores > 3 -> PerformanceTier.MID
        else                         -> PerformanceTier.LOW
    }
}
