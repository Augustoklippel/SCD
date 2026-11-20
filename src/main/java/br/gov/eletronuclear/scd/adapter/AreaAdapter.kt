package br.gov.eletronuclear.scd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.gov.eletronuclear.scd.R

data class Item( val area_id: String, val area_name: String)

class AreaAdapter (
    private val onItemClick: (Item) -> Unit // Callback para o evento de clique
) : RecyclerView.Adapter<AreaAdapter.ItemViewHolder>() {

    private var listaOriginal: List<Item> = emptyList()
    private var listaFiltrada: List<Item> = emptyList()
    private var termoBuscaAtual: String? = null

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCodigo: TextView = view.findViewById(R.id.txtCodigo)
        val txtDescricao: TextView = view.findViewById(R.id.txtDescricao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = listaFiltrada[position]
        holder.txtCodigo.text = item.area_id
        holder.txtDescricao.text = item.area_name

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = listaFiltrada.size

    fun atualizarDados(novaLista: List<Item>) {
        listaOriginal = novaLista
        filtrar(termoBuscaAtual)
    }

    fun filtrar(texto: String?) {
        listaFiltrada = if (texto.isNullOrBlank()) {
            listaOriginal
        } else {
            listaOriginal.filter { item ->
                item.area_id.contains(texto, ignoreCase = true) ||
                        item.area_name.contains(texto, ignoreCase = true)
            }
        }
        notifyDataSetChanged() // Atualiza a tela com os novos resultados
    }
}