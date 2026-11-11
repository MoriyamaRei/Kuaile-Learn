package com.nesxha.readit.util

import com.nesxha.readit.platform.InkEngine
import com.nesxha.readit.platform.PlatUi

/**
 * Pengenal tulisan tambahan dari platform (Google ML Kit di Android).
 * Di web tidak ada; layar otomatis memakai pengenal sederhana [Handwriting].
 */
object InkRecognizer {
    val available: Boolean get() = PlatUi.ui.ink != null
    val state: InkEngine.State get() = PlatUi.ui.ink?.state ?: InkEngine.State.GAGAL

    suspend fun ensure(onState: (InkEngine.State) -> Unit = {}): Boolean {
        val e = PlatUi.ui.ink ?: run { onState(InkEngine.State.GAGAL); return false }
        return e.ensure(onState)
    }

    suspend fun recognize(strokes: List<List<Pt>>, width: Float, height: Float): List<Char>? =
        PlatUi.ui.ink?.recognize(strokes, width, height)
}
