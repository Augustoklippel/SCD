package br.gov.eletronuclear.scd.layout

import android.Manifest
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
import android.widget.Toast
import androidx.core.content.ContextCompat

class ReaderActivity : AppCompatActivity() {

    private lateinit var edtCode: EditText

    // Recebe o valor lido pela ScannerActivity e coloca no EditText
    private val scannerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val codigo = result.data?.getStringExtra(ScannerActivity.EXTRA_CODIGO)
                if (!codigo.isNullOrEmpty()) {
                    edtCode.setText(codigo)
                    edtCode.setSelection(codigo.length)
                }
            }
        }

    // Pede a permissão de câmera antes de abrir o leitor
    private val permissaoCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedida ->
            if (concedida) {
                abrirScanner()
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
        edtCode = findViewById(R.id.edt_code)
        val btnAbrirCamera = findViewById<ImageButton>(R.id.btn_open)

        btnAbrirCamera.setOnClickListener {
            verificarPermissaoEAbrir()
        }
    }
    private fun verificarPermissaoEAbrir() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            abrirScanner()
        } else {
            permissaoCamera.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirScanner() {
        scannerLauncher.launch(Intent(this, ScannerActivity::class.java))
    }

}