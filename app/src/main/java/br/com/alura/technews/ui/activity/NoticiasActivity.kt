package br.com.alura.technews.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.activity.extensions.transacaoFragment
import br.com.alura.technews.ui.fragment.ListaNoticiaFragment
import br.com.alura.technews.ui.fragment.VisualizaNoticiaFragment

private const val TAG_FRAGMENTE_VISUALIZA_NOTICIA: String = "VisualizaNoticia"

class ListaNoticiasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)

        if (savedInstanceState == null) {
            iniciarFragmentLista()
        } else {
            supportFragmentManager.findFragmentByTag(TAG_FRAGMENTE_VISUALIZA_NOTICIA)?.let {

                val bundle = it.arguments

                transacaoFragment {remove(it)}
                supportFragmentManager.popBackStack()
                trocarFragmentVisualizaNoticia(bundle)
            }
        }
    }

    private fun iniciarFragmentLista() {
        transacaoFragment {
            add(R.id.activity_noticias_conteiner_primario, ListaNoticiaFragment())
        }
    }

    private fun trocarFragmentVisualizaNoticia(noticia: Noticia) {
        val bundle = Bundle()
        bundle.putLong(NOTICIA_ID_CHAVE, noticia.id)

        trocarFragmentVisualizaNoticia(bundle)
    }

    private fun trocarFragmentVisualizaNoticia(bundle: Bundle?) {
        val visualizaNoticiaFragment: VisualizaNoticiaFragment = VisualizaNoticiaFragment()
        visualizaNoticiaFragment.arguments = bundle

        exibirFragmenteVisualizaNoticia(visualizaNoticiaFragment)
    }

    private fun exibirFragmenteVisualizaNoticia(visualizaNoticiaFragment: VisualizaNoticiaFragment) {
        transacaoFragment {
            if (!isLandScape()) {
                addToBackStack(null)
            }
            replace(getIdConteiner(), visualizaNoticiaFragment, TAG_FRAGMENTE_VISUALIZA_NOTICIA)
        }
    }

    fun getIdConteiner(): Int {
        if (isLandScape()) {
            return R.id.activity_noticias_conteiner_secundario
        } else {
            return R.id.activity_noticias_conteiner_primario
        }
    }

    private fun isLandScape() =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
