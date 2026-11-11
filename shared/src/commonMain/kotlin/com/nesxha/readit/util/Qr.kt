package com.nesxha.readit.util

import kotlin.math.abs

/**
 * Pembuat kode QR sederhana (mode byte, koreksi galat M, versi 1–6), murni Kotlin
 * supaya jalan di Android dan web. Cukup untuk teks pendek seperti nomor KMC.
 * Mengikuti spesifikasi ISO/IEC 18004 (alur seperti pustaka QR Nayuki).
 */
object Qr {
    // Koreksi galat level M untuk versi 1..6.
    private val ECC_PER_BLOCK = intArrayOf(0, 10, 16, 26, 18, 24, 16)
    private val NUM_BLOCKS = intArrayOf(0, 1, 1, 1, 2, 2, 4)
    private const val MAX_VERSION = 6

    /** Matriks QR: [y][x] true = hitam. null kalau teks terlalu panjang. */
    fun encode(text: String): Array<BooleanArray>? {
        val bytes = text.encodeToByteArray()
        var ver = 1
        while (ver <= MAX_VERSION && bytes.size > dataCodewords(ver) - 2) ver++
        if (ver > MAX_VERSION) return null
        return Builder(ver).build(dataBits(bytes, dataCodewords(ver)))
    }

    private fun rawModules(ver: Int): Int {
        var r = (16 * ver + 128) * ver + 64
        if (ver >= 2) { val n = ver / 7 + 2; r -= (25 * n - 10) * n - 55 }
        return r
    }

    private fun dataCodewords(ver: Int) = rawModules(ver) / 8 - ECC_PER_BLOCK[ver] * NUM_BLOCKS[ver]

    /** Mode byte (0100), panjang 8 bit, isi, terminator, lalu bita pengisi 0xEC/0x11. */
    private fun dataBits(bytes: ByteArray, capacity: Int): IntArray {
        val bits = ArrayList<Boolean>()
        fun put(v: Int, n: Int) { for (i in n - 1 downTo 0) bits.add((v ushr i) and 1 == 1) }
        put(0b0100, 4); put(bytes.size, 8)
        for (b in bytes) put(b.toInt() and 0xFF, 8)
        val cap = capacity * 8
        repeat(minOf(4, cap - bits.size)) { bits.add(false) }
        while (bits.size % 8 != 0) bits.add(false)
        var pad = 0xEC
        while (bits.size < cap) { put(pad, 8); pad = pad xor 0xEC xor 0x11 }
        return IntArray(capacity) { i -> (0 until 8).fold(0) { a, j -> (a shl 1) or (if (bits[i * 8 + j]) 1 else 0) } }
    }

    // ---------- Reed–Solomon di GF(256), polinomial 0x11D ----------
    private fun mul(x: Int, y: Int): Int {
        var z = 0
        for (i in 7 downTo 0) {
            z = (z shl 1) xor ((z ushr 7) * 0x11D)
            z = z xor (((y ushr i) and 1) * x)
        }
        return z
    }

    private fun divisor(degree: Int): IntArray {
        val r = IntArray(degree); r[degree - 1] = 1
        var root = 1
        repeat(degree) {
            for (j in 0 until degree) {
                r[j] = mul(r[j], root)
                if (j + 1 < degree) r[j] = r[j] xor r[j + 1]
            }
            root = mul(root, 0x02)
        }
        return r
    }

    private fun remainder(data: IntArray, div: IntArray): IntArray {
        val r = IntArray(div.size)
        for (b in data) {
            val factor = b xor r[0]
            for (i in 0 until r.size - 1) r[i] = r[i + 1]
            r[r.size - 1] = 0
            for (i in r.indices) r[i] = r[i] xor mul(div[i], factor)
        }
        return r
    }

    private class Builder(val ver: Int) {
        val size = ver * 4 + 17
        val mod = Array(size) { BooleanArray(size) }
        val fn = Array(size) { BooleanArray(size) }

        fun set(x: Int, y: Int, dark: Boolean) { mod[y][x] = dark; fn[y][x] = true }

        fun build(data: IntArray): Array<BooleanArray> {
            drawFunctions()
            drawCodewords(withEcc(data))
            var best = 0; var bestScore = Int.MAX_VALUE
            for (m in 0 until 8) {
                applyMask(m); drawFormat(m)
                val p = penalty()
                if (p < bestScore) { bestScore = p; best = m }
                applyMask(m)
            }
            applyMask(best); drawFormat(best)
            return mod
        }

        fun drawFunctions() {
            for (i in 0 until size) { set(6, i, i % 2 == 0); set(i, 6, i % 2 == 0) }
            finder(3, 3); finder(size - 4, 3); finder(3, size - 4)
            val al = alignPositions()
            val n = al.size
            for (i in 0 until n) for (j in 0 until n) {
                if ((i == 0 && j == 0) || (i == 0 && j == n - 1) || (i == n - 1 && j == 0)) continue
                for (dy in -2..2) for (dx in -2..2) set(al[i] + dx, al[j] + dy, maxOf(abs(dx), abs(dy)) != 1)
            }
            drawFormat(0)
        }

