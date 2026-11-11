package com.nesxha.readit.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.nesxha.readit.json.JSONObject
import com.nesxha.readit.platform.epochDayToDate
import kotlinx.datetime.LocalDate
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.concurrent.Volatile
import com.nesxha.readit.platform.syncOn
import com.nesxha.readit.platform.IoDispatcher
import com.nesxha.readit.platform.nowMs

data class AppState(
    val cards: Map<Int, Card> = emptyMap(),
    val days: Map<Long, DayLog> = emptyMap(),
    val settings: Settings = Settings(),
    val restored: Set<Long> = emptySet(),        // hari bolos yang sudah dipulihkan
    val badges: Map<String, Long> = emptyMap(),  // id lencana -> hari didapat
    val counters: Map<String, Int> = emptyMap(),
) {
    fun counter(key: String): Int = counters[key] ?: 0
    val xp: Int get() = counter(Counter.XP)
    val levelInfo: LevelInfo get() = Levels.info(xp)
    fun card(id: Int): Card = cards[id] ?: Card()
    val todayLog: DayLog get() = days[today()] ?: DayLog()

    fun dueWords(day: Long = today()): List<Word> = cards.entries
        .filter { it.value.seen && it.value.due <= day }
        .sortedBy { it.value.due }
        .mapNotNull { Words.byId[it.key] }

    val newLeftToday: Int get() = max(0, settings.newPerDay - todayLog.newWords)

    fun newWordsToday(): List<Word> =
        Words.studyOrder(settings.byTheme).asSequence().filter { !card(it.id).seen }.take(newLeftToday).toList()

    val remainingToday: Int get() = dueWords().size + newWordsToday().size
    val doneToday: Int get() = todayLog.reviews
    val goalReached: Boolean get() = doneToday > 0 && remainingToday == 0
    val goalFraction: Float get() {
        val total = doneToday + remainingToday
        return if (total == 0) 0f else doneToday.toFloat() / total
    }

    // ---------- Streak ----------
    // Hadir = membuka aplikasi. Bolos sehari: api padam dan bisa dipulihkan keesokan harinya
    // (maks. 5× per bulan). Hari yang dipulihkan menyambung rantai tetapi tidak menambah angka.

    fun attended(d: Long): Boolean = days[d]?.attended == true
    private fun linked(d: Long): Boolean = attended(d) || d in restored

    private fun chainFrom(start: Long): Int {
        var d = start; var n = 0
        while (linked(d)) { if (attended(d)) n++; d-- }
        return n
    }

    /** Streak yang padam kemarin dan masih bisa dipulihkan hari ini (0 = tidak ada). */
    val brokenStreak: Int get() {
        val t = today()
        if (linked(t - 1)) return 0
        return chainFrom(t - 2)
    }

    val restoresThisMonth: Int get() {
        val now = epochDayToDate(today())
        return restored.count { epochDayToDate(it).let { d -> d.year == now.year && d.monthNumber == now.monthNumber } }
    }

    val restoresLeft: Int get() = max(0, MAX_RESTORES - restoresThisMonth)
    val canRestore: Boolean get() = brokenStreak > 0 && restoresLeft > 0

    /** Angka streak yang ditampilkan (saat padam: angka lama yang bisa dipulihkan). */
    val streak: Int get() {
        val b = brokenStreak
        if (b > 0) return b
        val t = today()
        return if (attended(t)) chainFrom(t) else chainFrom(t - 1)
    }

    /** Api menyala: hari ini hadir dan tidak sedang padam. */
    val streakLit: Boolean get() = brokenStreak == 0 && attended(today())

    val bestStreak: Int get() {
        val keys = (days.filterValues { it.attended }.keys + restored)
        if (keys.isEmpty()) return 0
        var best = 0; var run = 0
        var d = keys.min()
        val end = max(keys.max(), today())
        while (d <= end) {
            when {
                attended(d) -> { run++; best = max(best, run) }
                d in restored -> {}
                else -> run = 0
            }
            d++
        }
        return best
    }

    fun seenCount(level: Int? = null): Int =
        Words.all.count { (level == null || it.level == level) && card(it.id).seen }

    fun masteredCount(level: Int? = null): Int =
        Words.all.count { (level == null || it.level == level) && card(it.id).status == Status.HAFAL }

    /** Kata hari ini: acak tetapi tetap sepanjang hari, dari level yang sedang dipelajari. */
    fun wordOfDay(): Word {
        val lv = (1..4).firstOrNull { l -> Words.all.any { it.level == l && !card(it.id).seen } } ?: 4
        val pool = Words.all.filter { it.level == lv }
        return pool[Random(today()).nextInt(pool.size)]
    }

    fun mostWrong(n: Int = 20): List<Pair<Word, Card>> = cards.entries
        .filter { it.value.wrong > 0 }
        .sortedWith(compareByDescending<Map.Entry<Int, Card>> { it.value.wrong }.thenBy { it.value.right })
        .take(n)
        .mapNotNull { e -> Words.byId[e.key]?.let { it to e.value } }

    /** Rata-rata kata baru per hari (14 hari terakhir yang aktif). */
    fun pacePerDay(): Double {
        val t = today()
        val recent = (0 until 14).mapNotNull { days[t - it] }.filter { it.newWords > 0 }
        return if (recent.isEmpty()) settings.newPerDay.toDouble() else recent.sumOf { it.newWords }.toDouble() / recent.size
    }

    fun estimatedFinish(): LocalDate? {
        val left = Words.all.size - seenCount()
        if (left == 0) return null
        val pace = max(0.5, pacePerDay())
        return epochDayToDate(today() + kotlin.math.ceil(left / pace).toLong())
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("v", 2)
        put("cards", JSONObject().also { o -> cards.forEach { (k, v) -> o.put(k.toString(), v.toJson()) } })
        put("days", JSONObject().also { o -> days.forEach { (k, v) -> o.put(k.toString(), v.toJson()) } })
        put("settings", settings.toJson())
        put("restored", com.nesxha.readit.json.JSONArray().also { a -> restored.sorted().forEach { a.put(it) } })
        put("badges", JSONObject().also { o -> badges.forEach { (k, v) -> o.put(k, v) } })
        put("counters", JSONObject().also { o -> counters.forEach { (k, v) -> o.put(k, v) } })
    }

    companion object {
        fun fromJson(o: JSONObject): AppState {
            val c = HashMap<Int, Card>()
            o.optJSONObject("cards")?.let { co -> co.keys().forEach { k -> c[k.toInt()] = Card.fromJson(co.getJSONObject(k)) } }
            val d = HashMap<Long, DayLog>()
            o.optJSONObject("days")?.let { dobj -> dobj.keys().forEach { k -> d[k.toLong()] = DayLog.fromJson(dobj.getJSONObject(k)) } }
            val s = o.optJSONObject("settings")?.let { Settings.fromJson(it) } ?: Settings()
            val r = HashSet<Long>()
            o.optJSONArray("restored")?.let { a -> for (i in 0 until a.length()) r += a.getLong(i) }
            val b = HashMap<String, Long>()
            o.optJSONObject("badges")?.let { bo -> bo.keys().forEach { k -> b[k] = bo.getLong(k) } }
            val ct = HashMap<String, Int>()
            o.optJSONObject("counters")?.let { co -> co.keys().forEach { k -> ct[k] = co.getInt(k) } }
            return AppState(c, d, s, r, b, ct)
        }
    }
}

