package com.nesxha.readit.platform

import com.nesxha.readit.util.Photos
import kotlinx.coroutines.await
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.Promise

// ---------- Jembatan ke JavaScript browser ----------

@JsFun("(k) => { try { return localStorage.getItem(k); } catch (e) { return null; } }")
private external fun lsGet(k: String): String?

@JsFun("(k, v) => { try { if (v == null) localStorage.removeItem(k); else localStorage.setItem(k, v); return true; } catch (e) { return false; } }")
private external fun lsSet(k: String, v: String?): Boolean

@JsFun(
    """(method, url, headers, body) => fetch(url, { method: method, headers: JSON.parse(headers), body: body == null ? undefined : body, cache: 'no-store' })
        .then(async (r) => r.status + '\n' + await r.text())"""
)
private external fun jsFetch(method: String, url: String, headers: String, body: String?): Promise<JsString>

@JsFun("() => navigator.onLine !== false")
private external fun jsOnline(): Boolean

@JsFun("() => 'speechSynthesis' in window")
private external fun jsHasTts(): Boolean

@JsFun(
    """(text, rate) => {
        if (!('speechSynthesis' in window)) return;
        speechSynthesis.cancel();
        const u = new SpeechSynthesisUtterance(text);
        u.lang = 'zh-CN';
        const vs = speechSynthesis.getVoices();
        const v = vs.find((x) => /zh[-_]CN/i.test(x.lang)) || vs.find((x) => /^zh/i.test(x.lang));
        if (v) u.voice = v;
        u.rate = rate;
        speechSynthesis.speak(u);
    }"""
)
private external fun jsSpeak(text: String, rate: Double)

@JsFun(
    """(msg) => {
        let t = document.getElementById('k-toast');
        if (!t) { t = document.createElement('div'); t.id = 'k-toast'; document.body.appendChild(t); }
        t.textContent = msg; t.classList.add('show');
        clearTimeout(globalThis.__kToast);
        globalThis.__kToast = setTimeout(() => t.classList.remove('show'), msg.length > 40 ? 3800 : 2400);
    }"""
)
private external fun jsToast(msg: String)

@JsFun("(url) => { window.open(url, '_blank', 'noopener'); }")
private external fun jsOpen(url: String)

@JsFun(
    """(text) => {
        if (navigator.share) { navigator.share({ text: text }).catch(() => {}); return true; }
        if (navigator.clipboard) { navigator.clipboard.writeText(text).catch(() => {}); }
        return false;
    }"""
)
private external fun jsShare(text: String): Boolean

@JsFun(
    """(c, size) => {
        const cv = globalThis.__kGlyph || (globalThis.__kGlyph = document.createElement('canvas'));
        cv.width = size; cv.height = size;
        const g = cv.getContext('2d', { willReadFrequently: true });
        g.clearRect(0, 0, size, size);
        g.fillStyle = '#000';
        g.font = Math.round(size * 0.8) + "px KuaileHanzi, 'PingFang SC', 'Noto Sans SC', sans-serif";
        g.textAlign = 'center'; g.textBaseline = 'middle';
        g.fillText(c, size / 2, size / 2);
        const d = g.getImageData(0, 0, size, size).data;
        const out = new Array(size * size);
        for (let i = 0, j = 3; i < out.length; i++, j += 4) out[i] = d[j] > 128 ? '1' : '0';
        return out.join('');
    }"""
)
private external fun jsGlyph(c: String, size: Int): String

@JsFun(
    """(accept) => new Promise((resolve) => {
        const inp = document.createElement('input');
        inp.type = 'file'; inp.accept = accept;
        inp.onchange = () => {
            const f = inp.files && inp.files[0];
            if (!f) { resolve(null); return; }
            f.text().then((t) => resolve(t), () => resolve('\u0000ERR'));
        };
        inp.addEventListener('cancel', () => resolve(null));
        inp.click();
    })"""
)
private external fun jsPickText(accept: String): Promise<JsString?>

@JsFun(
    """(name, mime, text) => {
        try {
            const url = URL.createObjectURL(new Blob([text], { type: mime }));
            const a = document.createElement('a');
            a.href = url; a.download = name; document.body.appendChild(a); a.click(); a.remove();
            setTimeout(() => URL.revokeObjectURL(url), 4000);
            return true;
        } catch (e) { return false; }
    }"""
)
private external fun jsSave(name: String, mime: String, text: String): Boolean

