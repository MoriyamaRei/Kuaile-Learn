package com.nesxha.readit.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.auth.AuthError
import com.nesxha.readit.auth.DemoAuthApi
import com.nesxha.readit.auth.Kmc
import com.nesxha.readit.auth.KmcStatus
import com.nesxha.readit.auth.Nickname
import com.nesxha.readit.auth.PinRules
import com.nesxha.readit.auth.Res
import com.nesxha.readit.auth.Session
import com.nesxha.readit.ui.components.Confetti
import com.nesxha.readit.ui.theme.UiFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import com.nesxha.readit.platform.PlatUi
import com.nesxha.readit.platform.pad2
import com.nesxha.readit.platform.epochDayToDate
import com.nesxha.readit.platform.localEpochDay
import com.nesxha.readit.platform.nowMs
import com.nesxha.readit.platform.BackHandler

/**
 * Pintu masuk aplikasi: layar loading → (belum masuk) layar akun → aplikasi.
 * [onReady] dipanggil setiap kali aplikasi siap dipakai (misalnya untuk menyalakan streak).
 */
@Composable
fun AuthGate(onReady: () -> Unit, content: @Composable () -> Unit) {
    // Tanpa login: aplikasi langsung terbuka.
    LaunchedEffect(Unit) { onReady() }
    Box(Modifier.fillMaxSize()) { content() }
}

// ---------------- Layar loading ----------------

