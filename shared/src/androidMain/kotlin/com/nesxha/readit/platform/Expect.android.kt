package com.nesxha.readit.platform

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun systemUtcOffsetMinutes(ms: Long): Int = java.util.TimeZone.getDefault().getOffset(ms) / 60_000

actual val IoDispatcher: CoroutineDispatcher = Dispatchers.IO

actual inline fun <T> syncOn(lock: Any, block: () -> T): T = synchronized(lock, block)

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) = androidx.activity.compose.BackHandler(enabled, onBack)