@JsFun(
    """(maxBytes) => new Promise((resolve) => {
        const inp = document.createElement('input');
        inp.type = 'file'; inp.accept = 'image/*';
        inp.onchange = async () => {
            const f = inp.files && inp.files[0];
            if (!f) { resolve(null); return; }
            try {
                const img = await createImageBitmap(f);
                const side = Math.min(img.width, img.height);
                const sx = (img.width - side) / 2, sy = (img.height - side) / 2;
                const draw = (s) => {
                    const c = document.createElement('canvas'); c.width = s; c.height = s;
                    const g = c.getContext('2d'); g.imageSmoothingQuality = 'high';
                    g.drawImage(img, sx, sy, side, side, 0, 0, s, s); return c;
                };
                const full = draw(Math.min(1080, side)), thumb = draw(160);
                let q = 0.9, data = full.toDataURL('image/jpeg', q);
                while (data.length * 0.75 > maxBytes && q > 0.4) { q -= 0.1; data = full.toDataURL('image/jpeg', q); }
                if (data.length * 0.75 > maxBytes) { resolve('ERR'); return; }
                resolve(thumb.toDataURL('image/jpeg', 0.8).split(',')[1] + '|' + data.split(',')[1]);
            } catch (e) { resolve('ERR'); }
        };
        inp.addEventListener('cancel', () => resolve(null));
        inp.click();
    })"""
)
private external fun jsPickPhoto(maxBytes: Int): Promise<JsString?>

// ---------- Platform web ----------

private fun jsonString(s: String): String = buildString {
    append('"')
    for (c in s) when (c) {
        '"' -> append("\\\""); '\\' -> append("\\\\"); '\n' -> append("\\n"); '\r' -> append("\\r"); '\t' -> append("\\t")
        else -> if (c < ' ') append("\\u" + c.code.toString(16).padStart(4, '0')) else append(c)
    }
    append('"')
}

@OptIn(ExperimentalEncodingApi::class)
object WebPlatform : Platform {
    override val isWeb = true
    override val features = Features(reminder = false, widgets = false, cardScanner = false, photoPicker = true, appUpdate = false, fileBackup = true)

    override fun prefGet(file: String, key: String): String? = lsGet("kuaile.$file.$key")
    override fun prefPut(file: String, key: String, value: String?) { lsSet("kuaile.$file.$key", value) }

    override suspend fun http(method: String, url: String, headers: Map<String, String>, body: String?): HttpResult {
        val h = headers.entries.joinToString(",", "{", "}") { (k, v) -> jsonString(k) + ":" + jsonString(v) }
        val raw = jsFetch(method, url, h, body).await<JsString>().toString()
        val nl = raw.indexOf('\n')
        return HttpResult(raw.substring(0, nl).toInt(), raw.substring(nl + 1))
    }

    override fun hasInternet(): Boolean = jsOnline()

    override fun speak(text: String, rate: Float) = jsSpeak(text, rate.toDouble())
    override val ttsAvailable: Boolean get() = jsHasTts()

    override fun toast(message: String) = jsToast(message)
    override fun openUrl(url: String) = jsOpen(url)
    override fun shareText(text: String) { if (!jsShare(text)) toast("Teks disalin.") }

    // Cache kecil: di memori, ditambah localStorage untuk yang ukurannya wajar.
    private val mem = HashMap<String, ByteArray>()
    override fun readCache(name: String): ByteArray? =
        mem[name] ?: lsGet("kuaile.cache.$name")?.let { runCatching { Base64.decode(it) }.getOrNull() }?.also { mem[name] = it }

    override fun writeCache(name: String, data: ByteArray) {
        mem[name] = data
        if (data.size <= 400_000) {
            if (name.startsWith("photo_")) {
                // Hapus versi lama foto anggota yang sama.
                val parts = name.split('_')
                if (parts.size == 4) for (v in 0 until (parts[2].toIntOrNull() ?: 0)) lsSet("kuaile.cache.photo_${parts[1]}_${v}_${parts[3]}", null)
            }
            lsSet("kuaile.cache.$name", Base64.encode(data))
        }
    }

    override fun glyphMask(c: Char, size: Int): BooleanArray {
        val s = jsGlyph(c.toString(), size)
        return BooleanArray(size * size) { s[it] == '1' }
    }

    override suspend fun pickTextFile(mime: String): String? {
        val accept = if (mime == "text/csv") ".csv,text/csv,text/plain" else ".json,application/json,text/plain"
        val t = jsPickText(accept).await<JsString?>()?.toString() ?: return null
        if (t == "\u0000ERR") throw IllegalStateException("File tidak bisa dibaca.")
        return t
    }

    override suspend fun saveTextFile(name: String, mime: String, text: String): Boolean = jsSave(name, mime, text)

    override suspend fun pickProfilePhoto(): Photos.Prepared? {
        val r = jsPickPhoto(Photos.FULL_MAX_BYTES).await<JsString?>()?.toString() ?: return null
        if (r == "ERR") throw IllegalStateException("Foto tidak bisa dipakai.")
        val (thumb, full) = r.split('|')
        return Photos.Prepared(thumb, full)
    }
}

/** Di web tidak ada pemindai kartu, pengingat, widget, atau update APK. */
object WebUi : PlatformUi
