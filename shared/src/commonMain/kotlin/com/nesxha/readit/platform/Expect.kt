package com.nesxha.readit.platform

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineDispatcher

/** Dispatcher untuk kerja berat/IO (Dispatchers.IO di Android, Default di web). */
expect val IoDispatcher: CoroutineDispatcher

/** Kunci antar-thread di Android; di web (satu thread) cukup menjalankan blok. */
expect inline fun <T> syncOn(lock: Any, block: () -> T): T

/** Selisih zona waktu lokal terhadap UTC (menit) pada waktu [ms]. */
expect fun systemUtcOffsetMinutes(ms: Long): Int

/** Tombol kembali sistem (Android). Di web tidak melakukan apa-apa. */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
