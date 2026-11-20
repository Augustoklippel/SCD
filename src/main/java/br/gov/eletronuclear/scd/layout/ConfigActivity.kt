package br.gov.eletronuclear.scd.layout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.gov.eletronuclear.scd.BuildConfig.VERSION_NAME
import br.gov.eletronuclear.scd.R
import br.gov.eletronuclear.scd.data.AppDatabase
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import br.gov.eletronuclear.scd.data.Area
import br.gov.eletronuclear.scd.data.Bed
import br.gov.eletronuclear.scd.data.User
import br.gov.eletronuclear.scd.file.JsonToRoomImporter
import br.gov.eletronuclear.scd.network.BluetoothRfcommClient
import br.gov.eletronuclear.scd.file.SaveFile
import kotlin.collections.listOf

class ConfigActivity : AppCompatActivity() {

    private lateinit var bluetoothClient: BluetoothRfcommClient

    private lateinit var windowsMacAddresses: MutableList<String>

    private var grantedBluetooth: Boolean = false
    private val bluetoothPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                grantedBluetooth = true
            } else {
                Toast.makeText(
                    this,
                    "Permissão Bluetooth necessária",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    private val message_received = MutableLiveData<String>()
    private val status_message = MutableLiveData<String>()

    private val saveFile = SaveFile(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SCD)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val versionName: String = VERSION_NAME
        val labelVersion  = findViewById<TextView>(R.id.tv_version)
        labelVersion.setText(versionName)

        val btnUpdate = findViewById<Button>(R.id.btn_update)
        btnUpdate.setOnClickListener(){
            val intent = Intent(this, UpdateActivity::class.java)
            startActivity(intent)
        }
        val btnArea = findViewById<Button>(R.id.btn_area)
        btnArea.setOnClickListener(){
            val intent = Intent(this, SetAreaActivity::class.java)
            startActivity(intent)
        }
        val btnSync = findViewById<Button>(R.id.btn_sync)
        btnSync.setOnClickListener(){
            windowsMacAddresses = getMacAddress()
            showPopupModal()
        }
        val btnDados = findViewById<Button>(R.id.btn_data)
        btnDados.setOnClickListener(){
        //    val intent = Intent(this, AreaActivity::class.java)
        //    startActivity(intent)
            Toast.makeText(this, "Funcionou!!!", Toast.LENGTH_SHORT).show()
        }

        // Solicite a permissão BLUETOOTH_CONNECT antes de iniciar.
        solicitarPermissaoBluetooth()

        val db = AppDatabase.getDatabase(applicationContext)
        lifecycleScope.launch {
            val settingDao = db.settingDao().getSetting()
            if (settingDao == null) {
                btnArea.isEnabled = false
                btnDados.isEnabled = false
            }else{
                btnArea.isEnabled = true
                btnDados.isEnabled = true
            }
        }
        message_received.observe(this@ConfigActivity) { newMessage ->
            if (newMessage.substring(0,3) == "ACK") {
                Log.d("MensagemBL","Recebi o retorno do ACK!")
                bluetoothClient.sendMessage("STR00?")
            }else if (newMessage.substring(0,6) == "STR00=") {
                status_message.value = "Recebendo arquivo de Áreas"
                Log.d("MensagemBL","Recebendo o arquivo de Áreas!")
                saveFile.saveFileToDownloads("areas.json", newMessage.substring(6, newMessage.indexOf("#EOF")).unescapeUnicode().toByteArray())
                bluetoothClient.sendMessage("STR00!")
                bluetoothClient.sendMessage("STR01?")
            }else if (newMessage.substring(0,6) == "STR01=") {
                status_message.value = "Recebendo arquivo de Usuários"
                Log.d("MensagemBL","Recebendo o arquivo de Usuários!")
                saveFile.saveFileToDownloads("usuarios.json", newMessage.substring(6, newMessage.indexOf("#EOF")).unescapeUnicode().toByteArray())
                bluetoothClient.sendMessage("STR01!")
                bluetoothClient.sendMessage("STR02?")
            }else if (newMessage.substring(0,6) == "STR02=") {
                status_message.value = "Recebendo arquivo de Berços"
                Log.d("MensagemBL","Recebendo o arquivo de Berços!")
                saveFile.saveFileToDownloads("bercos.json", newMessage.substring(6, newMessage.indexOf("#EOF")).unescapeUnicode().toByteArray())
                bluetoothClient.sendMessage("STR02!")
                //Essa teem que ser a enviado antes do último envio!
                bluetoothClient.sendMessage("STR99?")
            }else if (newMessage.substring(0,6) == "STR99!") {
                status_message.value = "Gravando arquivos no coletor"
                bluetoothClient.disconnect()
            }
        }
    }

    private fun solicitarPermissaoBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (checkSelfPermission(
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothPermission.launch(
                    android.Manifest.permission.BLUETOOTH_CONNECT
                )
            }
        }
    }

    private fun getMacAddress() : MutableList<String> {
        val db = AppDatabase.getDatabase(applicationContext)
        var list_result: MutableList<String> = mutableListOf()
        lifecycleScope.launch {
            val bedDao = db.bedDao().getAll()
            if (bedDao.isNotEmpty()) {
                for (bed in bedDao) {
                    list_result += bed.macAddress.uppercase()
                    Log.d("MensagemBL","Gravando o Mac Address " + bed.macAddress.uppercase() + " !")
                }
            }else{
                showInputBox(this@ConfigActivity) { macAddress ->
                    list_result += macAddress.uppercase()
                    Log.d("MensagemBL","Informando o Mac Address $macAddress !")
                }
            }
        }
        return list_result
    }

    fun showInputBox(context: android.content.Context, onResult: (String) -> Unit) {
        val editText = EditText(context).apply {
            hint = "AA:BB:CC:DD:EE:FF"
        }

        AlertDialog.Builder(context)
            .setTitle("Informe o Mac Address")
            .setView(editText)
            .setPositiveButton("Confirmar") { _, _ ->
                onResult(editText.text.toString())
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    fun String.unescapeUnicode(): String {
        val regex = Regex("\\\\u([0-9a-fA-F]{4})")
        return regex.replace(this) { matchResult ->
            val hexCode = matchResult.groupValues[1]
            hexCode.toInt(16).toChar().toString()
        }
    }

    private fun connectBed() {

        val bluetoothManager =
            getSystemService(BLUETOOTH_SERVICE)
                    as android.bluetooth.BluetoothManager

        val adapter = bluetoothManager.adapter

        //val device = adapter.getRemoteDevice(macWindows)

        bluetoothClient = BluetoothRfcommClient(
            adapter,
            object : BluetoothRfcommClient.Listener {

                override fun onConnected() {
                    runOnUiThread {
                        println("Conectado ao Windows!")
                        Log.d("MensagemBL","Enviando um ACK")
                        bluetoothClient.sendMessage("ACK")
                    }
                }

                override fun onMessage(message: String) {
                    runOnUiThread {
                        println("Windows: $message")
                        message_received.value = message
                        //bluetoothClient.sendMessage("Olá, Windows!")
                    }
                }

                override fun onDisconnected() {
                    runOnUiThread {
                        println("Desconectado")
                    }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        println(error)
                    }
                }
            }
        )

        bluetoothClient.connectToFirstAvailable(
            windowsMacAddresses
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::bluetoothClient.isInitialized) {
            bluetoothClient.disconnect()
        }
    }
    private fun showPopupModal() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sincronizar Dados")
        builder.setMessage("Iniciar o processo de sincronização do coletor.")

        builder.setPositiveButton("Iniciar") { dialog, _ ->
            // Iniciar a sincronização
            execSync()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        // Cria e exibe o modal na tela
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
    //@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun execSync(){
        //val macWindows = windowsMacAddresses.get(0)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sincronização em andamento")

        val inflation = LayoutInflater.from(this)
        val viewModal = inflation.inflate(R.layout.dialog_progress, null)
        builder.setView(viewModal)

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val viewMessage = viewModal.findViewById<TextView>(R.id.viewMessage)

        val dialog = builder.create()
        dialog.show()

        connectBed()
        Log.d("MensagemBL","Solicitada a conexão")

        status_message.observe(this@ConfigActivity) { statusMessage ->
            if(statusMessage.toString() == "Gravando arquivos no coletor"){
                val importer = JsonToRoomImporter(this@ConfigActivity)
                val db = AppDatabase.getDatabase(applicationContext)
                val delFile: SaveFile = SaveFile(this@ConfigActivity)
                var total_users = 0
                var total_beds = 0
                var total_areas = 0
                lifecycleScope.launch {
                    total_users = importer.importar(
                        nomeArquivo = "usuarios.json",
                        entidade = User::class.java,
                        inserir = { lista -> db.userDao().insertAll(lista) }
                    )
                    delFile.deleteFromDownloads("usuarios.json")
                    //Toast.makeText( this@ConfigActivity, "Gravados $total_users registros na tabela usuários.", Toast.LENGTH_SHORT).show()
                    Log.d("MensagemBL","Gravados $total_users registros na tabela usuários.")
                    total_beds = importer.importar(
                        nomeArquivo = "bercos.json",
                        entidade = Bed::class.java,
                        inserir = { lista -> db.bedDao().insertAll(lista) }
                    )
                    delFile.deleteFromDownloads("bercos.json")
                    //Toast.makeText( this@ConfigActivity, "Gravados $total_beds registros na tabela Berços.", Toast.LENGTH_SHORT).show()
                    Log.d("MensagemBL","Gravados $total_beds registros na tabela Berços.")
                    total_areas = importer.importar(
                        nomeArquivo = "areas.json",
                        entidade = Area::class.java,
                        inserir = { lista -> db.areaDao().insertAll(lista) }
                    )
                    delFile.deleteFromDownloads("areas.json")
                    //Toast.makeText( this@ConfigActivity, "Gravados $total_areas registros na tabela Áreas.", Toast.LENGTH_SHORT).show()
                    Log.d("MensagemBL","Gravados $total_areas registros na tabela Áreas.")
                }
                dialog.dismiss()
                Toast.makeText(this, "Sincronização finalizada com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            viewMessage.text = statusMessage.toString()
        }
    }
}