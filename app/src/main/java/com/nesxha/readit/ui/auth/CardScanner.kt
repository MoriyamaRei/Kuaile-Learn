package com.nesxha.readit.ui.auth

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nesxha.readit.auth.CardText
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

private suspend fun <T> Task<T>.awaitOrNull(): T? = suspendCancellableCoroutine { c ->
    addOnSuccessListener { c.resume(it) }
    addOnFailureListener { c.resume(null) }
    addOnCanceledListener { c.resume(null) }
}

private suspend fun scanCode(ctx: Context): String? {
    val opts = GmsBarcodeScannerOptions.Builder().enableAutoZoom().build()
    val code = GmsBarcodeScanning.getClient(ctx, opts).startScan().awaitOrNull() ?: return null
    return CardText.extract(code.rawValue ?: code.displayValue)
}

private suspend fun readPhoto(ctx: Context, uri: Uri): String? {
    val img = runCatching { InputImage.fromFilePath(ctx, uri) }.getOrNull() ?: return null
    val rec = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val text = rec.process(img).awaitOrNull()
    rec.close()
    return CardText.extract(text?.text)
}

/**
 * Scan kartu KMC: pindai QR/barcode, atau foto nomor yang tercetak di kartu.
 * Mengembalikan fungsi untuk membuka pilihan scan. [onFound] menerima nomor KMC resmi.
 */
@Composable
internal fun rememberCardScanner(onFound: (String) -> Unit): () -> Unit {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var choose by remember { mutableStateOf(false) }
    val photo = remember { File(File(ctx.cacheDir, "scan").apply { mkdirs() }, "kartu.jpg") }
    val photoUri = remember { FileProvider.getUriForFile(ctx, ctx.packageName + ".files", photo) }

    fun done(kmc: String?) {
        if (kmc != null) onFound(kmc)
        else Toast.makeText(ctx, "Nomor KMC tidak terbaca. Coba lagi dengan cahaya terang, atau ketik manual.", Toast.LENGTH_LONG).show()
    }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) scope.launch { done(readPhoto(ctx, photoUri)); photo.delete() }
    }

    if (choose) AlertDialog(
        onDismissRequest = { choose = false },
        title = { Text("Scan Kartu KMC") },
        text = {
            Column {
                Option("Pindai QR / Barcode", "Arahkan kamera ke kode di kartu.") {
                    choose = false
                    scope.launch { done(runCatching { scanCode(ctx) }.getOrNull()) }
                }
                Option("Foto Nomor di Kartu", "Foto bagian kartu yang bertuliskan nomor KMC.") {
                    choose = false
                    runCatching { camera.launch(photoUri) }.onFailure {
                        Toast.makeText(ctx, "Aplikasi kamera tidak ditemukan.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { choose = false }) { Text("Batal") } },
    )
    return { choose = true }
}

@Composable
private fun Option(title: String, sub: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick).padding(vertical = 10.dp, horizontal = 4.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