@Composable
private fun SplashScreen() {
    val logo = remember { Animatable(0f) }
    val title = remember { Animatable(0f) }
    val exit = remember { Animatable(0f) }
    var status by remember { mutableStateOf("Menyiapkan Kuaile Learn…") }
    LaunchedEffect(Unit) {
        val t0 = nowMs()
        launch { logo.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 180f)) }
        launch { delay(280); title.animateTo(1f, tween(650, easing = FastOutSlowInEasing)) }
        launch {
            delay(700)
            status = if (!Auth.hasSession) "Menyiapkan Kuaile Learn…" else if (Auth.hasInternet()) "Memeriksa KMC…" else "Membuka Mode Offline…"
        }
        val next = Auth.evaluate()
        val wait = 1900 - (nowMs() - t0)
        if (wait > 0) delay(wait)
        exit.animateTo(1f, tween(320, easing = FastOutSlowInEasing))
        Auth.apply(next)
    }
    val tr = rememberInfiniteTransition(label = "float")
    val bob by tr.animateFloat(-6f, 6f, infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bob")
    AuroraBackground {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.size(210.dp).graphicsLayer {
                    val s = logo.value * (1f + exit.value * 0.25f)
                    scaleX = s; scaleY = s; alpha = (logo.value * (1f - exit.value)).coerceIn(0f, 1f)
                    rotationZ = (1f - logo.value) * -25f
                    translationY = bob * density
                },
                contentAlignment = Alignment.Center,
            ) {
                Orbit(Modifier.size(210.dp))
                LogoMark(112.dp)
            }
            Spacer(Modifier.height(18.dp))
            Column(
                Modifier.graphicsLayer { alpha = title.value * (1f - exit.value); translationY = (1f - title.value) * 40f * density },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Kuaile Learn", style = MaterialTheme.typography.displayMedium, color = Color.White)
                Text("Kuaile Mandarin Club", style = MaterialTheme.typography.titleMedium, color = AuthColors.soft, letterSpacing = 1.5.sp)
            }
            Spacer(Modifier.weight(1f))
            Column(Modifier.graphicsLayer { alpha = title.value * (1f - exit.value) }, horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingDots()
                Spacer(Modifier.height(12.dp))
                AnimatedContent(status, label = "status", transitionSpec = {
                    (fadeIn(tween(300)) + slideInVertically { it / 2 }) togetherWith (fadeOut(tween(200)) + slideOutVertically { -it / 2 })
                }) { Text(it, style = MaterialTheme.typography.bodyMedium, color = AuthColors.soft) }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

// ---------------- Alur akun ----------------

private enum class Screen { WELCOME, LOGIN, REGISTER, FORGOT }

@Composable
private fun AuthFlow(notice: String?) {
    var screen by remember { mutableStateOf(Screen.WELCOME) }
    var prefill by remember { mutableStateOf(Auth.lastKmc ?: "") }
    BackHandler(enabled = screen != Screen.WELCOME) { screen = Screen.WELCOME }
    AuroraBackground {
        AnimatedContent(
            screen, label = "auth",
            transitionSpec = {
                val fwd = targetState.ordinal > initialState.ordinal
                (slideInHorizontally(tween(420, easing = FastOutSlowInEasing)) { if (fwd) it / 3 else -it / 3 } + fadeIn(tween(420))) togetherWith
                    (slideOutHorizontally(tween(420, easing = FastOutSlowInEasing)) { if (fwd) -it / 3 else it / 3 } + fadeOut(tween(250)))
            },
        ) { s ->
            when (s) {
                Screen.WELCOME -> WelcomeScreen(notice, onLogin = { Auth.clearNotice(); screen = Screen.LOGIN }, onRegister = { Auth.clearNotice(); screen = Screen.REGISTER }, onForgot = { screen = Screen.FORGOT })
                Screen.LOGIN -> LoginScreen(prefill, onBack = { screen = Screen.WELCOME }, onForgot = { screen = Screen.FORGOT }, onRegister = { prefill = it; screen = Screen.REGISTER })
                Screen.REGISTER -> RegisterScreen(prefill, onBack = { screen = Screen.WELCOME }, onLogin = { prefill = it; screen = Screen.LOGIN })
                Screen.FORGOT -> ForgotScreen(onBack = { screen = Screen.WELCOME }, onRegister = { screen = Screen.REGISTER })
            }
        }
    }
}

/** Kerangka layar akun: tombol kembali, judul, isi yang bisa digulir. */
@Composable
internal fun AuthPage(onBack: (() -> Unit)?, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()) {
        if (onBack != null) Box(
            Modifier.padding(start = 12.dp, top = 8.dp).size(46.dp).clip(CircleShape).background(AuthColors.glass).clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali", tint = Color.White) }
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
            content = content,
        )
    }
}

@Composable
private fun WelcomeScreen(notice: String?, onLogin: () -> Unit, onRegister: () -> Unit, onForgot: () -> Unit) {
    val show = remember { Animatable(0f) }
    LaunchedEffect(Unit) { show.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
    fun Modifier.rise(i: Int) = graphicsLayer {
        val p = ((show.value - i * 0.12f) / 0.6f).coerceIn(0f, 1f)
        alpha = p; translationY = (1f - p) * 36f * density
    }
    AuthPage(null) {
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().rise(0), contentAlignment = Alignment.Center) {
            Mascot(210.dp)
        }
        Spacer(Modifier.height(12.dp))
        Text("Kuaile Learn", style = MaterialTheme.typography.displayMedium, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().rise(1))
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().rise(2), contentAlignment = Alignment.Center) {
            Text(
                "Khusus Anggota Kuaile Mandarin Club", style = MaterialTheme.typography.labelLarge, color = Color.White,
                modifier = Modifier.clip(RoundedCornerShape(50)).background(AuthColors.glassStrong).padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Belajar membaca hanzi setiap hari lewat flashcard, kuis, dan latihan menulis.",
            style = MaterialTheme.typography.bodyLarge, color = AuthColors.soft, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).rise(3),
        )
        Spacer(Modifier.height(32.dp))
        if (notice != null) {
            GlassCard(Modifier.fillMaxWidth().rise(4)) { Text(notice, style = MaterialTheme.typography.bodyMedium, color = Color.White) }
            Spacer(Modifier.height(14.dp))
        }
        if (Auth.api.isDemo) {
            DemoCard(Modifier.rise(4))
            Spacer(Modifier.height(18.dp))
        }
        Column(Modifier.rise(5), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AuthButton("Masuk", onClick = onLogin)
            AuthButton("Daftar", ghost = true, onClick = onRegister)
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().rise(6), contentAlignment = Alignment.Center) { AuthLink("Lupa PIN?", onClick = onForgot) }
    }
}

