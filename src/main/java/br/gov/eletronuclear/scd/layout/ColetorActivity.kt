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
import br.gov.eletronuclear.scd.data.Setting
import br.gov.eletronuclear.scd.data.User
import kotlinx.coroutines.launch

class ColetorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SCD)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coletor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val versionName: String = VERSION_NAME
        val labelVersion  = findViewById<TextView>(R.id.tv_version)
        labelVersion.setText(versionName)

        val numColetor = findViewById<TextView>(R.id.num_coletor)

        val btnColetorUpdate = findViewById<Button>(R.id.btn_coletor_update)
        btnColetorUpdate.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            updateNumberColetor(numColetor.text.toString())
            Toast.makeText(this, "O número do coletor foi atualizado!", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val rsSetting = db.settingDao().getSetting()
            if (rsSetting != null) {
                numColetor.setText(rsSetting.colNumber)
            }
        }
    }
     fun updateNumberColetor(number: String){
        val db = AppDatabase.getDatabase(applicationContext)
        lifecycleScope.launch {
            val settingDao = db.settingDao()
            if (settingDao.getSetting() == null) {
                settingDao.upsertSetting(
                    Setting(
                        colNumber = number,
                        userName = null,
                        idUser = null,
                        areaName = null,
                        idArea = null,
                        macAddress = null
                    )
                )
            }else{
                settingDao.updateColNumber(number)
            }
        }
    }

}