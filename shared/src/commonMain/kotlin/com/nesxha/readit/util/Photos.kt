package com.nesxha.readit.util

import androidx.compose.ui.graphics.ImageBitmap
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Res
import com.nesxha.readit.platform.IoDispatcher
import com.nesxha.readit.platform.Plat
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/** Foto profil: pilih dari galeri, unduh dari server, dan simpan salinan supaya tidak diunduh berulang. */
@OptIn(ExperimentalEncodingApi::class, ExperimentalResourceApi::class)
object Photos {
    const val FULL_MAX_BYTES = 1_000_000
    private val lock = Mutex()

    /** Foto siap unggah: versi kecil (±10 KB) dan asli (maks. 1 MB), JPEG dalam base64. */
    data class Prepared(val thumb: String, val full: String)

    /** Pilih foto dari galeri; platform memotong jadi kotak dan mengecilkannya. */
    suspend fun pick(): Prepared? = Plat.p.pickProfilePhoto()

    private fun name(pid: String, ver: Int, full: Boolean) = "photo_${pid}_${ver}_${if (full) "f" else "t"}"

    private fun decode(bytes: ByteArray): ImageBitmap? = runCatching { bytes.decodeToImageBitmap() }.getOrNull()

    /** Mengambil foto anggota dari salinan, atau dari server kalau versinya baru. */
    suspend fun load(pid: String, ver: Int, full: Boolean): ImageBitmap? {
        if (ver <= 0) return null
        val key = name(pid, ver, full)
        withContext(IoDispatcher) { Plat.p.readCache(key) }?.let { b -> decode(b)?.let { return it } }
        return lock.withLock {
            withContext(IoDispatcher) { Plat.p.readCache(key) }?.let { b -> return@withLock decode(b) }
            val r = Member.photo(pid, full)
            val data = (r as? Res.Ok)?.value ?: return@withLock null
            runCatching {
                val bytes = Base64.Mime.decode(data)
                withContext(IoDispatcher) { Plat.p.writeCache(key, bytes) }
                decode(bytes)
            }.getOrNull()
        }
    }

    /** Menyimpan foto sendiri yang baru diunggah supaya langsung tampil tanpa diunduh lagi. */
    suspend fun remember(pid: String, ver: Int, p: Prepared) = withContext(IoDispatcher) {
        runCatching {
            Plat.p.writeCache(name(pid, ver, false), Base64.Mime.decode(p.thumb))
            Plat.p.writeCache(name(pid, ver, true), Base64.Mime.decode(p.full))
        }
    }

    fun previewOf(p: Prepared): ImageBitmap? = runCatching { decode(Base64.Mime.decode(p.full)) }.getOrNull()
}