const val MAX_RESTORES = 5

object Store {
    private val scope = CoroutineScope(SupervisorJob() + IoDispatcher)
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state
    val now: AppState get() = _state.value
    private var io: StoreIo? = null
    @Volatile private var loaded = false
    private var saveJob: Job? = null

    // ---------- Sinkron antarperangkat ----------
    // [own] = aktivitas di perangkat ini, [other] = salinan terakhir bagian perangkat lain (HP/web) dari server.
    // Yang tampil di layar ([state]) adalah gabungan keduanya (lihat [Merge]).
    private var own = AppState()
    private var other = AppState()
    /** KMC pemilik progres di perangkat ini (null = belum pernah sinkron). */
    private var owner: String? = null
    /** Nomor "atur ulang" dari server; naik setiap kali progres akun diatur ulang. */
    private var gen = 0
    /** Atur ulang belum terkirim ke server. */
    private var pendingReset = false
    /** Waktu (server) salinan [other] yang dimiliki. */
    private var otherAt = 0L
    /** Bagian sendiri berubah sejak terakhir dikirim. */
    private var dirty = true
    private val _changes = MutableStateFlow(0L)
    /** Bertambah setiap kali bagian sendiri berubah (pemicu sinkron). */
    val changes: StateFlow<Long> = _changes