        fun finder(x: Int, y: Int) {
            for (dy in -4..4) for (dx in -4..4) {
                val xx = x + dx; val yy = y + dy
                if (xx in 0 until size && yy in 0 until size) {
                    val d = maxOf(abs(dx), abs(dy)); set(xx, yy, d != 2 && d != 4)
                }
            }
        }

        fun alignPositions(): IntArray {
            if (ver == 1) return IntArray(0)
            val n = ver / 7 + 2
            val step = (ver * 8 + n * 3 + 5) / (n * 4 - 4) * 2
            val r = IntArray(n); r[0] = 6
            var pos = size - 7
            for (i in n - 1 downTo 1) { r[i] = pos; pos -= step }
            return r
        }

        fun drawFormat(mask: Int) {
            val data = (0 shl 3) or mask // level M = 00
            var rem = data
            repeat(10) { rem = (rem shl 1) xor ((rem ushr 9) * 0x537) }
            val bits = ((data shl 10) or rem) xor 0x5412
            fun bit(i: Int) = (bits ushr i) and 1 == 1
            for (i in 0..5) set(8, i, bit(i))
            set(8, 7, bit(6)); set(8, 8, bit(7)); set(7, 8, bit(8))
            for (i in 9 until 15) set(14 - i, 8, bit(i))
            for (i in 0 until 8) set(size - 1 - i, 8, bit(i))
            for (i in 8 until 15) set(8, size - 15 + i, bit(i))
            set(8, size - 8, true)
        }

        fun withEcc(data: IntArray): IntArray {
            val blocks = NUM_BLOCKS[ver]; val ecc = ECC_PER_BLOCK[ver]
            val raw = rawModules(ver) / 8
            val shortCount = blocks - raw % blocks
            val shortLen = raw / blocks
            val div = divisor(ecc)
            val list = ArrayList<IntArray>()
            var k = 0
            for (i in 0 until blocks) {
                val len = shortLen - ecc + (if (i < shortCount) 0 else 1)
                val dat = data.copyOfRange(k, k + len); k += len
                val e = remainder(dat, div)
                // Blok pendek diberi satu tempat kosong supaya penyusunan bergantian rapi.
                list.add(if (i < shortCount) dat + intArrayOf(-1) + e else dat + e)
            }
            val out = ArrayList<Int>()
            for (i in 0 until shortLen + 1) for (b in list) { val v = b[i]; if (v >= 0) out.add(v) }
            return out.toIntArray()
        }

        fun drawCodewords(data: IntArray) {
            var i = 0
            var right = size - 1
            while (right >= 1) {
                if (right == 6) right = 5
                for (vert in 0 until size) for (j in 0..1) {
                    val x = right - j
                    val up = ((right + 1) and 2) == 0
                    val y = if (up) size - 1 - vert else vert
                    if (!fn[y][x] && i < data.size * 8) {
                        mod[y][x] = (data[i ushr 3] ushr (7 - (i and 7))) and 1 == 1
                        i++
                    }
                }
                right -= 2
            }
        }

        fun applyMask(m: Int) {
            for (y in 0 until size) for (x in 0 until size) {
                if (fn[y][x]) continue
                val inv = when (m) {
                    0 -> (x + y) % 2 == 0
                    1 -> y % 2 == 0
                    2 -> x % 3 == 0
                    3 -> (x + y) % 3 == 0
                    4 -> (x / 3 + y / 2) % 2 == 0
                    5 -> x * y % 2 + x * y % 3 == 0
                    6 -> (x * y % 2 + x * y % 3) % 2 == 0
                    else -> ((x + y) % 2 + x * y % 3) % 2 == 0
                }
                if (inv) mod[y][x] = !mod[y][x]
            }
        }

        /** Penalti sederhana (deretan sewarna, kotak 2×2, keseimbangan hitam-putih) untuk memilih mask. */
        fun penalty(): Int {
            var p = 0
            for (y in 0 until size) {
                var run = 1
                for (x in 1 until size) { if (mod[y][x] == mod[y][x - 1]) { run++; if (run == 5) p += 3 else if (run > 5) p++ } else run = 1 }
            }
            for (x in 0 until size) {
                var run = 1
                for (y in 1 until size) { if (mod[y][x] == mod[y - 1][x]) { run++; if (run == 5) p += 3 else if (run > 5) p++ } else run = 1 }
            }
            for (y in 0 until size - 1) for (x in 0 until size - 1) {
                val c = mod[y][x]
                if (c == mod[y][x + 1] && c == mod[y + 1][x] && c == mod[y + 1][x + 1]) p += 3
            }
            val dark = mod.sumOf { r -> r.count { it } }
            val total = size * size
            p += (abs(dark * 20 - total * 10) + total - 1) / total * 10
            return p
        }
    }
}