@Composable
private fun DemoCard(modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf(false) }
    GlassCard(modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).clickable { open = !open }, padding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Science, null, tint = AuthColors.gold, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text("Mode Demo", style = MaterialTheme.typography.titleSmall, color = AuthColors.gold)
                Text("Server belum tersambung. Data hanya tersimpan di ${com.nesxha.readit.platform.thisDevice}.", style = MaterialTheme.typography.bodySmall, color = AuthColors.soft)
            }
            Text(if (open) "Tutup" else "Contoh", style = MaterialTheme.typography.labelMedium, color = Color.White)
        }
        AnimatedVisibility(open) {
            Column(Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DemoAuthApi.SAMPLES.forEach { (k, d) ->
                    Column {
                        Text(k, style = MaterialTheme.typography.labelLarge, color = Color.White)
                        Text(d, style = MaterialTheme.typography.bodySmall, color = AuthColors.faint)
                    }
                }
            }
        }
    }
}

// ---------------- Isian ----------------

/** Menyisipkan tanda hubung setelah 5 angka pertama: 0000100 → 00001-00. */
private object KmcDigits : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val d = text.text
        val out = if (d.length > 5) d.take(5) + "-" + d.drop(5) else d
        return TransformedText(AnnotatedString(out), object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = if (offset <= 5) offset else offset + 1
            override fun transformedToOriginal(offset: Int) = (if (offset <= 5) offset else offset - 1).coerceAtMost(d.length)
        })
    }
}

@Composable
private fun GlassField(
    value: String, onChange: (String) -> Unit, label: String, placeholder: String,
    modifier: Modifier = Modifier, prefix: String? = null, keyboard: KeyboardOptions = KeyboardOptions.Default,
    visual: VisualTransformation = VisualTransformation.None, error: Boolean = false, onDone: () -> Unit = {},
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = AuthColors.soft)
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(18.dp)).background(AuthColors.glass)
                .border(1.5.dp, if (error) AuthColors.error else AuthColors.line, RoundedCornerShape(18.dp)).padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val style = TextStyle(fontFamily = UiFont, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = Color.White)
            if (prefix != null) Text(prefix, style = style.copy(color = AuthColors.faint))
            Box(Modifier.weight(1f)) {
                if (value.isEmpty()) Text(placeholder, style = style.copy(color = Color(0x66FFFFFF), fontWeight = FontWeight.Medium))
                BasicTextField(
                    value, onChange, singleLine = true, textStyle = style, cursorBrush = SolidColor(Color.White),
                    keyboardOptions = keyboard, keyboardActions = KeyboardActions(onDone = { onDone() }, onNext = { onDone() }),
                    visualTransformation = visual, modifier = Modifier.fillMaxWidth(),
                )
            }
            trailing?.invoke()
        }
    }
}

@Composable
private fun KmcField(digits: String, onChange: (String) -> Unit, error: Boolean, onDone: () -> Unit) {
    val scan = PlatUi.ui.rememberCardScanner { kmc -> onChange(digitsOf(kmc)) }
    GlassField(
        digits, { onChange(it.filter(Char::isDigit).take(7)) }, "Nomor KMC", "00001-00", prefix = "KMC-",
        keyboard = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        visual = KmcDigits, error = error, onDone = onDone,
        trailing = if (scan == null) null else {
            {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(AuthColors.glassStrong).clickable(onClick = scan),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.QrCodeScanner, "Scan Kartu", tint = Color.White, modifier = Modifier.size(22.dp)) }
            }
        },
    )
    Spacer(Modifier.height(6.dp))
    Text(if (scan != null) "Ketuk ikon pindai untuk scan kartu KMC." else "Ketik 7 angka nomor KMC di kartu anggotamu.", style = MaterialTheme.typography.bodySmall, color = AuthColors.faint)
}

private fun digitsOf(kmc: String) = kmc.removePrefix("KMC").filter(Char::isDigit).take(7)

/** Hitung mundur untuk KMC yang terkunci sementara. */
@Composable
private fun rememberCountdown(until: Long): Long {
    var left by remember(until) { mutableLongStateOf(until - nowMs()) }
    LaunchedEffect(until) {
        while (left > 0) { delay(250); left = until - nowMs() }
    }
    return left.coerceAtLeast(0)
}

