package com.nesxha.readit

import com.nesxha.readit.data.Words
import java.io.File

/** Memuat words.json dari Compose Resources modul shared untuk tes (tes berjalan dari folder modul app). */
object TestData {
    fun load() {
        if (runCatching { Words.all.isNotEmpty() }.getOrDefault(false)) return
        Words.parse(File("../shared/src/commonMain/composeResources/files/words.json").readText())
    }
}
