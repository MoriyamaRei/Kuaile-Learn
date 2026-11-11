package com.nesxha.readit.platform

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@JsFun("(ms) => -new Date(ms).getTimezoneOffset()")
private external fun jsOffset(ms: Double): Int

actual fun systemUtcOffsetMinutes(ms: Long): Int = jsOffset(ms.toDouble())

actual val IoDispatcher: CoroutineDispatcher = Dispatchers.Default

actual inline fun <T> syncOn(lock: Any, block: () -> T): T = block()

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {}