private fun mmss(ms: Long): String {
    val s = (ms + 999) / 1000
    return "${pad2(s / 60)}:${pad2(s % 60)}"
}

/** Pesan untuk kesalahan; kunci sementara ditampilkan dengan hitung mundur. */
@Composable
private fun ErrorLine(error: AuthError?, onUnlocked: () -> Unit = {}) {
    when (error) {
        null -> {}
        is AuthError.Locked -> {
            val left = rememberCountdown(error.until)
            LaunchedEffect(left == 0L) { if (left == 0L) onUnlocked() }
            AuthMessage("KMC terkunci sementara. Coba lagi dalam ${mmss(left)}.")
        }
        else -> AuthMessage(error.message)
    }
}

// ---------------- Masuk ----------------

@Composable
private fun LoginScreen(prefill: String, onBack: () -> Unit, onForgot: () -> Unit, onRegister: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    var digits by remember { mutableStateOf(digitsOf(prefill)) }
    var step by remember { mutableIntStateOf(0) }
    var pin by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<AuthError?>(null) }
    var shake by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf<Session?>(null) }
    val kmc = Kmc.normalize(digits)
    BackHandler(enabled = step == 1 && done == null) { step = 0; pin = ""; error = null }

    fun next() {
        if (kmc == null || busy) return
        focus.clearFocus()
        busy = true; error = null
        scope.launch {
            when (val r = Auth.api.check(kmc)) {
                is Res.Err -> error = r.error
                is Res.Ok -> when (r.value) {
                    KmcStatus.TERDAFTAR -> step = 1
                    KmcStatus.AKTIF -> error = AuthError.NotRegistered
                    KmcStatus.DIBLOKIR -> error = AuthError.Blocked
                    KmcStatus.TIDAK_ADA -> error = AuthError.NotActive
                }
            }
            busy = false
        }
    }

    fun submit() {
        val k = kmc ?: return
        busy = true; error = null
        scope.launch {
            when (val r = Auth.login(k, pin)) {
                is Res.Ok -> { done = r.value; delay(1500); Auth.enter(r.value) }
                is Res.Err -> { error = r.error; shake++; delay(350); pin = "" }
            }
            busy = false
        }
    }

    val s = done
    if (s != null) {
        val st = com.nesxha.readit.data.Store.now
        SuccessView(
            "${s.nickname.ifEmpty { "Kawan" }}!", "Menyiapkan ruang belajarmu…",
            kicker = "Selamat Datang,", info = "🔥 Streak ${st.streak} hari · ⭐ Level ${st.levelInfo.level}",
        )
        return
    }

    AuthPage(onBack = { if (step == 1) { step = 0; pin = ""; error = null } else onBack() }) {
        AnimatedContent(step, label = "login", transitionSpec = {
            (slideInHorizontally { if (targetState > initialState) it / 3 else -it / 3 } + fadeIn()) togetherWith (slideOutHorizontally { if (targetState > initialState) -it / 3 else it / 3 } + fadeOut())
        }) { st ->
            Column(Modifier.fillMaxWidth()) {
                if (st == 0) {
                    AuthTitle("Masuk", "Gunakan nomor KMC dan PIN-mu.")
                    Spacer(Modifier.height(30.dp))
                    KmcField(digits, { digits = it; error = null }, error != null, ::next)
                    Spacer(Modifier.height(14.dp))
                    ErrorLine(error)
                    if (error == AuthError.NotRegistered) Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AuthLink("Daftar Sekarang") { onRegister(kmc ?: "") }
                    }
                    Spacer(Modifier.height(24.dp))
                    AuthButton("Lanjut", enabled = kmc != null, loading = busy, onClick = ::next)
                } else {
                    PinStep(
                        title = "Masukkan PIN", sub = kmc ?: "", pin = pin, busy = busy, shake = shake,
                        locked = error is AuthError.Locked || error == AuthError.LockedForever || error == AuthError.Blocked,
                        error = error, onUnlocked = { error = null },
                        onDigit = { if (pin.length < 6) { pin += it; error = if (error is AuthError.Locked) error else null; if (pin.length == 6) submit() } },
                        onDelete = { pin = pin.dropLast(1) },
                    )
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { AuthLink("Lupa PIN?", onClick = onForgot) }
                }
            }
        }
    }
}

