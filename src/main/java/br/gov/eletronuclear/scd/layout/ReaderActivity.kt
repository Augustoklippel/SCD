package br.gov.eletronuclear.scd.layout

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.gov.eletronuclear.scd.R
import android.content.pm.PackageManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import br.gov.eletronuclear.scd.data.AppDatabase
import br.gov.eletronuclear.scd.data.Instrument
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ReaderActivity : AppCompatActivity() {

    private lateinit var edtCode: EditText
    private lateinit var tvDescription: TextView
    private lateinit var tvNumber : TextView

    private lateinit var db: AppDatabase

    // Recebe o valor lido pela ScannerActivity e coloca no EditText
    private val scannerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val code = result.data?.getStringExtra(ScannerActivity.EXTRA_CODIGO)
                if (!code.isNullOrEmpty()) {
                    Log.d("MensagemBL", "Código: '$code'")
                    getInstrumentToCode(code)
                }
            }
        }

    // Pede a permissão de câmera antes de abrir o leitor
    private val permissionCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accept ->
            if (accept) {
                openScanner()
            } else {
                Toast.makeText(this, R.string.permissao_negada, Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SCD)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reader)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getDatabase(applicationContext)

        edtCode = findViewById(R.id.edt_code)
        val btnOpenCamera = findViewById<MaterialButton>(R.id.btn_open)

        btnOpenCamera.setOnClickListener {
            checkPermissionToOpen()
        }
        val btnChangeUser = findViewById<MaterialButton>(R.id.btn_change_user)
        btnChangeUser.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val btnSeek = findViewById<ImageButton>(R.id.btn_seek)
        btnSeek.setOnClickListener {
            openScanner()
        }
        tvDescription = findViewById(R.id.tv_description)
        tvNumber = findViewById(R.id.tv_number)
        Log.d("MensagemBL", "onCreate_end")
    }
    private fun checkPermissionToOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openScanner()
        } else {
            permissionCamera.launch(Manifest.permission.CAMERA)
        }
    }

    fun getInstrumentToCode(code: String) {
        var instrument: Instrument? = null
        lifecycleScope.launch {
            instrument = db.instrumentDao().findByCode(code)
            if (instrument != null) {
                Log.d("MensagemBL", "Descrição: ${instrument?.description}")
                edtCode.setText(instrument?.code)
                tvDescription.setText(instrument?.description)
                tvNumber.setText(instrument?.number)
            }else{
                edtCode.setText("")
                edtCode.setSelection(0)
                tvDescription.setText("")
                tvNumber.setText("")
                Log.d("MensagemBL", "Código não encontrado")
                Toast.makeText(this@ReaderActivity, "Código inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openScanner() {
        scannerLauncher.launch(Intent(this, ScannerActivity::class.java))
    }

}