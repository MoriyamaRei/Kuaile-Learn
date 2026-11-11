package com.nesxha.readit.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

/**
 * Pengganti kecil untuk org.json (yang hanya ada di Android), dengan nama dan perilaku yang sama,
 * supaya kode bersama Android + web bisa memakai JSON tanpa ditulis ulang.
 */
class JSONException(message: String) : Exception(message)

private object NullValue { override fun toString() = "null" }

private fun Any?.isNullish() = this == null || this === NullValue

private fun toNumber(v: Any?): Number? = when (v) {
    is Number -> v
    is String -> v.toLongOrNull() ?: v.toDoubleOrNull()
    else -> null
}

private fun fromElement(e: JsonElement): Any? = when (e) {
    is JsonNull -> NullValue
    is JsonObject -> JSONObject(LinkedHashMap<String, Any?>().also { m -> e.forEach { (k, v) -> m[k] = fromElement(v) } })
    is JsonArray -> JSONArray(e.map { fromElement(it) }.toMutableList())
    is JsonPrimitive -> when {
        e.isString -> e.content
        e.booleanOrNull != null -> e.booleanOrNull
        e.longOrNull != null -> e.longOrNull
        else -> e.doubleOrNull ?: e.content
    }
}

private fun toElement(v: Any?): JsonElement = when (v) {
    null, NullValue -> JsonNull
    is JSONObject -> JsonObject(v.map.mapValues { toElement(it.value) })
    is JSONArray -> JsonArray(v.list.map { toElement(it) })
    is String -> JsonPrimitive(v)
    is Boolean -> JsonPrimitive(v)
    is Int -> JsonPrimitive(v)
    is Long -> JsonPrimitive(v)
    is Double -> if (v % 1.0 == 0.0 && kotlin.math.abs(v) < 1e15) JsonPrimitive(v.toLong()) else JsonPrimitive(v)
    is Float -> JsonPrimitive(v.toDouble())
    is Number -> JsonPrimitive(v.toDouble())
    is Collection<*> -> JsonArray(v.map { toElement(it) })
    else -> JsonPrimitive(v.toString())
}

private fun parseElement(text: String): JsonElement =
    runCatching { Json.parseToJsonElement(text) }.getOrElse { throw JSONException(it.message ?: "JSON tidak valid") }

private fun parseObjectMap(text: String): LinkedHashMap<String, Any?> {
    val e = parseElement(text) as? JsonObject ?: throw JSONException("Bukan objek JSON")
    return LinkedHashMap<String, Any?>().also { m -> e.forEach { (k, v) -> m[k] = fromElement(v) } }
}

private fun parseArrayList(text: String): MutableList<Any?> {
    val e = parseElement(text) as? JsonArray ?: throw JSONException("Bukan larik JSON")
    return e.map { fromElement(it) }.toMutableList()
}

class JSONObject internal constructor(internal val map: LinkedHashMap<String, Any?>) {
    constructor() : this(LinkedHashMap())
    constructor(text: String) : this(parseObjectMap(text))

    companion object {
        /** Nilai null eksplisit (sama seperti com.nesxha.readit.json.JSONObject.NULL). */
        val NULL: Any = NullValue
    }

    /** Sama seperti org.json: put(k, null) menghapus kunci; put(k, NULL) menyimpan null. */
    fun put(key: String, value: Any?): JSONObject { if (value == null) map.remove(key) else map[key] = value; return this }
    fun has(key: String) = map.containsKey(key)
    fun isNull(key: String) = map[key].isNullish()
    fun keys(): Iterator<String> = map.keys.toList().iterator()
    fun length() = map.size
    fun opt(key: String): Any? = map[key]?.takeUnless { it === NullValue }
    fun remove(key: String): Any? = map.remove(key)