/** Layar isi PIN bersama untuk Masuk, Buat PIN, dan Konfirmasi PIN. */
@Composable
internal fun PinStep(
    title: String, sub: String, pin: String, busy: Boolean, shake: Int, locked: Boolean,
    error: AuthError?, onUnlocked: () -> Unit, onDigit: (Char) -> Unit, onDelete: () -> Unit, hint: String? = null,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(64.dp).clip(CircleShape).background(AuthColors.glassStrong), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Key, null, tint = Color.White, modifier = Modifier.size(30.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text(sub, style = MaterialTheme.typography.titleSmall, color = AuthColors.soft, letterSpacing = 1.sp)
        Spacer(Modifier.height(26.dp))
        PinDots(pin.length, error != null && error !is AuthError.Locked, shake)
        Spacer(Modifier.height(14.dp))
        Box(Modifier.height(44.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when {
                busy -> LoadingDots()
                error != null -> ErrorLine(error, onUnlocked)
                hint != null -> AuthMessage(hint, color = AuthColors.faint)
            }
        }
        Spacer(Modifier.height(10.dp))
        PinPad(enabled = !busy && !locked, onDigit = onDigit, onDelete = onDelete)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
internal fun SuccessView(
    title: String, sub: String, action: String? = null, onAction: () -> Unit = {},
    kicker: String? = null, info: String? = null,
) {
    val pop = remember { Animatable(0f) }
    val text = remember { Animatable(0f) }
    var confetti by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        launch { pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 260f)) }
        delay(250); confetti = 1
        text.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value; alpha = pop.value.coerceIn(0f, 1f) }) {
                Mascot(200.dp)
                Box(
                    Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = 6.dp).size(52.dp).clip(CircleShape)
                        .background(Color(0xFF16A34A)).border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text("✓", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) }
            }
            Spacer(Modifier.height(18.dp))
            Column(
                Modifier.fillMaxWidth().graphicsLayer { alpha = text.value; translationY = (1f - text.value) * 30f * density },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (kicker != null) Text(kicker, style = MaterialTheme.typography.titleLarge, color = AuthColors.soft)
                FitText(title, MaterialTheme.typography.headlineLarge.copy(color = Color.White, textAlign = TextAlign.Center))
                Spacer(Modifier.height(8.dp))
                Text(sub, style = MaterialTheme.typography.bodyLarge, color = AuthColors.soft, textAlign = TextAlign.Center)
                if (info != null) {
                    Spacer(Modifier.height(14.dp))
                    Text(info, style = MaterialTheme.typography.labelLarge, color = Color.White,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(AuthColors.glassStrong).padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }
            if (action != null) {
                Spacer(Modifier.height(32.dp))
                AuthButton(action, onClick = onAction)
            } else {
                Spacer(Modifier.height(26.dp))
                LoadingDots()
            }
        }
        RainConfetti(confetti)
    }
}

/** Teks satu baris yang mengecil sendiri kalau terlalu panjang, supaya tidak terpotong di tengah. */
@Composable
internal fun FitText(text: String, style: TextStyle, modifier: Modifier = Modifier, minSize: Float = 18f) {
    var size by remember(text) { mutableStateOf(style.fontSize.value) }
    Text(
        text, style = style.copy(fontSize = size.sp), maxLines = 1, softWrap = false, modifier = modifier,
        onTextLayout = { r -> if (r.didOverflowWidth && size > minSize) size = (size * 0.9f).coerceAtLeast(minSize) },
    )
}

// ---------------- Daftar ----------------

