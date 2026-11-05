package br.gov.eletronuclear.scd.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import br.gov.eletronuclear.scd.data.AppDatabase
import com.google.android.material.textfield.TextInputEditText
import androidx.lifecycle.ViewModelProvider
import br.gov.eletronuclear.scd.BuildConfig.VERSION_NAME
import br.gov.eletronuclear.scd.R
import br.gov.eletronuclear.scd.layout.model.LoginViewModel

class LoginActivity : AppCompatActivity() {
    // Configura a Factory para passar o Dao para a ViewModel
    private val viewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(applicationContext)
                return LoginViewModel(database.userDao(), database.settingDao()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SCD)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val versionName: String = VERSION_NAME
        val labelVersion  = findViewById<TextView>(R.id.tv_version)
        labelVersion.setText(versionName)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val TIUsername = findViewById<TextInputEditText>(R.id.username)
        val TIPassword = findViewById<TextInputEditText>(R.id.password)

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogin.setOnClickListener(){
            viewModel.loginUser(TIUsername.text.toString(), TIPassword.text.toString())
        }
    }
}