package com.example.quadroagil.data.repository

import com.example.quadroagil.data.model.Usuario
import com.example.quadroagil.data.model.UsuarioSimples
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // CADASTRAR
    suspend fun cadastrarUsuario(email: String, senha: String, usuario: Usuario): Result<Usuario> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
            val uid =
                authResult.user?.uid ?: return Result.failure(Exception("Erro ao criar usuário"))

            val usuarioComId = usuario.copy(id = uid)

            db.collection("usuarios").document(uid).set(usuarioComId).await()

            Result.success(usuarioComId)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LOGIN
    suspend fun login(email: String, senha: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, senha).await()
            Result.success("Login realizado")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // USUÁRIO ATUAL
    fun usuarioAtual() = auth.currentUser

    // BUSCAR DADOS DO USUÁRIO
    suspend fun buscarPorId(id: String): Usuario? {
        return db.collection("usuarios")
            .document(id)
            .get()
            .await()
            .toObject(Usuario::class.java)
    }

    suspend fun listarUsuariosDoProjetoSimples(idProjeto: String): List<UsuarioSimples> {
        val participacoes = ParticipacaoRepository().listarUsuariosDoProjeto(idProjeto)
        return participacoes.mapNotNull { p ->
            buscarPorId(p.idUsuario)?.let { UsuarioSimples(it.id, it.nome) }
        }
    }


    suspend fun obterUsuarioLogado(): Usuario? {
        val email = auth.currentUser?.email ?: return null

        val snapshot = db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(Usuario::class.java)
            ?.copy(id = snapshot.documents.first().id)
    }
}