    fun optString(key: String, fallback: String = ""): String = map[key].let { if (it.isNullish()) fallback else it.toString() }
    fun optInt(key: String, fallback: Int = 0): Int = toNumber(map[key])?.toInt() ?: fallback
    fun optLong(key: String, fallback: Long = 0L): Long = toNumber(map[key])?.toLong() ?: fallback
    fun optDouble(key: String, fallback: Double = Double.NaN): Double = toNumber(map[key])?.toDouble() ?: fallback
    fun optBoolean(key: String, fallback: Boolean = false): Boolean = when (val v = map[key]) {
        is Boolean -> v
        is String -> v.equals("true", true) || (!v.equals("false", true) && fallback)
        else -> fallback
    }
    fun optJSONObject(key: String): JSONObject? = map[key] as? JSONObject
    fun optJSONArray(key: String): JSONArray? = map[key] as? JSONArray

    private fun need(key: String): Any = map[key]?.takeUnless { it === NullValue } ?: throw JSONException("JSONObject[\"$key\"] tidak ada")
    fun getString(key: String): String = need(key).toString()
    fun getInt(key: String): Int = toNumber(need(key))?.toInt() ?: throw JSONException("JSONObject[\"$key\"] bukan angka")
    fun getLong(key: String): Long = toNumber(need(key))?.toLong() ?: throw JSONException("JSONObject[\"$key\"] bukan angka")
    fun getDouble(key: String): Double = toNumber(need(key))?.toDouble() ?: throw JSONException("JSONObject[\"$key\"] bukan angka")
    fun getBoolean(key: String): Boolean = (need(key) as? Boolean) ?: throw JSONException("JSONObject[\"$key\"] bukan boolean")
    fun getJSONObject(key: String): JSONObject = need(key) as? JSONObject ?: throw JSONException("JSONObject[\"$key\"] bukan objek")
    fun getJSONArray(key: String): JSONArray = need(key) as? JSONArray ?: throw JSONException("JSONObject[\"$key\"] bukan larik")

    override fun toString(): String = toElement(this).toString()
    /** Teks JSON rapi (seperti org.json toString(indent)). */
    fun toString(indent: Int): String = prettyJson.encodeToString(JsonElement.serializer(), toElement(this))
}

private val prettyJson = Json { prettyPrint = true }

class JSONArray internal constructor(internal val list: MutableList<Any?>) {
    constructor() : this(mutableListOf())
    constructor(text: String) : this(parseArrayList(text))

    fun length() = list.size
    fun put(value: Any?): JSONArray { list.add(value ?: NullValue); return this }
    fun opt(i: Int): Any? = list.getOrNull(i)?.takeUnless { it === NullValue }
    fun isNull(i: Int) = list.getOrNull(i).isNullish()
    private fun need(i: Int): Any = list.getOrNull(i)?.takeUnless { it === NullValue } ?: throw JSONException("JSONArray[$i] tidak ada")
    fun get(i: Int): Any = need(i)
    fun getString(i: Int): String = need(i).toString()
    fun getInt(i: Int): Int = toNumber(need(i))?.toInt() ?: throw JSONException("JSONArray[$i] bukan angka")
    fun getLong(i: Int): Long = toNumber(need(i))?.toLong() ?: throw JSONException("JSONArray[$i] bukan angka")
    fun getDouble(i: Int): Double = toNumber(need(i))?.toDouble() ?: throw JSONException("JSONArray[$i] bukan angka")
    fun getJSONObject(i: Int): JSONObject = need(i) as? JSONObject ?: throw JSONException("JSONArray[$i] bukan objek")
    fun getJSONArray(i: Int): JSONArray = need(i) as? JSONArray ?: throw JSONException("JSONArray[$i] bukan larik")
    fun optJSONObject(i: Int): JSONObject? = list.getOrNull(i) as? JSONObject
    fun optString(i: Int, fallback: String = ""): String = list.getOrNull(i).let { if (it.isNullish()) fallback else it.toString() }
    fun optInt(i: Int, fallback: Int = 0): Int = toNumber(list.getOrNull(i))?.toInt() ?: fallback

    override fun toString(): String = toElement(this).toString()
}
