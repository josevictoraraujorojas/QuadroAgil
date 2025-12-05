package com.example.quadroagil.data.repository

import com.example.quadroagil.data.model.Participacao
import com.example.quadroagil.data.model.Papel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ParticipacaoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun adicionarParticipacao(
        idUsuario: String,
        idProjeto: String,
        papel: Papel
    ): Result<Participacao> {
        return try {


            val query = db.collection("participacoes")
                .whereEqualTo("idUsuario", idUsuario)
                .whereEqualTo("idProjeto", idProjeto)
                .get()
                .await()

            if (!query.isEmpty) {
                return Result.failure(Exception("Usuário já participa deste projeto"))
            }

            val ref = db.collection("participacoes").document()

            val participacao = Participacao(
                id = ref.id,
                idUsuario = idUsuario,
                idProjeto = idProjeto,
                papel = papel
            )

            ref.set(participacao).await()

            Result.success(participacao)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REMOVER
    suspend fun removerParticipacao(idUsuario: String, idProjeto: String): Result<Unit> {
        return try {

            val query = db.collection("participacoes")
                .whereEqualTo("idUsuario", idUsuario)
                .whereEqualTo("idProjeto", idProjeto)
                .get()
                .await()

            if (query.isEmpty) {
                return Result.failure(Exception("Participação não encontrada"))
            }

            val docId = query.documents.first().id

            db.collection("participacoes").document(docId).delete().await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // LISTAR PROJETOS DO USUÁRIO
    suspend fun listarProjetosDoUsuario(idUsuario: String): List<Participacao> {
        return db.collection("participacoes")
            .whereEqualTo("idUsuario", idUsuario)
            .get()
            .await()
            .toObjects(Participacao::class.java)
    }

    // LISTAR USUÁRIOS DE UM PROJETO
    suspend fun listarUsuariosDoProjeto(idProjeto: String): List<Participacao> {
        return db.collection("participacoes")
            .whereEqualTo("idProjeto", idProjeto)
            .get()
            .await()
            .toObjects(Participacao::class.java)
    }

    fun listenUsuariosDoProjeto(idProjeto: String, callback: (List<Participacao>) -> Unit) {
        db.collection("participacoes")
            .whereEqualTo("idProjeto", idProjeto)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }

                val lista = snapshot.toObjects(Participacao::class.java)
                callback(lista)
            }
    }

    suspend fun buscarParticipacao(idUsuario: String, idProjeto: String): Participacao? {
        val query = db.collection("participacoes")
            .whereEqualTo("idUsuario", idUsuario)
            .whereEqualTo("idProjeto", idProjeto)
            .get()
            .await()

        return if (!query.isEmpty) {
            query.documents.first().toObject(Participacao::class.java)
        } else {
            null
        }
    }
}
