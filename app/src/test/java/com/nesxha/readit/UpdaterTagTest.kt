package com.nesxha.readit

import com.nesxha.readit.util.Updater
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UpdaterTagTest {
    @Test fun tagLama() = assertEquals(34 to "1.0.34", Updater.parseTag("v1.0.34"))

    @Test fun tagBaru() = assertEquals(35 to "1.1.1", Updater.parseTag("v1.1.1.35"))

    /** Aplikasi lama hanya membaca angka setelah titik terakhir, jadi tag baru harus tetap lebih besar dari 34. */
    @Test fun aplikasiLamaTetapMelihatUpdate() {
        val oldParse = "v1.1.1.35".substringAfterLast('.').toIntOrNull()
        assertEquals(35, oldParse)
    }

    @Test fun tagRusak() = assertNull(Updater.parseTag("v1.1.x"))
}
