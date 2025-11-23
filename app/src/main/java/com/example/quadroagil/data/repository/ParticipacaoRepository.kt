package com.example.quadroagil.data.repository

import com.example.quadroagil.data.model.Participacao
import com.example.quadroagil.data.model.Papel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ParticipacaoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // ADICIONAR
    suspend fun adicionarParticipacao(idUsuario: String, idProjeto: String, papel: Papel): Result<Participacao> {
        return try {
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
    suspend fun removerParticipacao(id: String): Result<Unit> {
        return try {
            db.collection("participacoes").document(id).delete().await()
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
}
