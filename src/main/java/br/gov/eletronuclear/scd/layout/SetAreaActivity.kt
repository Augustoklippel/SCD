package br.gov.eletronuclear.scd.layout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.gov.eletronuclear.scd.R
import br.gov.eletronuclear.scd.adapter.AreaAdapter
import br.gov.eletronuclear.scd.adapter.Item
import br.gov.eletronuclear.scd.data.AppDatabase
import br.gov.eletronuclear.scd.data.Setting
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetAreaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SCD)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_area)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        Log.d("MensagemBL", "Carregou os componentes")
        //val dados = populateListArea()
        val adapter = AreaAdapter() { itemSelecionado ->
            updateDataArea(itemSelecionado.area_name, itemSelecionado.area_id)
            Log.d("MensagemBL", "Clicou na área " + itemSelecionado.area_name + "!")
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val db = AppDatabase.getDatabase(applicationContext)
        var list_result: List<Item> = listOf()
        lifecycleScope.launch {
            val areaDao = db.areaDao().getAll()
            if (areaDao.isNotEmpty()) {
                for(area in areaDao){
                    list_result += Item(area.idArea, area.areaName)
                    Log.d("MensagemBL", "Carregou a área " + area.areaName + "!")
                }
            }
            adapter.atualizarDados(list_result)
        }
        Log.d("MensagemBL", "Dados carregados")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filtrar(newText)
                return true
            }
        })
    }
    fun updateDataArea(areaName: String, areaId: String){
        val db = AppDatabase.getDatabase(applicationContext)
        lifecycleScope.launch {
            val settingDao = db.settingDao()
            settingDao.updateArea(areaName, areaId)
        }
        val intent = Intent(this, MainActivity::class.java)
        Log.d("MensagemBL", "A área foi atualizada para $areaName ($areaId)!")
        startActivity(intent)
        finish()
    }
}