package br.gov.eletronuclear.scd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.gov.eletronuclear.scd.BuildConfig.VERSION_NAME


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        setTheme(R.style.Theme_SCD);
        super.onCreate(savedInstanceState)
        //installSplashScreen()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val versionName: String = VERSION_NAME
        val labelVersion  = findViewById<TextView>(R.id.tv_version)
        labelVersion.setText(versionName)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val btnConfig = findViewById<Button>(R.id.btn_config)
        btnConfig.setOnClickListener(){
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }
    }
}