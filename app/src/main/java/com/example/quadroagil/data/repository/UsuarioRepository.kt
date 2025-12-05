package com.example.quadroagil.data.repository

import com.example.quadroagil.data.model.Usuario
import com.example.quadroagil.data.model.UsuarioSimples
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
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

    suspend fun buscarPorEmail(email: String): Usuario? {
        val snapshot = db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .await()

        // Se não existir, retorna null
        if (snapshot.isEmpty) return null

        return snapshot.documents[0].toObject(Usuario::class.java)
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

    suspend fun obterUsuarioAtual(): Result<Usuario> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuário não está logado")

            val document = db.collection("usuarios").document(uid).get().await()

            if (document.exists()) {
                val usuario = document.toObject(Usuario::class.java)
                // Garante que o ID e Email estejam preenchidos corretamente
                val usuarioFinal = usuario?.copy(
                    id = uid,
                    email = auth.currentUser?.email ?: ""
                )
                if (usuarioFinal != null) {
                    Result.success(usuarioFinal)
                } else {
                    Result.failure(Exception("Erro ao converter dados do usuário"))
                }
            } else {
                Result.failure(Exception("Perfil não encontrado no banco de dados"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Atualizar apenas dados cadastrais
    suspend fun atualizarDadosUsuario(nome: String, telefone: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuário não está logado")

            val updates = mapOf(
                "nome" to nome,
                "telefone" to telefone
            )

            db.collection("usuarios").document(uid).update(updates).await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Atualizar Senha
    suspend fun atualizarSenha(novaSenha: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não está logado")

            user.updatePassword(novaSenha).await()
            Result.success(Unit)

        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(Exception("Por segurança, faça logout e login novamente antes de trocar a senha."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun atualizarEmail(novoEmail: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não logado")

            // Atualiza no Auth
            user.updateEmail(novoEmail).await()

            // Atualiza no Firestore
            db.collection("usuarios").document(user.uid)
                .update("email", novoEmail)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            // Retorna o erro para o ViewModel mostrar na tela
            Result.failure(e)
        }
    }
}