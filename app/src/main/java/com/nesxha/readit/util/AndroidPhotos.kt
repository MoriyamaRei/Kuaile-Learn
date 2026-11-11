package com.nesxha.readit.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/** Menyiapkan foto profil dari galeri: memotong kotak dan mengecilkan (penyimpanan salinan ada di [Photos] bersama). */
object AndroidPhotos {
    private const val FULL_MAX_BYTES = Photos.FULL_MAX_BYTES
    private const val FULL_SIDE = 1080
    private const val THUMB_SIDE = 160
    /** Membaca foto dari galeri → kotak → JPEG versi kecil (±10 KB) dan asli (maks. 1 MB), dalam base64. */
    suspend fun prepare(ctx: Context, uri: Uri): Photos.Prepared? = withContext(Dispatchers.Default) {
        runCatching {
            val src = decode(ctx, uri) ?: return@runCatching null
            val side = minOf(src.width, src.height)
            val sq = Bitmap.createBitmap(src, (src.width - side) / 2, (src.height - side) / 2, side, side)
            val full = Bitmap.createScaledBitmap(sq, minOf(FULL_SIDE, side), minOf(FULL_SIDE, side), true)
            val thumb = Bitmap.createScaledBitmap(sq, THUMB_SIDE, THUMB_SIDE, true)
            var q = 90
            var bytes = jpeg(full, q)
            while (bytes.size > FULL_MAX_BYTES && q > 40) { q -= 10; bytes = jpeg(full, q) }
            if (bytes.size > FULL_MAX_BYTES) return@runCatching null
            Photos.Prepared(b64(jpeg(thumb, 80)), b64(bytes))
        }.getOrNull()
    }

    private fun decode(ctx: Context, uri: Uri): Bitmap? =
        if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(ctx.contentResolver, uri)) { d, info, _ ->
                d.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val s = maxOf(info.size.width, info.size.height)
                if (s > 2400) d.setTargetSampleSize(s / 2400 + 1)
            }
        } else {
            val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, o) }
            val s = maxOf(o.outWidth, o.outHeight)
            val opts = BitmapFactory.Options().apply { inSampleSize = if (s > 2400) s / 2400 + 1 else 1 }
            ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        }

    private fun jpeg(b: Bitmap, q: Int): ByteArray = ByteArrayOutputStream().also { b.compress(Bitmap.CompressFormat.JPEG, q, it) }.toByteArray()
    private fun b64(b: ByteArray): String = Base64.encodeToString(b, Base64.NO_WRAP)
}
