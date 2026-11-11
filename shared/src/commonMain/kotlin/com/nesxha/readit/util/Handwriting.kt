package com.nesxha.readit.util

import com.nesxha.readit.platform.Plat
import com.nesxha.readit.data.Words
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import com.nesxha.readit.platform.syncOn

/** Satu titik goresan dalam koordinat kanvas. */
data class Pt(val x: Float, val y: Float)

enum class Verdict { BENAR, HAMPIR, SALAH }

/**
 * Pengenal tulisan hanzi sederhana tanpa data urutan goresan.
 * Tulisan dan hanzi target dinormalkan ke kotak 64×64, lalu dibandingkan jarak rata-rata
 * antar kerangka garisnya (chamfer dua arah). Kalau nilainya ragu-ragu, tulisan juga
 * dibandingkan dengan 40 hanzi lain: tulisan dianggap benar bila paling mirip dengan target.
 */
object Handwriting {
    private const val N = 64
    private const val RS = 130          // ukuran bitmap saat merender hanzi
    private const val CAP = 8
    private const val SURE = 2.0f
    private const val MAYBE = 2.7f

    private val cache = HashMap<Char, Grid>()

    private class Grid(val mask: BooleanArray, val dist: IntArray, val count: Int)

    /** Nilai: rata-rata jarak dalam piksel kotak 64 (kecil = mirip). */
    fun cost(c: Char, strokes: List<List<Pt>>): Float {
        val u = userGrid(strokes) ?: return 99f
        return cost(target(c), u)
    }

    fun judge(c: Char, strokes: List<List<Pt>>): Verdict {
        val u = userGrid(strokes) ?: return Verdict.SALAH
        val cg = cost(target(c), u)
        if (cg <= SURE) return Verdict.BENAR
        if (cg > MAYBE) return if (cg <= MAYBE + 0.5f) Verdict.HAMPIR else Verdict.SALAH
        val pool = Words.all.asSequence().flatMap { it.hanzi.asSequence() }
            .filter { it in '一'..'鿿' && it != c }.distinct().toList().shuffled().take(40)
        val better = pool.count { cost(target(it), u) < cg }
        return if (better <= 1) Verdict.BENAR else Verdict.HAMPIR
    }

    // ---------------- Cari hanzi lewat tulisan ----------------

    /** Semua hanzi unik di kamus. */
    val allChars: List<Char> by lazy {
        Words.all.asSequence().flatMap { it.hanzi.asSequence() }.filter { it in '一'..'鿿' || it == '〇' }.distinct().toList()
    }

    private const val CACHE_FILE = "hw_cetakan_v2.bin"

    /**
     * Menyiapkan cetakan semua hanzi. Pertama kali dibuat dari font (beberapa detik),
     * lalu disimpan di HP supaya berikutnya langsung siap.
     */
    fun prepare(onProgress: (Float) -> Unit = {}) {
        val chars = allChars
        if (chars.all { syncOn(this) { cache.containsKey(it) } }) return
        // Cetakan tersimpan: [jumlah 4 byte][kode hanzi 2 byte + 512 byte cetakan]...
        Plat.p.readCache(CACHE_FILE)?.let { data ->
            runCatching {
                var o = 0
                fun int() = ((data[o].toInt() and 0xff) shl 24 or ((data[o + 1].toInt() and 0xff) shl 16) or ((data[o + 2].toInt() and 0xff) shl 8) or (data[o + 3].toInt() and 0xff)).also { o += 4 }
                val n = int()
                repeat(n) {
                    val c = (((data[o].toInt() and 0xff) shl 8) or (data[o + 1].toInt() and 0xff)).toChar(); o += 2
                    val mask = BooleanArray(N * N) { i -> (data[o + i / 8].toInt() shr (i % 8)) and 1 == 1 }
                    o += N * N / 8
                    syncOn(this) { cache[c] = Grid(mask, distance(mask), mask.count { it }) }
                }
            }
        }
        val missing = chars.filter { syncOn(this) { !cache.containsKey(it) } }
        missing.forEachIndexed { i, c -> target(c); if (i % 20 == 0) onProgress(i.toFloat() / missing.size) }
        if (missing.isNotEmpty()) runCatching {
            val all = syncOn(this) { cache.toMap() }
            val out = ByteArray(4 + all.size * (2 + N * N / 8))
            var o = 0
            out[0] = (all.size ushr 24).toByte(); out[1] = (all.size ushr 16).toByte(); out[2] = (all.size ushr 8).toByte(); out[3] = all.size.toByte(); o = 4
            all.forEach { (c, g) ->
                out[o] = (c.code ushr 8).toByte(); out[o + 1] = c.code.toByte(); o += 2
                for (i in 0 until N * N) if (g.mask[i]) out[o + i / 8] = (out[o + i / 8].toInt() or (1 shl (i % 8))).toByte()
                o += N * N / 8
            }
            Plat.p.writeCache(CACHE_FILE, out)
        }
        onProgress(1f)
    }

    /** Hanzi yang paling mirip dengan tulisan, urut dari yang paling cocok. Urutan goresan bebas. */
    fun rank(strokes: List<List<Pt>>, limit: Int = 10): List<Char> {
        val u = userGrid(strokes) ?: return emptyList()
        return allChars.map { it to cost(target(it), u) }.sortedBy { it.second }.take(limit).map { it.first }
    }

