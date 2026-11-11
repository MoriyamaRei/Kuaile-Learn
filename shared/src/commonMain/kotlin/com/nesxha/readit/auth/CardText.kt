package com.nesxha.readit.auth

/** Mencari nomor KMC di teks hasil pindai QR/barcode atau hasil baca foto kartu. */
object CardText {
    private val LABELED = Regex("K\\s*M\\s*C\\s*[-–—:.\\s]*([0-9OoIlSB]{5})\\s*[-–—.\\s]*([0-9OoIlSB]{2})(?![0-9])")
    private val BARE = Regex("(?<![0-9])([0-9]{5})\\s*[-–—.]\\s*([0-9]{2})(?![0-9])")
    private val DIGITS7 = Regex("^\\s*([0-9]{7})\\s*$")

    /** Huruf yang sering tertukar dengan angka saat foto dibaca. */
    private fun fix(s: String) = s.map {
        when (it) { 'O', 'o' -> '0'; 'I', 'l' -> '1'; 'S' -> '5'; 'B' -> '8'; else -> it }
    }.joinToString("")

    fun extract(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val t = text.uppercase().replace('L', 'l').replace("KMc", "KMC")
        LABELED.find(text.uppercase())?.let { m -> return Kmc.normalize(fix(m.groupValues[1]) + fix(m.groupValues[2])) }
        LABELED.find(t)?.let { m -> return Kmc.normalize(fix(m.groupValues[1]) + fix(m.groupValues[2])) }
        BARE.find(text)?.let { m -> return Kmc.normalize(m.groupValues[1] + m.groupValues[2]) }
        DIGITS7.find(text)?.let { m -> return Kmc.normalize(m.groupValues[1]) }
        return null
    }
}