private val MONTHS = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
private fun LocalDate.indo() = "$dayOfMonth ${MONTHS[monthNumber - 1]} $year"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreen(prefill: String, onBack: () -> Unit, onLogin: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    var step by remember { mutableIntStateOf(0) } // 0 KMC, 1 data lahir, 2 buat PIN, 3 konfirmasi PIN, 4 nickname, 5 selesai
    var digits by remember { mutableStateOf(digitsOf(prefill)) }
    var place by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf<LocalDate?>(null) }
    var pickDate by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var pin2 by remember { mutableStateOf("") }
    var nick by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<AuthError?>(null) }
    var shake by remember { mutableIntStateOf(0) }
    val kmc = Kmc.normalize(digits)

    fun back() {
        error = null
        when (step) {
            0, 5 -> onBack()
            3 -> { step = 2; pin = ""; pin2 = "" }
            else -> step -= 1
        }
    }
    BackHandler(enabled = step in 1..4) { back() }

    fun go(block: suspend () -> Unit) {
        if (busy) return
        focus.clearFocus(); busy = true; error = null
        scope.launch { block(); busy = false }
    }

    if (step == 5) {
        SuccessView("Pendaftaran Berhasil!", "Sekarang masuk dengan nomor KMC dan PIN yang baru kamu buat.", "Masuk Sekarang", onAction = { onLogin(kmc ?: "") })
        return
    }

    AuthPage(onBack = ::back) {
        StepBar(when (step) { 0 -> 1; 1 -> 2; 2, 3 -> 3; else -> 4 }, 4)
        Spacer(Modifier.height(10.dp))
        Text("Langkah ${when (step) { 0 -> 1; 1 -> 2; 2, 3 -> 3; else -> 4 }} dari 4", style = MaterialTheme.typography.labelMedium, color = AuthColors.faint)
        Spacer(Modifier.height(18.dp))
        AnimatedContent(step, label = "reg", transitionSpec = {
            (slideInHorizontally { if (targetState > initialState) it / 3 else -it / 3 } + fadeIn()) togetherWith (slideOutHorizontally { if (targetState > initialState) -it / 3 else it / 3 } + fadeOut())
        }) { st ->
            Column(Modifier.fillMaxWidth()) {
                when (st) {
                    0 -> {
                        AuthTitle("Daftar", "Masukkan nomor KMC yang sudah diaktifkan pengurus.")
                        Spacer(Modifier.height(28.dp))
                        val next = {
                            if (kmc != null) go {
                                when (val r = Auth.api.check(kmc)) {
                                    is Res.Err -> error = r.error
                                    is Res.Ok -> when (r.value) {
                                        KmcStatus.AKTIF -> step = 1
                                        KmcStatus.TERDAFTAR -> error = AuthError.AlreadyRegistered
                                        KmcStatus.DIBLOKIR -> error = AuthError.Blocked
                                        KmcStatus.TIDAK_ADA -> error = AuthError.NotActive
                                    }
                                }
                            }
                        }
                        KmcField(digits, { digits = it; error = null }, error != null) { next() }
                        Spacer(Modifier.height(14.dp))
                        ErrorLine(error)
                        if (error == AuthError.AlreadyRegistered) Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            AuthLink("Masuk Sekarang") { onLogin(kmc ?: "") }
                        }
                        Spacer(Modifier.height(24.dp))
                        AuthButton("Lanjut", enabled = kmc != null, loading = busy) { next() }
                    }
                    1 -> {
                        AuthTitle("Data Kelahiran", "Untuk memastikan KMC ini benar milikmu. Isi sesuai data pendaftaran klub.")
                        Spacer(Modifier.height(28.dp))
                        GlassField(
                            place, { place = it.take(40); error = null }, "Tempat Lahir", "Contoh: Batulicin",
                            keyboard = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                            error = error is AuthError.WrongBirth,
                        )
                        Spacer(Modifier.height(18.dp))
                        Text("Tanggal Lahir", style = MaterialTheme.typography.labelLarge, color = AuthColors.soft)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(18.dp)).background(AuthColors.glass)
                                .border(1.5.dp, if (error is AuthError.WrongBirth) AuthColors.error else AuthColors.line, RoundedCornerShape(18.dp))
                                .clickable { focus.clearFocus(); pickDate = true }.padding(horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                birth?.indo() ?: "Pilih tanggal", modifier = Modifier.weight(1f),
                                style = TextStyle(fontFamily = UiFont, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = if (birth == null) Color(0x66FFFFFF) else Color.White),
                            )
                            Icon(Icons.Rounded.CalendarMonth, null, tint = Color.White)
                        }
                        Spacer(Modifier.height(14.dp))
                        ErrorLine(error) { error = null }
                        Spacer(Modifier.height(24.dp))
                        AuthButton("Lanjut", enabled = place.isNotBlank() && birth != null && error !is AuthError.Locked, loading = busy) {
                            val k = kmc; val b = birth
                            if (k != null && b != null) go {
                                when (val r = Auth.api.checkBirth(k, place.trim(), b.toString())) {
                                    is Res.Ok -> step = 2
                                    is Res.Err -> { error = r.error; shake++ }
                                }
                            }
                        }
                    }
                    2 -> PinStep(
                        title = "Buat PIN", sub = "6 angka yang hanya kamu yang tahu", pin = pin, busy = false, shake = shake,
                        locked = false, error = error, onUnlocked = {}, hint = "Hindari tanggal lahir dan angka berurutan.",
                        onDigit = { d ->
                            if (pin.length < 6) {
                                pin += d; error = null
                                if (pin.length == 6) {
                                    val p = PinRules.problem(pin)
                                    if (p != null) scope.launch { error = AuthError.Invalid(p); shake++; delay(350); pin = "" }
                                    else scope.launch { delay(180); step = 3 }
                                }
                            }
                        },
                        onDelete = { pin = pin.dropLast(1) },
                    )
                    3 -> PinStep(
                        title = "Konfirmasi PIN", sub = "Ketik ulang PIN-mu", pin = pin2, busy = false, shake = shake,
                        locked = false, error = error, onUnlocked = {},
                        onDigit = { d ->
                            if (pin2.length < 6) {
                                pin2 += d; error = null
                                if (pin2.length == 6) {
                                    if (pin2 == pin) scope.launch { delay(180); step = 4 }
                                    else scope.launch {
                                        error = AuthError.Invalid("PIN tidak sama. Buat ulang PIN-mu."); shake++
                                        delay(900); pin = ""; pin2 = ""; error = null; step = 2
                                    }
                                }
                            }
                        },
                        onDelete = { pin2 = pin2.dropLast(1) },
                    )
                    else -> {
                        AuthTitle("Buat Nickname", "Nama ini yang tampil di leaderboard. Nama asli dan nomor KMC-mu tidak ditampilkan.")
                        Spacer(Modifier.height(28.dp))
                        val problem = if (nick.isEmpty()) null else Nickname.problem(nick)
                        GlassField(
                            nick, { nick = it.take(Nickname.MAX); error = null }, "Nickname", "Contoh: Xiao Long",
                            keyboard = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                            error = problem != null || error != null,
                            trailing = { Text("${Nickname.clean(nick).length}/${Nickname.MAX}", style = MaterialTheme.typography.labelMedium, color = AuthColors.faint) },
                        )
                        Spacer(Modifier.height(14.dp))
                        if (problem != null) AuthMessage(problem) else ErrorLine(error)
                        Spacer(Modifier.height(24.dp))
                        AuthButton("Selesaikan Pendaftaran", enabled = nick.isNotBlank() && problem == null, loading = busy) {
                            val k = kmc; val b = birth
                            if (k != null && b != null) go {
                                when (val r = Auth.api.register(k, place.trim(), b.toString(), pin, Nickname.clean(nick))) {
                                    is Res.Ok -> step = 5
                                    is Res.Err -> when (r.error) {
                                        is AuthError.WrongBirth -> { step = 1; error = r.error }
                                        AuthError.WeakPin -> { pin = ""; pin2 = ""; step = 2; error = r.error }
                                        else -> error = r.error
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (pickDate) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = birth?.let { it.toEpochDays() * 86_400_000L },
            initialDisplayedMonthMillis = (birth ?: LocalDate(2005, 1, 1)).toEpochDays() * 86_400_000L,
            yearRange = 1940..epochDayToDate(localEpochDay()).year,
        )
        DatePickerDialog(
            onDismissRequest = { pickDate = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { birth = LocalDate.fromEpochDays(it.floorDiv(86_400_000L).toInt()); error = null }
                    pickDate = false
                }) { Text("Pilih") }
            },
            dismissButton = { TextButton(onClick = { pickDate = false }) { Text("Batal") } },
        ) { DatePicker(state, title = { Text("Tanggal Lahir", Modifier.padding(start = 24.dp, top = 16.dp)) }) }
    }
}

// ---------------- Lupa PIN ----------------

@Composable
private fun ForgotScreen(onBack: () -> Unit, onRegister: () -> Unit) {
    AuthPage(onBack) {
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PulseIcon { Icon(Icons.Rounded.Key, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
        }
        Spacer(Modifier.height(12.dp))
        AuthTitle("Lupa PIN?", "Demi keamanan, PIN hanya bisa direset oleh pengurus KMC. Pengurus pun tidak bisa melihat PIN-mu.")
        Spacer(Modifier.height(24.dp))
        GlassCard(Modifier.fillMaxWidth()) {
            listOf(
                "Hubungi pengurus KMC dan sebutkan nomor KMC-mu.",
                "Pengurus mereset PIN-mu.",
                "Buka menu Daftar, isi nomor KMC, tempat dan tanggal lahir, lalu buat PIN baru.",
            ).forEachIndexed { i, t ->
                Row(Modifier.padding(vertical = 6.dp)) {
                    Box(Modifier.size(28.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        Text("${i + 1}", style = MaterialTheme.typography.labelLarge, color = AuthColors.mid)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(t, style = MaterialTheme.typography.bodyLarge, color = Color.White, modifier = Modifier.weight(1f).padding(top = 3.dp))
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        AuthButton("Daftar Ulang", onClick = onRegister)
        Spacer(Modifier.height(12.dp))
        AuthButton("Kembali", ghost = true, onClick = onBack)
    }
}

// ---------------- Perlu online ----------------

@Composable
private fun NeedOnlineScreen() {
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    AuroraBackground(hanzi = false) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            PulseIcon { Icon(Icons.Rounded.WifiOff, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
            Spacer(Modifier.height(20.dp))
            Text("Sambungkan Internet", style = MaterialTheme.typography.headlineMedium, color = Color.White, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text(
                "Mode Offline sudah dipakai lebih dari 3 hari. Sambungkan internet sebentar untuk memeriksa KMC-mu. Kamu tidak perlu masuk ulang, dan progresmu tetap aman.",
                style = MaterialTheme.typography.bodyLarge, color = AuthColors.soft, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            if (failed) AuthMessage("Masih belum tersambung. Periksa data seluler atau Wi-Fi.")
            Spacer(Modifier.height(20.dp))
            AuthButton("Coba Lagi", loading = busy) {
                busy = true; failed = false
                scope.launch { delay(400); Auth.boot(); busy = false; failed = Auth.phase.value == Auth.Phase.NeedOnline }
            }
        }
    }
}

// ---------------- Penanda Mode Offline ----------------

@Composable
private fun OfflineChip(p: Auth.Phase.Ready) {
    var show by remember(p.offline, p.serverIssue) { mutableStateOf(p.offline) }
    LaunchedEffect(p.offline, p.serverIssue) { if (p.offline) { delay(5000); show = false } }
    Box(Modifier.fillMaxWidth().statusBarsPadding().padding(top = 6.dp), contentAlignment = Alignment.TopCenter) {
        AnimatedVisibility(show, enter = slideInVertically { -it * 2 } + fadeIn(), exit = slideOutVertically { -it * 2 } + fadeOut()) {
            Row(
                Modifier.clip(RoundedCornerShape(50)).background(Color(0xF0172554)).clickable { show = false }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.CloudOff, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (p.serverIssue) "Server sedang bermasalah · Mode Offline"
                    else "Mode Offline · Sisa ${p.daysLeft} hari",
                    style = MaterialTheme.typography.labelLarge, color = Color.White,
                )
            }
        }
    }
}
