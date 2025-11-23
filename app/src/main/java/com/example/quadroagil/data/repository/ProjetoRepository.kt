package com.example.quadroagil.data.repository

import com.example.quadroagil.data.model.Projeto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class ProjetoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // CRIAR
    suspend fun criarProjeto(projeto: Projeto): Result<Projeto> {
        return try {
            val ref = db.collection("projetos").document()
            val projetoComId = projeto.copy(id = ref.id, dataCriacao = Date())

            ref.set(projetoComId).await()

            Result.success(projetoComId)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ATUALIZAR
    suspend fun atualizarProjeto(projeto: Projeto): Result<Unit> {
        return try {
            db.collection("projetos")
                .document(projeto.id)
                .set(projeto)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETAR
    suspend fun removerProjeto(id: String): Result<Unit> {
        return try {
            db.collection("projetos").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // BUSCAR POR ID
    suspend fun buscarProjeto(id: String): Projeto? {
        return db.collection("projetos")
            .document(id)
            .get()
            .await()
            .toObject(Projeto::class.java)
    }
}
