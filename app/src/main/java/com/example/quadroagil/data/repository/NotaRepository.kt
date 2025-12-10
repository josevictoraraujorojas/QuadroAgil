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

    suspend fun alterarStatusNota(idNota: String, novoStatus: String): Boolean {
        return try {
            val ref = db.collection("notas").document(idNota)

            // Buscar a nota antes
            val notaExistente = ref.get().await().toObject(Nota::class.java)
                ?: return false

            // Atualizar o status
            ref.update("status", novoStatus).await()

            // Atualizar resumo
            resumoRepo.atualizarResumo(notaExistente.idProjeto)

            true

        } catch (e: Exception) {
            false
        }
    }


    suspend fun buscarNotaPorId(idNota: String): Nota? {
        return try {
            val doc = db.collection("notas")
                .document(idNota)
                .get()
                .await()

            doc.toObject(Nota::class.java)
        } catch (e: Exception) {
            null
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

    // Remover Responavel das notas do projeto
    suspend fun removerResponsavelDasNotasDoProjeto( idUsuario: String, idProjeto: String): Int {
        val notasRef = db.collection("notas")

        // Buscar notas daquele projeto onde o usuário é responsável
        val snapshot = notasRef
            .whereEqualTo("idProjeto", idProjeto)
            .whereEqualTo("idUsuario", idUsuario)
            .get()
            .await()

        val notas = snapshot.documents

        // Atualizar cada nota removendo o responsável
        notas.forEach { doc ->
            doc.reference.update("idUsuario", "").await()
        }

        // Retorna quantas notas foram modificadas
        return notas.size
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





