package br.gov.eletronuclear.scd.layout

import br.gov.eletronuclear.scd.R
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class ScannerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CODIGO = "extra_codigo"
    }

    private lateinit var previewView: PreviewView
    private lateinit var moldura: View
    private lateinit var scanner: BarcodeScanner
    private lateinit var cameraController: LifecycleCameraController
    private var isRead = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SCD)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        previewView = findViewById(R.id.previewView)
        moldura = findViewById(R.id.moldura)
        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }

        startCamera()
    }

    private fun startCamera() {
        scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_CODE_128)
                .build()
        )

        val mainExecutor = ContextCompat.getMainExecutor(this)

        cameraController = LifecycleCameraController(this).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                mainExecutor,
                MlKitAnalyzer(
                    listOf(scanner),
                    // As posições dos códigos chegam em coordenadas da PreviewView
                    ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                    mainExecutor
                ) { result ->
                    val codigos = result.getValue(scanner) ?: return@MlKitAnalyzer
                    val areaMoldura = Rect(moldura.left, moldura.top, moldura.right, moldura.bottom)

                    val dentro = codigos.firstOrNull { code ->
                        val caixa = code.boundingBox
                        caixa != null && areaMoldura.contains(caixa)
                    }

                    dentro?.rawValue?.let { getResult(it) }
                }
            )
            bindToLifecycle(this@ScannerActivity)
        }

        previewView.controller = cameraController
    }

    private fun getResult(valor: String) {
        if (isRead || isFinishing) return
        isRead = true
        cameraController.unbind()
        setResult(RESULT_OK, Intent().putExtra(EXTRA_CODIGO, valor))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.close()
    }
}
