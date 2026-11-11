package com.nesxha.readit.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nesxha.readit.auth.AuthError
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.PinRules
import com.nesxha.readit.auth.Res
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.nesxha.readit.platform.BackHandler

/** Ganti PIN: PIN lama → PIN baru → konfirmasi. Wajib online. */
@Composable
fun ChangePinScreen(onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var step by remember { mutableIntStateOf(0) }
    var old by remember { mutableStateOf("") }
    var new by remember { mutableStateOf("") }
    var again by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<AuthError?>(null) }
    var shake by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }

    fun back() { error = null; if (step == 0 || done) onDone() else { step--; if (step == 1) { new = ""; again = "" } } }
    BackHandler { back() }

    fun fail(e: AuthError) { error = e; shake++ }

    fun save() {
        busy = true
        scope.launch {
            when (val r = Member.changePin(old, new)) {
                is Res.Ok -> done = true
                is Res.Err -> {
                    when (r.error) {
                        is AuthError.WrongPin, is AuthError.Locked, AuthError.LockedForever -> { old = ""; new = ""; again = ""; step = 0 }
                        AuthError.WeakPin -> { new = ""; again = ""; step = 1 }
                        else -> again = ""
                    }
                    fail(r.error)
                }
            }
            busy = false
        }
    }

    AuroraBackground {
        if (done) {
            SuccessView("PIN Berhasil Diganti", "Gunakan PIN baru saat masuk berikutnya.", "Selesai", onDone)
            return@AuroraBackground
        }
        AuthPage(onBack = ::back) {
            Spacer(Modifier.height(8.dp))
            AnimatedContent(step, label = "pin", transitionSpec = {
                (slideInHorizontally { if (targetState > initialState) it / 3 else -it / 3 } + fadeIn()) togetherWith
                    (slideOutHorizontally { if (targetState > initialState) -it / 3 else it / 3 } + fadeOut())
            }) { st ->
                Column(Modifier.fillMaxWidth()) {
                    when (st) {
                        0 -> PinStep(
                            "PIN Lama", "Masukkan PIN yang sekarang", old, busy = false, shake = shake,
                            locked = error is AuthError.Locked || error == AuthError.LockedForever, error = error, onUnlocked = { error = null },
                            onDigit = { d -> if (old.length < 6) { old += d; if (error !is AuthError.Locked) error = null; if (old.length == 6) scope.launch { delay(150); step = 1 } } },
                            onDelete = { old = old.dropLast(1) },
                        )
                        1 -> PinStep(
                            "PIN Baru", "6 angka yang hanya kamu yang tahu", new, busy = false, shake = shake,
                            locked = false, error = error, onUnlocked = {}, hint = "Hindari tanggal lahir dan angka berurutan.",
                            onDigit = { d ->
                                if (new.length < 6) {
                                    new += d; error = null
                                    if (new.length == 6) {
                                        val p = PinRules.problem(new)
                                        when {
                                            p != null -> scope.launch { fail(AuthError.Invalid(p)); delay(350); new = "" }
                                            new == old -> scope.launch { fail(AuthError.Invalid("PIN baru harus berbeda dari PIN lama.")); delay(350); new = "" }
                                            else -> scope.launch { delay(150); step = 2 }
                                        }
                                    }
                                }
                            },
                            onDelete = { new = new.dropLast(1) },
                        )
                        else -> PinStep(
                            "Konfirmasi PIN Baru", "Ketik ulang PIN baru", again, busy = busy, shake = shake,
                            locked = false, error = error, onUnlocked = {},
                            onDigit = { d ->
                                if (again.length < 6) {
                                    again += d; error = null
                                    if (again.length == 6) {
                                        if (again == new) save()
                                        else scope.launch { fail(AuthError.Invalid("PIN tidak sama. Ulangi PIN baru.")); delay(900); new = ""; again = ""; error = null; step = 1 }
                                    }
                                }
                            },
                            onDelete = { again = again.dropLast(1) },
                        )
                    }
                }
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Langkah ${step + 1} dari 3", style = MaterialTheme.typography.labelMedium, color = AuthColors.faint)
            }
        }
    }
}