    /** Dipanggil setelah data berubah (mis. menyegarkan widget). */
    var onChange: (() -> Unit)? = null

    /** Lencana yang baru didapat dan belum diumumkan ke pengguna. */
    private val _newBadges = MutableStateFlow<List<String>>(emptyList())
    val newBadges: StateFlow<List<String>> = _newBadges
    fun clearNewBadges() { _newBadges.value = emptyList() }

    /** Streak yang sedang dirayakan dengan animasi api besar (null = tidak ada). */
    private val _flameCelebration = MutableStateFlow<Int?>(null)
    val flameCelebration: StateFlow<Int?> = _flameCelebration
    fun clearFlameCelebration() { _flameCelebration.value = null }

    /** Tambahan XP terakhir (untuk kabar "+5 XP"); seq berubah tiap kali ada XP baru. */
    data class XpPop(val amount: Int, val seq: Long)
    private val _xpPop = MutableStateFlow<XpPop?>(null)
    val xpPop: StateFlow<XpPop?> = _xpPop
    private var xpSeq = 0L

    /** Level baru yang sedang dirayakan (null = tidak ada). */
    private val _levelUp = MutableStateFlow<Int?>(null)
    val levelUp: StateFlow<Int?> = _levelUp
    fun clearLevelUp() { _levelUp.value = null }

    /** Hari terakhir animasi api menyala diputar di Beranda (disimpan, jadi hanya sekali per hari). */
    var ignitedDay: Long
        get() = now.counter("igniteDay").toLong()
        set(v) { mark("igniteDay", v.toInt()) }

    /** Hari terakhir konfeti target harian diputar (disimpan, jadi hanya sekali per hari). */
    var celebratedDay: Long
        get() = now.counter("goalShownDay").toLong()
        set(v) { mark("goalShownDay", v.toInt()) }

    /** Tempat menyimpan progres (file di Android, localStorage di web). */
    interface StoreIo {
        fun read(): String?
        fun write(text: String)
    }

    fun init(storeIo: StoreIo) {
        if (loaded) return
        syncOn(this) {
            if (loaded) return
            io = storeIo
            storeIo.read()?.let { txt -> runCatching { load(JSONObject(txt)) } }
            loaded = true
        }
    }

    /** Baca ulang dari penyimpanan (dipakai widget yang mungkin berjalan di proses lain). */
    fun reload() = syncOn(this) {
        io?.read()?.let { txt -> runCatching { load(JSONObject(txt)) } }
    }

    private fun load(o: JSONObject) {
        if (o.has("own")) {
            own = AppState.fromJson(o.getJSONObject("own"))
            other = o.optJSONObject("other")?.let { AppState.fromJson(it) } ?: AppState()
            owner = o.optString("owner").ifEmpty { null }
            gen = o.optInt("gen", 0)
            pendingReset = o.optBoolean("reset", false)
            otherAt = o.optLong("otherAt", 0)
            dirty = o.optBoolean("dirty", true)
        } else {
            // Format lama (sebelum 1.2.1): seluruh progres milik perangkat ini.
            own = AppState.fromJson(o)
            other = AppState()
            dirty = true
        }
        _state.value = Merge.combine(own, other)
    }

