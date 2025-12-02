package com.example.quadroagil.data.repository

import com.example.quadroagil.data.model.ResumoProjeto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class ResumoProjetoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun atualizarResumo(idProjeto: String) {
        val notas = db.collection("notas")
            .whereEqualTo("idProjeto", idProjeto)
            .get()
            .await()

        val total = notas.size()

        val concluídas = notas.documents.count { it.getString("status") == "FEITO" }
        val andamento = notas.documents.count { it.getString("status") == "FAZENDO" }
        val afazer = notas.documents.count { it.getString("status") == "AFAZER" }

        val resumo = ResumoProjeto(
            idProjeto = idProjeto,
            totalNota = total,
            concluidas = concluídas,
            emAndamento = andamento,
            naoIniciadas = afazer
        )

        val ref = db.collection("resumos").document(idProjeto)
        ref.set(resumo).await()
    }

    fun listenResumoProjeto(
        idProjeto: String,
        onChange: (ResumoProjeto) -> Unit
    ): ListenerRegistration {

        return db.collection("notas")
            .whereEqualTo("idProjeto", idProjeto)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val total = snapshot.size()
                val concluidas = snapshot.documents.count { it.getString("status") == "FEITO" }
                val andamento = snapshot.documents.count { it.getString("status") == "FAZENDO" }
                val afazer = snapshot.documents.count { it.getString("status") == "AFAZER" }

                val resumo = ResumoProjeto(
                    idProjeto = idProjeto,
                    totalNota = total,
                    concluidas = concluidas,
                    emAndamento = andamento,
                    naoIniciadas = afazer
                )

                onChange(resumo)
            }
    }
}
