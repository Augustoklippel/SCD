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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import br.gov.eletronuclear.scd.network.BluetoothRfcommClient

class ConfigActivity : AppCompatActivity() {

    private lateinit var bluetoothClient: BluetoothRfcommClient

    private val windowsMacAddresses = listOf(
        "AA:BB:CC:DD:EE:01",
        "00:1A:7D:DA:71:13",
        "AA:BB:CC:DD:EE:03",
        "A0:A8:CD:5E:51:DB",
        "5C:F3:70:87:BC:13"
    )

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
        //btnArea.setOnClickListener(){
        //    val intent = Intent(this, AreaActivity::class.java)
        //    startActivity(intent)
        //}
        val btnSync = findViewById<Button>(R.id.btn_sync)
        btnSync.setOnClickListener(){
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
                    }
                }

                override fun onMessage(message: String) {
                    runOnUiThread {
                        println("Windows: $message")
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
            // Iniciar a sincronizaçãoToast.makeText(this, "Iniciar Sincronização", Toast.LENGTH_SHORT).show()
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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sincronização andamento")

        val inflation = LayoutInflater.from(this)
        val viewModal = inflation.inflate(R.layout.dialog_progress, null)
        builder.setView(viewModal)

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val viewMessage = viewModal.findViewById<TextView>(R.id.viewMessage)

        val dialog = builder.create()
        dialog.show()



        lifecycleScope.launch {
            connectBed()
            delay(3000)
            viewMessage.text = "Procurando o berço...."
            delay(3000)
            viewMessage.text = "Conectando ao berço...."
            delay(3000)
            if(grantedBluetooth == false){
                Toast.makeText(this@ConfigActivity, "Permissão Bluetooth necessária", Toast.LENGTH_LONG).show()
                bluetoothClient.sendMessage("Olá, Berço 1 !")
                dialog.dismiss()
            }else{
                Toast.makeText(this@ConfigActivity, "Sincronização concluída", Toast.LENGTH_LONG).show()
                bluetoothClient.sendMessage("Olá, Berço 2 !")
                dialog.dismiss()
            }
        }
    }
}