    private fun fileJson(): JSONObject = JSONObject().apply {
        put("v", 3)
        put("own", own.toJson())
        put("other", other.toJson())
        put("owner", owner ?: "")
        put("gen", gen)
        put("reset", pendingReset)
        put("otherAt", otherAt)
        put("dirty", dirty)
    }

    /** Terapkan tampilan baru [next] (dari [prev]): bagian sendiri ikut berubah, lalu digabung lagi. */
    private fun commit(prev: AppState, next: AppState) {
        own = Merge.applyChange(own, prev, next, nowMs())
        dirty = true
        _state.value = Merge.combine(own, other)
        _changes.value = _changes.value + 1
    }

    /** Data untuk dikirim ke server. */
    internal class SyncOut(val own: String?, val gen: Int, val reset: Boolean, val otherAt: Long, val owner: String?)

    internal fun syncOut(): SyncOut = syncOn(this) {
        SyncOut(if (dirty || pendingReset) own.toJson().toString() else null, gen, pendingReset, otherAt, owner)
    }

    /** Akun lain masuk di perangkat ini: progres perangkat dikosongkan, lalu diambil dari server. */
    internal fun switchOwner(kmc: String) = syncOn(this) {
        if (owner == kmc) return@syncOn
        if (owner != null) {
            own = AppState(settings = own.settings); other = AppState(); gen = 0; otherAt = 0; pendingReset = false
            _state.value = Merge.combine(own, other)
        }
        owner = kmc; dirty = true
        writeLocked()
    }

    /** Server sudah diatur ulang (dari perangkat lain): kosongkan progres di sini juga. */
    internal fun serverReset(newGen: Int) = syncOn(this) {
        own = AppState(settings = own.settings, days = mapOf(today() to DayLog(opened = true)))
        other = AppState(); otherAt = 0; gen = newGen; pendingReset = false; dirty = true
        _state.value = Merge.combine(own, other)
        writeLocked()
    }

    /** Data perangkat lama dengan slot yang sama (ganti HP/browser) diambil alih tanpa dihitung dua kali. */
    internal fun adopt(mine: AppState) = syncOn(this) {
        own = Merge.union(own, mine); dirty = true
        _state.value = Merge.combine(own, other)
        writeLocked()
    }

    /** Hasil sinkron: [sent] = isi yang berhasil dikirim, [newOther] = bagian perangkat lain kalau ada yang baru. */
    internal fun synced(sentOwn: String?, newGen: Int, newOther: AppState?, newOtherAt: Long) = syncOn(this) {
        gen = newGen; pendingReset = false
        if (sentOwn != null && sentOwn == own.toJson().toString()) dirty = false
        if (newOther != null) { other = newOther; otherAt = newOtherAt }
        val before = _state.value
        _state.value = Merge.combine(own, other)
        if (_state.value != before) onChange?.invoke()
        writeLocked()
    }

    private fun update(silent: Boolean = false, f: (AppState) -> AppState) = syncOn(this) { updateLocked(silent, f) }

    private fun updateLocked(silent: Boolean, f: (AppState) -> AppState) {
        val prev = _state.value
        var next = f(prev)
        if (silent) {
            // Impor/atur ulang: catat lencana yang sudah memenuhi syarat tanpa perayaan.
            val ok = Badges.all.filter { it.id !in next.badges && it.unlocked(next) }.map { it.id }
            if (ok.isNotEmpty()) next = next.copy(badges = next.badges + ok.associateWith { today() })
            commit(prev, next)
            scheduleSave()
            return
        }
        // Bonus target harian (sekali sehari).
        val t0 = today().toInt()
        if (next.counter(Counter.GOAL_DAY) != t0 && next.goalReached) {
            next = next.copy(counters = next.counters + (Counter.GOAL_DAY to t0)).plusXp(Xp.GOAL)
        }
        // Lencana bisa memicu lencana level, jadi diperiksa beberapa kali.
        repeat(3) {
            val fresh = Badges.all.filter { it.id !in next.badges && it.unlocked(next) }.map { it.id }
            if (fresh.isNotEmpty()) {
                next = next.copy(badges = next.badges + fresh.associateWith { today() }).plusXp(Xp.BADGE * fresh.size)
                _newBadges.value = _newBadges.value + fresh
            }
        }
        if (next.streakLit) {
            val sk = next.streak
            val t = today().toInt()
            val shown = next.counter(Counter.FLAME_SHOWN) == sk && next.counter(Counter.FLAME_DAY) == t
            if (FlameTiers.isMilestone(sk) && !shown) {
                next = next.copy(counters = next.counters + (Counter.FLAME_SHOWN to sk) + (Counter.FLAME_DAY to t)).plusXp(Xp.FLAME_TIER)
                _flameCelebration.value = sk
            }
        }
        val gained = next.xp - prev.xp
        if (gained > 0) _xpPop.value = XpPop(gained, ++xpSeq)
        val lvBefore = prev.levelInfo.level
        val lvAfter = next.levelInfo.level
        if (lvAfter > lvBefore) _levelUp.value = lvAfter
        commit(prev, next)
        scheduleSave()
    }

