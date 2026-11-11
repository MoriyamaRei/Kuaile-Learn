package com.nesxha.readit.platform

/**
 * Penyimpanan kecil dengan API mirip SharedPreferences Android, di atas [Platform.prefGet]/[Platform.prefPut].
 * Di Android tersimpan di SharedPreferences bernama [file]; di web di localStorage dengan awalan [file].
 */
class Prefs(private val file: String) {
    fun getString(key: String, fallback: String?): String? = Plat.p.prefGet(file, key) ?: fallback
    fun getLong(key: String, fallback: Long): Long = Plat.p.prefGet(file, key)?.toLongOrNull() ?: fallback
    fun getInt(key: String, fallback: Int): Int = Plat.p.prefGet(file, key)?.toIntOrNull() ?: fallback
    fun getBoolean(key: String, fallback: Boolean): Boolean = Plat.p.prefGet(file, key)?.toBooleanStrictOrNull() ?: fallback
    fun contains(key: String): Boolean = Plat.p.prefGet(file, key) != null

    fun edit(): Editor = Editor()

    inner class Editor {
        private val changes = LinkedHashMap<String, String?>()
        fun putString(key: String, value: String?) = apply { changes[key] = value }
        fun putLong(key: String, value: Long) = apply { changes[key] = value.toString() }
        fun putInt(key: String, value: Int) = apply { changes[key] = value.toString() }
        fun putBoolean(key: String, value: Boolean) = apply { changes[key] = value.toString() }
        fun remove(key: String) = apply { changes[key] = null }
        fun apply() { changes.forEach { (k, v) -> Plat.p.prefPut(file, k, v) } }
        fun commit(): Boolean { apply(); return true }
    }
}
