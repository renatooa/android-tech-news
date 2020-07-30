package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.activity.extensions.transacaoFragment
import br.com.alura.technews.ui.fragment.ListaNoticiaFragment
import br.com.alura.technews.ui.fragment.VisualizaNoticiaFragment

class ListaNoticiasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)

        if (savedInstanceState == null) {
            iniciarFragmentLista()
        }
    }

    private fun iniciarFragmentLista() {
        transacaoFragment {
            add(R.id.activity_noticias_conteiner, ListaNoticiaFragment())
        }
    }

    private fun trocarFragmentVisualizaNoticia(noticia: Noticia) {
        val visualizaNoticiaFragment: VisualizaNoticiaFragment = VisualizaNoticiaFragment()
        val bundle = Bundle()
        bundle.putLong(NOTICIA_ID_CHAVE, noticia.id)
        visualizaNoticiaFragment.arguments = bundle

        transacaoFragment {
            addToBackStack(null)
            replace(R.id.activity_noticias_conteiner, visualizaNoticiaFragment)
        }
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is ListaNoticiaFragment -> configurarListaNoticiasFragment(fragment)
            is VisualizaNoticiaFragment -> configurarVisualizaNoticiaFragment(fragment)
        }
    }

    private fun configurarVisualizaNoticiaFragment(fragment: VisualizaNoticiaFragment) {
        fragment.aoEditarNoticia = this::abreFormularioEdicao
        fragment.finalizar = this::finish
    }

    private fun configurarListaNoticiasFragment(fragment: ListaNoticiaFragment) {
        fragment.aoSelecionarNoticia = this::abreVisualizadorNoticia
        fragment.aoTocarFabSalva = this::abreFormularioModoCriacao
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreFormularioEdicao(noticia: Noticia) {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticia.id)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(noticia: Noticia) {
        trocarFragmentVisualizaNoticia(noticia)
    }


}
