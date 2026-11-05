package br.gov.eletronuclear.scd.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.gov.eletronuclear.scd.BuildConfig.VERSION_NAME
import br.gov.eletronuclear.scd.R
import br.gov.eletronuclear.scd.data.AppDatabase
import kotlinx.coroutines.launch

class UpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SCD)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val versionName: String = VERSION_NAME
        val labelVersion  = findViewById<TextView>(R.id.tv_version)
        labelVersion.setText(versionName)

        val btnNumberUpdate = findViewById<Button>(R.id.btn_number_update)
        btnNumberUpdate.setOnClickListener(){
            val intent = Intent(this, ColetorActivity::class.java)
            startActivity(intent)
        }

        val btnDataUpdate = findViewById<Button>(R.id.btn_data_update)
        btnDataUpdate.setOnClickListener(){
            //    val intent = Intent(this, AreaActivity::class.java)
            //    startActivity(intent)
            Toast.makeText(this, "Funcionou!!!", Toast.LENGTH_SHORT).show()
        }

        val db = AppDatabase.getDatabase(applicationContext)
        lifecycleScope.launch {
            val settingDao = db.settingDao().getSetting()
            if (settingDao == null) {
                btnDataUpdate.isEnabled = false
            }else{
                btnDataUpdate.isEnabled = true
            }
        }
    }
}