    private fun cost(t: Grid, u: Grid): Float {
        if (t.count == 0 || u.count == 0) return 99f
        var a = 0L; var b = 0L
        for (i in 0 until N * N) {
            if (u.mask[i]) a += min(t.dist[i], CAP)
            if (t.mask[i]) b += min(u.dist[i], CAP)
        }
        return ((a.toFloat() / u.count) + (b.toFloat() / t.count)) / 2f
    }

    private fun target(c: Char): Grid = syncOn(this) { cache[c] } ?: run {
        // Hanzi target digambar oleh platform dengan font WenKai (Android: Canvas; web: kanvas browser).
        val ink = Plat.p.glyphMask(c, RS)
        val px = IntArray(RS * RS) { if (ink[it]) 1 else 0 }
        thin(px, RS, RS)
        val xs = ArrayList<Float>(); val ys = ArrayList<Float>()
        for (y in 0 until RS) for (x in 0 until RS) if (px[y * RS + x] == 1) { xs += x.toFloat(); ys += y.toFloat() }
        gridFromPoints(xs, ys).also { g -> syncOn(this) { cache[c] = g } }
    }

    /** Goresan pengguna → titik rapat (diinterpolasi) → kotak 64. */
    private fun userGrid(strokes: List<List<Pt>>): Grid? {
        val all = strokes.flatten()
        if (all.size < 2) return null
        val minX = all.minOf { it.x }; val maxX = all.maxOf { it.x }
        val minY = all.minOf { it.y }; val maxY = all.maxOf { it.y }
        val span = max(max(maxX - minX, maxY - minY), 1f)
        val step = span / 200f
        val xs = ArrayList<Float>(); val ys = ArrayList<Float>()
        for (s in strokes) {
            if (s.size == 1) { xs += s[0].x; ys += s[0].y }
            for (i in 1 until s.size) {
                val a = s[i - 1]; val b = s[i]
                val dx = b.x - a.x; val dy = b.y - a.y
                val n = max(1, (sqrt(dx * dx + dy * dy) / step).toInt())
                for (k in 0..n) { xs += a.x + dx * k / n; ys += a.y + dy * k / n }
            }
        }
        return gridFromPoints(xs, ys)
    }

    private fun gridFromPoints(xs: List<Float>, ys: List<Float>): Grid {
        val mask = BooleanArray(N * N)
        if (xs.isNotEmpty()) {
            val x0 = xs.min(); val x1 = xs.max(); val y0 = ys.min(); val y1 = ys.max()
            val s = max(max(x1 - x0, y1 - y0), 1f)
            val m = (N - 8) / s
            val cx = (x0 + x1) / 2; val cy = (y0 + y1) / 2
            for (i in xs.indices) {
                val gx = ((xs[i] - cx) * m + N / 2f).toInt().coerceIn(0, N - 1)
                val gy = ((ys[i] - cy) * m + N / 2f).toInt().coerceIn(0, N - 1)
                mask[gy * N + gx] = true
            }
        }
        return Grid(mask, distance(mask), mask.count { it })
    }

    /** Jarak papan catur (8 arah) ke piksel tinta terdekat, lewat BFS. */
    private fun distance(mask: BooleanArray): IntArray {
        val d = IntArray(N * N) { 999 }
        val q = IntArray(N * N); var h = 0; var t = 0
        for (i in mask.indices) if (mask[i]) { d[i] = 0; q[t++] = i }
        while (h < t) {
            val i = q[h++]; val y = i / N; val x = i % N
            for (dy in -1..1) for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val yy = y + dy; val xx = x + dx
                if (yy < 0 || yy >= N || xx < 0 || xx >= N) continue
                val j = yy * N + xx
                if (d[j] > d[i] + 1) { d[j] = d[i] + 1; q[t++] = j }
            }
        }
        return d
    }

    /** Penipisan Zhang–Suen: menyisakan kerangka garis setebal 1 piksel. */
    private fun thin(img: IntArray, w: Int, h: Int) {
        fun at(x: Int, y: Int) = if (x < 0 || y < 0 || x >= w || y >= h) 0 else img[y * w + x]
        var changed = true
        val del = ArrayList<Int>()
        while (changed) {
            changed = false
            for (step in 0..1) {
                del.clear()
                for (y in 0 until h) for (x in 0 until w) {
                    if (img[y * w + x] != 1) continue
                    val p2 = at(x, y - 1); val p3 = at(x + 1, y - 1); val p4 = at(x + 1, y); val p5 = at(x + 1, y + 1)
                    val p6 = at(x, y + 1); val p7 = at(x - 1, y + 1); val p8 = at(x - 1, y); val p9 = at(x - 1, y - 1)
                    val b = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9
                    if (b < 2 || b > 6) continue
                    val seq = intArrayOf(p2, p3, p4, p5, p6, p7, p8, p9, p2)
                    var a = 0
                    for (k in 0 until 8) if (seq[k] == 0 && seq[k + 1] == 1) a++
                    if (a != 1) continue
                    val ok = if (step == 0) (p2 * p4 * p6 == 0 && p4 * p6 * p8 == 0) else (p2 * p4 * p8 == 0 && p2 * p6 * p8 == 0)
                    if (ok) del += y * w + x
                }
                if (del.isNotEmpty()) { changed = true; del.forEach { img[it] = 0 } }
            }
        }
    }
}
