package com.nexuslauncher.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * BackgroundLoader — utilitário para operações pesadas em background.
 * Fase 1: esqueleto. Será expandido com carregamento de assets e JRE na Fase 2.
 */
object BackgroundLoader {

    /**
     * Executa um bloco de código em uma coroutine de IO.
     * Use para leitura de arquivos, verificação de JRE, download de componentes, etc.
     */
    suspend fun <T> runOnIo(block: suspend () -> T): T =
        withContext(Dispatchers.IO) { block() }

    /**
     * Executa um bloco de código na thread principal (UI).
     */
    suspend fun <T> runOnMain(block: suspend () -> T): T =
        withContext(Dispatchers.Main) { block() }
}
