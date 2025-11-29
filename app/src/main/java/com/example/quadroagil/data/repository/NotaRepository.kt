package com.example.quadroagil.data.repository

import com.example.quadroagil.data.model.Nota
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class NotaRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val resumoRepo: ResumoProjetoRepository = ResumoProjetoRepository()
) {

    // CADASTRAR
    suspend fun cadastrarNota(nota: Nota): Result<Nota> {
        return try {
            val ref = db.collection("notas").document()
            val notaComId = nota.copy(id = ref.id)

            ref.set(notaComId).await()

            resumoRepo.atualizarResumo(nota.idProjeto)

            Result.success(notaComId)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ATUALIZAR
    suspend fun atualizarNota(nota: Nota): Result<Unit> {
        return try {
            db.collection("notas")
                .document(nota.id)
                .set(nota)
                .await()

            resumoRepo.atualizarResumo(nota.idProjeto)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REMOVER
    suspend fun removerNota(nota: Nota): Result<Unit> {
        return try {
            db.collection("notas").document(nota.id).delete().await()

            resumoRepo.atualizarResumo(nota.idProjeto)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // BUSCAR NOTAS DE UM PROJETO
    suspend fun listarNotasPorProjeto(idProjeto: String): List<Nota> {
        return db.collection("notas")
            .whereEqualTo("idProjeto", idProjeto)
            .get()
            .await()
            .toObjects(Nota::class.java)
    }

    // BUSCAR NOTAS DE UM USUÁRIO
    suspend fun listarNotasPorUsuario(idUsuario: String): List<Nota> {
        return db.collection("notas")
            .whereEqualTo("idUsuario", idUsuario)
            .get()
            .await()
            .toObjects(Nota::class.java)
    }

    fun observarNotasDoProjeto(idProjeto: String, callback: (List<Nota>) -> Unit): ListenerRegistration {
        return db.collection("notas")
            .whereEqualTo("idProjeto", idProjeto)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val notas = snapshot.toObjects(Nota::class.java)
                callback(notas)
            }
    }
}