    /** Menyimpan penanda kecil tanpa memeriksa lencana atau XP. */
    fun mark(key: String, value: Int) = syncOn(this) { markLocked(key, value) }

    private fun markLocked(key: String, value: Int) {
        val prev = _state.value
        commit(prev, prev.copy(counters = prev.counters + (key to value)))
        scheduleSave()
    }

    private fun scheduleSave() {
        saveJob?.cancel()
        saveJob = scope.launch {
            delay(150)
            writeNow()
            onChange?.invoke()
        }
    }

    private fun writeNow() = syncOn(this) { writeLocked() }

    private fun writeLocked() {
        io?.write(fileJson().toString())
    }

    private fun AppState.withDay(f: (DayLog) -> DayLog): AppState {
        val t = today()
        return copy(days = days + (t to f(days[t] ?: DayLog())))
    }

    /** Tambah XP ke total dan ke catatan hari ini. */
    private fun AppState.plusXp(n: Int): AppState {
        if (n <= 0) return this
        return copy(counters = counters + (Counter.XP to counter(Counter.XP) + n)).withDay { it.copy(xp = it.xp + n) }
    }

    fun addXp(n: Int) = update { it.plusXp(n) }

    /** Ulangan berjarak ala SM-2 yang disederhanakan. */
    fun grade(id: Int, g: Grade) = update { st ->
        val c = st.card(id)
        val t = today()
        val isNew = !c.seen
        val nc = when (g) {
            // Lupa: muncul lagi besok (bukan hari ini) supaya kata yang sama tidak berputar terus.
            Grade.LUPA -> c.copy(reps = 0, interval = 0, ease = max(1.3, c.ease - 0.2), due = t + 1, lapses = c.lapses + 1, wrong = c.wrong + 1)
            Grade.SUSAH -> {
                val iv = max(1, (max(1, c.interval) * 1.2).roundToInt())
                c.copy(reps = c.reps + 1, interval = iv, ease = max(1.3, c.ease - 0.15), due = t + iv, right = c.right + 1)
            }
            Grade.BISA -> {
                val iv = when (c.reps) { 0 -> 1; 1 -> 3; else -> max(c.interval + 1, (c.interval * c.ease).roundToInt()) }
                c.copy(reps = c.reps + 1, interval = iv, due = t + iv, right = c.right + 1)
            }
            Grade.GAMPANG -> {
                val iv = when (c.reps) { 0 -> 4; else -> max(c.interval + 2, (c.interval * c.ease * 1.3).roundToInt()) }
                c.copy(reps = c.reps + 1, interval = iv, ease = c.ease + 0.15, due = t + iv, right = c.right + 1)
            }
        }.let { if (isNew) it.copy(learnedOn = t) else it }
        st.copy(cards = st.cards + (id to nc)).withDay {
            it.copy(
                reviews = it.reviews + 1,
                newWords = it.newWords + if (isNew) 1 else 0,
                correct = it.correct + if (g != Grade.LUPA) 1 else 0,
                wrong = it.wrong + if (g == Grade.LUPA) 1 else 0,
            )
        }.plusXp(Xp.grade(g) + if (isNew) Xp.NEW_WORD else 0)
    }

