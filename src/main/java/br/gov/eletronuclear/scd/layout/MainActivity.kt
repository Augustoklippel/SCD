package br.gov.eletronuclear.scd.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import br.gov.eletronuclear.scd.BuildConfig.VERSION_NAME
import br.gov.eletronuclear.scd.R
import br.gov.eletronuclear.scd.data.AppDatabase
import br.gov.eletronuclear.scd.data.User
import br.gov.eletronuclear.scd.layout.model.LoginViewModel
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(applicationContext)
                return LoginViewModel(database.userDao(), database.settingDao()) as T
            }
        }
    }

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

        val labelUserName  = findViewById<TextView>(R.id.tv_username)
        val labelNumColetor  = findViewById<TextView>(R.id.tv_num_coletor)


        val btnLogin = findViewById<Button>(R.id.btn_access)
        btnLogin.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val btnReader = findViewById<Button>(R.id.btn_reader)
        btnReader.setOnClickListener(){
            val intent = Intent(this, ReaderActivity::class.java)
            startActivity(intent)
        }


        val btnConfig = findViewById<Button>(R.id.btn_config)
        btnConfig.setOnClickListener(){
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }
        val db = AppDatabase.getDatabase(applicationContext)
        lifecycleScope.launch {
            val userDao = db.userDao()
            userDao.insert(
                User(
                    idUser = 0,
                    userName = "Administrador",
                    number = "000000",
                    password = "123456",
                    type = 0
                )
            )
            val settingDao = db.settingDao().getSetting()
            if (settingDao == null) {
                btnLogin.isEnabled = false
            }else{
                labelNumColetor.setText(settingDao.colNumber)
                if (settingDao.userName != null) {
                    labelUserName.setText(settingDao.userName)
                    btnLogin.visibility = Button.GONE
                    btnReader.visibility = Button.VISIBLE
                }
            }
        }
    }
}