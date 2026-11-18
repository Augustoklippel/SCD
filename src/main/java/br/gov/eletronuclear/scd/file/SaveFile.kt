package br.gov.eletronuclear.scd.file

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

class SaveFile(contexto: Context) {
    private val contexto: Context = contexto

    fun saveFileToDownloads(fileName: String, fileContent: ByteArray) {
        val resolver = contexto.contentResolver
        val mimeType = "application/json"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri: Uri? = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let { fileUri ->
            resolver.openOutputStream(fileUri)?.use { outputStream ->
                outputStream.write(fileContent)
                outputStream.flush()
            }
        }
    }
}