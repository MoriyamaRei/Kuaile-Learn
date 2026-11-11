package com.nesxha.readit

import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.nesxha.readit.json.JSONArray
import com.nesxha.readit.json.JSONObject
import com.nesxha.readit.platform.Sha256
import com.nesxha.readit.platform.fixed
import com.nesxha.readit.platform.thousands
import com.nesxha.readit.platform.roundHalfUp
import com.nesxha.readit.util.Qr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.security.MessageDigest

/** Tes untuk pengganti pustaka Java di kode bersama (SHA-256, JSON, QR, format angka). */
class PlatformCommonTest {
    private fun javaSha(s: String) = MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString("") { "%02x".format(it) }

    @Test fun sha256SamaDenganJava() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", Sha256.hex("abc"))
        for (s in listOf("", "a", "KMC-00001-00:123456", "x".repeat(55), "y".repeat(56), "z".repeat(64), "汉字".repeat(40))) {
            assertEquals(javaSha(s), Sha256.hex(s))
        }
    }

    @Test fun jsonBolakBalik() {
        val o = JSONObject("""{"a":1,"b":"x\"y\n","c":[1,2.5,true,null],"d":{"e":-3}}""")
        assertEquals(1, o.getInt("a"))
        assertEquals("x\"y\n", o.getString("b"))
        assertEquals(2.5, o.getJSONArray("c").getDouble(1), 0.0)
        assertEquals(-3, o.getJSONObject("d").getInt("e"))
        val again = JSONObject(o.toString())
        assertEquals(o.toString(), again.toString())
        assertEquals(3, JSONArray("[1,\"汉\",{}]").length())
    }

    @Test fun angka() {
        assertEquals("1.1", fixed(1.06, 1))
        assertEquals("0.0", fixed(-0.04, 1))
        assertEquals("-3.50", fixed(-3.5, 2))
        assertEquals("12.345", thousands(12345))
        assertEquals("300", thousands(300))
        assertEquals(3, roundHalfUp(2.5))
        assertEquals(-2, roundHalfUp(-2.5))
    }

    private fun decode(m: Array<BooleanArray>): String {
        val scale = 8; val border = 4
        val n = (m.size + border * 2) * scale
        val px = IntArray(n * n) { i ->
            val x = i % n / scale - border; val y = i / n / scale - border
            if (x in m.indices && y in m.indices && m[y][x]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
        val bmp = BinaryBitmap(HybridBinarizer(RGBLuminanceSource(n, n, px)))
        return QRCodeReader().decode(bmp, mapOf(DecodeHintType.PURE_BARCODE to true)).text
    }

    @Test fun qrBisaDibaca() {
        for (t in listOf("KMC-00001-00", "KMC-12345-67", "A", "https://moriyamarei.github.io/Kuaile-Learn-Update/", "x".repeat(100))) {
            val m = Qr.encode(t)
            assertNotNull(m)
            assertEquals(t, decode(m!!))
        }
    }
}
