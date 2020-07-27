package br.com.alura.technews.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import br.com.alura.technews.asynctask.BaseAsyncTask
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient

class NoticiaRepository(
    private val dao: NoticiaDAO,
    private val webclient: NoticiaWebClient = NoticiaWebClient()
) {
    private val noticiasCache = MutableLiveData<Resource<List<Noticia>>>()

    fun buscaTodos(): LiveData<Resource<List<Noticia>>> {

        val atualizaListaNoticias: (List<Noticia>) -> Unit = {
            noticiasCache.value = Resource(dado = it)
        }
        buscaInterno(quandoSucesso = atualizaListaNoticias)

        buscaNaApi(quandoSucesso = atualizaListaNoticias,
            quandoFalha = { erro ->
                val resourceAtual = noticiasCache.value;
                val resourceNovo: Resource<List<Noticia>> = Resource.criarResourceFalha(
                    resourceAtual, erro
                )
                noticiasCache.value = resourceNovo
            })

        return noticiasCache
    }

    fun salva(
        noticia: Noticia
    ): LiveData<Resource<Void>> {
        val liveData = MutableLiveData<Resource<Void>>()

        salvaNaApi(noticia, quandoSucesso = {
            liveData.value = Resource(null)
        }, quandoFalha = {
            liveData.value = Resource(null, erro = it)
        })

        return liveData
    }

    fun remove(
        noticia: Noticia
    ): LiveData<Resource<Void>> {

        val liveData = MutableLiveData<Resource<Void>>()

        removeNaApi(noticia, quandoSucesso = {
            liveData.value = Resource<Void>(null)
        }, quandoFalha = {
            liveData.value = Resource<Void>(null, erro = it)
        })

        return liveData
    }

    fun edita(
        noticia: Noticia
    ): LiveData<Resource<Void>> {
        val liveData = MutableLiveData<Resource<Void>>()
        editaNaApi(
            noticia,
            quandoSucesso = {
                liveData.value = Resource(null)
            },
            quandoFalha = {
                liveData.value = Resource(null, it)
            })

        return liveData
    }


    fun buscaPorId(
        noticiaId: Long
    ): LiveData<Noticia> {
        return dao.buscaPorId(noticiaId)
    }

    private fun buscaNaApi(
        quandoSucesso: (List<Noticia>) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.buscaTodas(
            quandoSucesso = { noticiasNovas ->
                noticiasNovas?.let {
                    salvaInterno(noticiasNovas, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun buscaInterno(quandoSucesso: (List<Noticia>) -> Unit) {
        BaseAsyncTask(quandoExecuta = {
            Log.i("teste", "Buscaondo no Banco")
            dao.buscaTodos()
        }, quandoFinaliza = {
            Log.i("teste", "Finalizando no Banco")
            quandoSucesso(it)
        }).execute()
    }


    private fun salvaNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.salva(
            noticia,
            quandoSucesso = {
                it?.let { noticiaSalva ->
                    salvaInterno(noticiaSalva, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun salvaInterno(
        noticias: List<Noticia>,
        quandoSucesso: (noticiasNovas: List<Noticia>) -> Unit
    ) {
        BaseAsyncTask(
            quandoExecuta = {
                dao.salva(noticias)
                dao.buscaTodos()
            }, quandoFinaliza = quandoSucesso
        ).execute()
    }

    private fun salvaInterno(
        noticia: Noticia,
        quandoSucesso: () -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.salva(noticia)
        }, quandoFinaliza = {
            quandoSucesso()
        }).execute()

    }

    private fun removeNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.remove(
            noticia.id,
            quandoSucesso = {
                removeInterno(noticia, quandoSucesso)
            },
            quandoFalha = quandoFalha
        )
    }


    private fun removeInterno(
        noticia: Noticia,
        quandoSucesso: () -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.remove(noticia)
        }, quandoFinaliza = {
            quandoSucesso()
        }).execute()
    }

    private fun editaNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.edita(
            noticia.id, noticia,
            quandoSucesso = { noticiaEditada ->
                noticiaEditada?.let {
                    salvaInterno(noticiaEditada, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

}