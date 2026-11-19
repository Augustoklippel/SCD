package br.gov.eletronuclear.scd.file

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Procura um arquivo .json na pasta Downloads e grava seu conteúdo numa tabela do Room.
 *
 * Dependência (build.gradle): implementation("com.google.code.gson:gson:2.11.0")
 *
 * Uso:
 *   val importer = JsonToRoomImporter(context)
 *   val total = importer.importar(
 *       nomeArquivo = "clientes.json",
 *       entidade = Cliente::class.java,
 *       inserir = { lista -> db.clienteDao().inserirTodos(lista) }
 *   )
 *
 * O JSON pode ser uma lista ([{...},{...}]) ou um único objeto ({...}).
 * Os nomes dos campos do JSON devem bater com os campos da entidade
 * (ou use @SerializedName nos campos da entidade).
 */
class JsonToRoomImporter(
    private val context: Context,
    private val gson: Gson = Gson()
) {

    /**
     * @param nomeArquivo nome do arquivo em Downloads (ex.: "dados.json")
     * @param entidade    classe da entidade Room (ex.: Cliente::class.java)
     * @param inserir     função que grava a lista no Room (normalmente um método @Insert do DAO)
     * @return quantidade de registros gravados
     */
    suspend fun <T> importar(
        nomeArquivo: String,
        entidade: Class<T>,
        inserir: suspend (List<T>) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        val conteudo = abrirArquivo(nomeArquivo).use { it.bufferedReader().readText() }
        val registros = converter(conteudo, entidade)
        if (registros.isNotEmpty()) inserir(registros)
        registros.size
    }

    // ---------- Conversão JSON -> entidades ----------

    private fun <T> converter(json: String, entidade: Class<T>): List<T> {
        val raiz = JsonParser.parseString(json)
        return if (raiz.isJsonArray) {
            val tipo = TypeToken.getParameterized(List::class.java, entidade).type
            gson.fromJson(raiz, tipo)
        } else {
            listOf(gson.fromJson(raiz, entidade))
        }
    }

    // ---------- Localização do arquivo em Downloads ----------

    private fun abrirArquivo(nomeArquivo: String): InputStream {
        // 1) Tenta via MediaStore (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            buscarNoMediaStore(nomeArquivo)?.let { uri ->
                context.contentResolver.openInputStream(uri)?.let { return it }
            }
        }

        // 2) Tenta acesso direto ao arquivo
        //    (Android 9-: READ_EXTERNAL_STORAGE; Android 11+: MANAGE_EXTERNAL_STORAGE)
        @Suppress("DEPRECATION")
        val arquivo = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            nomeArquivo
        )
        if (arquivo.exists() && arquivo.canRead()) return arquivo.inputStream()

        throw FileNotFoundException(
            "Arquivo '$nomeArquivo' não encontrado (ou sem permissão) na pasta Downloads."
        )
    }

    private fun buscarNoMediaStore(nomeArquivo: String): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null

        val colecao = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projecao = arrayOf(MediaStore.Downloads._ID)
        val selecao = "${MediaStore.Downloads.DISPLAY_NAME} = ?"

        context.contentResolver.query(
            colecao,
            projecao,
            selecao,
            arrayOf(nomeArquivo),
            "${MediaStore.Downloads.DATE_ADDED} DESC" // pega o mais recente se houver duplicados
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                return ContentUris.withAppendedId(colecao, id)
            }
        }
        return null
    }
}