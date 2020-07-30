package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.fragment.ListaNoticiaFragment
import br.com.alura.technews.ui.fragment.VisualizaNoticiaFragment

private const val TITULO_APPBAR = "Notícias"

class ListaNoticiasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)
        title = TITULO_APPBAR

        iniciarFragmentLista()
    }

    private fun iniciarFragmentLista() {
        val transacao = supportFragmentManager.beginTransaction()
        transacao.add(R.id.activity_noticias_conteiner, ListaNoticiaFragment())
        transacao.commit()
    }

    private fun trocarFragmentVisualizaNoticia(noticia: Noticia) {

        val transacao = supportFragmentManager.beginTransaction()
        val visualizaNoticiaFragment: VisualizaNoticiaFragment = VisualizaNoticiaFragment()
        val bundle = Bundle()
        bundle.putLong(NOTICIA_ID_CHAVE, noticia.id)
        visualizaNoticiaFragment.arguments = bundle
        transacao.replace(R.id.activity_noticias_conteiner, visualizaNoticiaFragment)
        transacao.commit()
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        if (fragment is ListaNoticiaFragment) {
            val listaNoticiaFragment = fragment as ListaNoticiaFragment

            listaNoticiaFragment.aoSelecionarNoticia = {
                abreVisualizadorNoticia(it)
            }

            listaNoticiaFragment.aoTocarFabSalva = {
                abreFormularioModoCriacao()
            }
        } else if (fragment is VisualizaNoticiaFragment) {
            val visualizaNoticiaFragment = fragment as VisualizaNoticiaFragment

            visualizaNoticiaFragment.aoEditarNoticia = {
                abreFormularioEdicao(it)
            }

            visualizaNoticiaFragment.finalizar = {
                finish()
            }
        }
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