    fun quizAnswer(id: Int, correct: Boolean, xp: Int = if (correct) Xp.QUIZ_RIGHT else Xp.QUIZ_WRONG) = update { st ->
        val c = st.card(id)
        val nc = if (correct) c.copy(right = c.right + 1) else c.copy(wrong = c.wrong + 1)
        st.copy(cards = st.cards + (id to nc)).withDay {
            it.copy(quiz = it.quiz + 1, correct = it.correct + if (correct) 1 else 0, wrong = it.wrong + if (correct) 0 else 1)
        }.plusXp(xp)
    }

    /** Tandai hari ini hadir (aplikasi dibuka). */
    fun markOpen() {
        if (now.days[today()]?.opened == true) return
        update { st ->
            val opened = st.withDay { it.copy(opened = true) }
            val bonus = if (opened.streakLit) minOf(opened.streak, Xp.STREAK_BONUS_MAX) else 0
            opened.plusXp(Xp.DAILY + bonus)
        }
    }

    /** Pulihkan streak yang padam kemarin. */
    fun restoreStreak(): Boolean {
        if (!now.canRestore) return false
        update { st ->
            st.copy(
                restored = st.restored + (today() - 1),
                counters = st.counters + (Counter.RESTORES to st.counter(Counter.RESTORES) + 1),
            )
        }
        return true
    }

    /** Jawaban Kuis Angka: dicatat per kategori, ikut aktivitas harian dan XP. */
    fun numAnswer(cat: NumCat, correct: Boolean, xp: Int) = update { st ->
        val c = st.counters.toMutableMap()
        c[Counter.numTotal(cat)] = st.counter(Counter.numTotal(cat)) + 1
        if (correct) {
            c[Counter.numOk(cat)] = st.counter(Counter.numOk(cat)) + 1
            c[Counter.NUM_OK] = st.counter(Counter.NUM_OK) + 1
        }
        st.copy(counters = c).withDay {
            it.copy(quiz = it.quiz + 1, correct = it.correct + if (correct) 1 else 0, wrong = it.wrong + if (correct) 0 else 1)
        }.plusXp(xp)
    }

    fun addCounter(key: String, delta: Int = 1) = update { st -> st.copy(counters = st.counters + (key to st.counter(key) + delta)) }

    fun maxCounter(key: String, value: Int) {
        if (value <= now.counter(key)) return
        update { st -> st.copy(counters = st.counters + (key to max(st.counter(key), value))) }
    }

    fun toggleStar(id: Int) = update { st ->
        val c = st.card(id)
        st.copy(cards = st.cards + (id to c.copy(star = !c.star)))
    }

    fun resetWord(id: Int) = update { st ->
        val star = st.card(id).star
        st.copy(cards = st.cards + (id to Card(star = star)))
    }

    /** Atur ulang progres akun di semua perangkat (server ikut dikosongkan saat sinkron berikutnya). */
    fun resetAll() = syncOn(this) {
        own = AppState(settings = own.settings, days = mapOf(today() to DayLog(opened = true)))
        other = AppState(); otherAt = 0; pendingReset = true; dirty = true
        _state.value = Merge.combine(own, other)
        _changes.value = _changes.value + 1
        scheduleSave()
    }

    fun settings(f: (Settings) -> Settings) = update { it.copy(settings = f(it.settings)) }

    fun exportJson(): String = now.toJson().toString(2)

    /** Pulihkan dari teks cadangan. Mengembalikan false kalau formatnya tidak dikenali. */
    fun importJson(txt: String): Boolean = runCatching {
        val o = JSONObject(txt)
        require(o.has("cards") || o.has("days"))
        val st = AppState.fromJson(o)
        // Digabung (bukan menimpa) supaya progres dari perangkat lain tidak hilang dan tidak terhitung dua kali.
        update(silent = true) { Merge.union(it, st).copy(settings = st.settings) }
        true
    }.getOrDefault(false)
}
