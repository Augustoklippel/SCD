package br.gov.eletronuclear.scd.layout

import br.gov.eletronuclear.scd.R
import android.content.Intent
import android.graphics.Rect
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.core.Camera
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import android.media.ToneGenerator

class ScannerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CODIGO = "extra_codigo"
    }

    private lateinit var previewView: PreviewView
    private lateinit var moldura: View
    private lateinit var scanner: BarcodeScanner
    private lateinit var cameraController: LifecycleCameraController
    private var isRead = false
    private var toneGenerator: ToneGenerator? = null
    private var isTorchOn = false

    private lateinit var btnTorch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SCD)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        previewView = findViewById(R.id.previewView)
        moldura = findViewById(R.id.moldura)
        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }
        btnTorch = findViewById<Button>(R.id.btn_flash)
        btnTorch.setOnClickListener { toggleTorch() }
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        startCamera()
    }
    private fun playBeep() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
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
        playBeep()
        if (isRead || isFinishing) return
        isRead = true
        cameraController.unbind()
        setResult(RESULT_OK, Intent().putExtra(EXTRA_CODIGO, valor))
        finish()
    }
    private fun toggleTorch() {
        val cam = cameraController
        val hasCam = cam.cameraInfo?.hasFlashUnit()
        if (hasCam?: false) {
            isTorchOn = !isTorchOn
            cam.cameraControl?.enableTorch(isTorchOn)
            btnTorch.text = if (isTorchOn) "Desligar Lanterna" else "Ligar Lanterna"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.close()
        toneGenerator?.release()
        toneGenerator = null
    }
}
