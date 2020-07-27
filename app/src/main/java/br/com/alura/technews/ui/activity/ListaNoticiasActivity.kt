package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.activity.extensions.mostraErro
import br.com.alura.technews.ui.recyclerview.adapter.ListaNoticiasAdapter
import br.com.alura.technews.ui.viewmodel.ListaNoticiasViewModel
import kotlinx.android.synthetic.main.activity_lista_noticias.*
import org.koin.android.viewmodel.ext.android.viewModel

private const val TITULO_APPBAR = "Notícias"
private const val MENSAGEM_FALHA_CARREGAR_NOTICIAS = "Não foi possível carregar as novas notícias"

class ListaNoticiasActivity : AppCompatActivity() {

    private val adapter by lazy {
        ListaNoticiasAdapter(context = this)
    }

    val viewModel: ListaNoticiasViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_noticias)
        title = TITULO_APPBAR
        configuraRecyclerView()
        configuraFabAdicionaNoticia()

        Log.i("ViewModel", viewModel.toString());
    }

    override fun onResume() {
        super.onResume()
        buscaNoticias()
    }

    private fun configuraFabAdicionaNoticia() {
        activity_lista_noticias_fab_salva_noticia.setOnClickListener {
            abreFormularioModoCriacao()
        }
    }

    private fun configuraRecyclerView() {
        val divisor = DividerItemDecoration(this, VERTICAL)
        activity_lista_noticias_recyclerview.addItemDecoration(divisor)
        activity_lista_noticias_recyclerview.adapter = adapter
        configuraAdapter()
    }

    private fun configuraAdapter() {
        adapter.quandoItemClicado = this::abreVisualizadorNoticia
    }

    private fun buscaNoticias() {
        viewModel.buscaTodos().observe(this, Observer {
            Log.i("teste", "Atualizando")
            it.dado?.let {
                adapter.atualiza(it)
            }
            it.erro?.let {
                mostraErro(it)
            }
        })
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(it: Noticia) {
        val intent = Intent(this, VisualizaNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, it.id)
        startActivity(intent)
    }

}
