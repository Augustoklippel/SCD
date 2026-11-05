package br.gov.eletronuclear.scd.layout

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import br.gov.eletronuclear.scd.data.User
import kotlinx.coroutines.launch

class ConfigActivity : AppCompatActivity() {
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
        val btnDados = findViewById<Button>(R.id.btn_data)
        btnDados.setOnClickListener(){
        //    val intent = Intent(this, AreaActivity::class.java)
        //    startActivity(intent)
            Toast.makeText(this, "Funcionou!!!", Toast.LENGTH_SHORT).show()
        }

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
}