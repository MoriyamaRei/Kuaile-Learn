package com.nesxha.readit

import com.nesxha.readit.auth.AuthError
import com.nesxha.readit.auth.DemoAuthApi
import com.nesxha.readit.auth.KeyValue
import com.nesxha.readit.auth.Kmc
import com.nesxha.readit.auth.KmcStatus
import com.nesxha.readit.auth.Lockout
import com.nesxha.readit.auth.Nickname
import com.nesxha.readit.auth.OfflinePolicy
import com.nesxha.readit.auth.PinRules
import com.nesxha.readit.auth.Res
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthCoreTest {
    private class MapKv : KeyValue {
        val m = HashMap<String, String>()
        override fun get(key: String) = m[key]
        override fun put(key: String, value: String?) { if (value == null) m.remove(key) else m[key] = value }
    }

    @Test fun formatKmc() {
        assertEquals("KMC-00001-00", Kmc.normalize("KMC-00001-00"))
        assertEquals("KMC-00001-00", Kmc.normalize("kmc 00001 00"))
        assertEquals("KMC-12345-67", Kmc.normalize("1234567"))
        assertNull(Kmc.normalize("123456"))
        assertNull(Kmc.normalize("KMC-00001-001"))
        assertEquals("batulicin", Kmc.normalizePlace("Batu Licin."))
        assertEquals(Kmc.normalizePlace("BATULICIN"), Kmc.normalizePlace("batu-licin"))
    }

    @Test fun pinLemahDitolak() {
        for (p in listOf("000000", "123456", "654321", "890123", "121212", "123123", "112233", "12345", "12a456"))
            assertNotNull(p, PinRules.problem(p))
        assertNull(PinRules.problem("482915"))
        assertNull(PinRules.problem("730194"))
    }

    @Test fun nickname() {
        assertNull(Nickname.problem("A"))
        assertNull(Nickname.problem("Xiao Long_01"))
        assertNotNull(Nickname.problem("   "))
        assertNotNull(Nickname.problem("12345678901234567"))
        assertNotNull(Nickname.problem("halo!"))
    }

    @Test fun kunciBertingkat() {
        var s = Lockout.State()
        val expect = listOf(60_000L, 180_000L, 300_000L, 600_000L, 3_600_000L)
        for ((i, d) in expect.withIndex()) {
            s = Lockout.fail(s, 0); s = Lockout.fail(s, 0)
            assertEquals(1, Lockout.left(s))
            s = Lockout.fail(s, 0)
            assertEquals("tingkat ${i + 1}", d, s.until)
        }
        repeat(3) { s = Lockout.fail(s, 0) }
        assertTrue(s.forever)
    }

    @Test fun modeOffline() {
        val day = OfflinePolicy.DAY_MS
        val ok = OfflinePolicy.status(0, 0, day / 2)
        assertTrue(!ok.expired); assertEquals(3, ok.daysLeft)
        assertEquals(1, OfflinePolicy.status(0, 0, day * 2 + 10).daysLeft)
        assertTrue(OfflinePolicy.status(0, 0, day * 3 + 1).expired)
        // Jam HP dimundurkan: dianggap kedaluwarsa.
        assertTrue(OfflinePolicy.status(day * 5, day * 5, day * 5 - day).expired)
    }

    @Test fun alurDaftarDanMasukDemo() = runBlocking {
        var now = 1_000_000L
        val kv = MapKv()
        val api = DemoAuthApi(kv) { now }
        val k = "KMC-00001-00"
        assertEquals(Res.Ok(KmcStatus.AKTIF), api.check(k))
        assertEquals(Res.Ok(KmcStatus.TIDAK_ADA), api.check("KMC-99999-00"))

        // Data lahir salah 3× → terkunci 1 menit.
        repeat(2) { assertTrue((api.checkBirth(k, "Kotabaru", "2005-08-17") as Res.Err).error is AuthError.WrongBirth) }
        assertTrue((api.checkBirth(k, "Kotabaru", "2005-08-17") as Res.Err).error is AuthError.Locked)
        assertTrue((api.checkBirth(k, "Batulicin", "2005-08-17") as Res.Err).error is AuthError.Locked)
        now += 61_000
        assertEquals(Res.Ok(Unit), api.checkBirth(k, "batu licin", "2005-08-17"))

        assertEquals(Res.Err(AuthError.WeakPin), api.register(k, "Batulicin", "2005-08-17", "123456", "Rei"))
        assertEquals(Res.Err(AuthError.NicknameTaken), api.register(k, "Batulicin", "2005-08-17", "730194", "xiao mei"))
        assertEquals(Res.Ok(Unit), api.register(k, "Batulicin", "2005-08-17", "730194", "Rei"))
        assertEquals(Res.Err(AuthError.AlreadyRegistered), api.register(k, "Batulicin", "2005-08-17", "730194", "Rei"))

        // Satu KMC hanya di satu HP.
        assertTrue((api.login(k, "000001", "hp-a") as Res.Err).error is AuthError.WrongPin)
        val a = api.login(k, "730194", "hp-a") as Res.Ok
        assertEquals("Rei", a.value.nickname)
        assertEquals(Res.Err(AuthError.OtherDevice), api.login(k, "730194", "hp-b"))
        assertEquals(Res.Ok("Rei"), api.verify(a.value.token, "hp-a"))

        // Data tersimpan: server demo baru tetap ingat pendaftaran.
        val again = DemoAuthApi(kv) { now }
        assertEquals(Res.Ok(KmcStatus.TERDAFTAR), again.check(k))

        // Setelah logout, HP lain bisa masuk dan sesi lama tidak berlaku.
        api.logout(a.value.token, "hp-a")
        assertEquals(Res.Err(AuthError.Revoked), api.verify(a.value.token, "hp-a"))
        assertTrue(api.login(k, "730194", "hp-b") is Res.Ok)
    }

    @Test fun kmcSedangDipakaiDanDiblokir() = runBlocking {
        val api = DemoAuthApi(MapKv())
        assertEquals(Res.Err(AuthError.OtherDevice), api.login("KMC-00003-00", "482915", "hp-saya"))
        assertEquals(Res.Err(AuthError.Blocked), api.login("KMC-00004-00", "482915", "hp-saya"))
        assertEquals(Res.Err(AuthError.NotRegistered), api.login("KMC-00001-00", "482915", "hp-saya"))
    }
}

class CardTextTest {
    @Test fun bacaNomorKmc() {
        val e = com.nesxha.readit.auth.CardText::extract
        assertEquals("KMC-00042-00", e("KMC-00042-00"))
        assertEquals("KMC-00042-00", e("Kuaile Mandarin Club\nNo. KMC 00042 - 00\nNama: Budi"))
        assertEquals("KMC-00042-00", e("KMC-OOO42-OO"))
        assertEquals("KMC-12345-67", e("https://mandarinkuaile.com/kmc/KMC-12345-67"))
        assertEquals("KMC-00042-01", e("Anggota 00042-01"))
        assertEquals("KMC-00042-01", e("0004201"))
        assertNull(e("Tidak ada nomor di sini 123"))
        assertNull(e(null))
    }
}
