package com.vishalsharma.whatwewear.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vishalsharma.whatwewear.data.remote.model.ClothingDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WardrobeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun collection() = firestore
        .collection("users")
        .document(auth.currentUser?.uid ?: throw IllegalStateException("User is not logged in"))
        .collection("clothing")

    fun newId(): String = collection().document().id

    // NEW: live updates
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeClothing(): Flow<List<ClothingDto>> =
        authUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                callbackFlow {
                    val listener = firestore
                        .collection("users")
                        .document(uid)
                        .collection("clothing")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) { close(error); return@addSnapshotListener }
                            trySend(snapshot?.toObjects(ClothingDto::class.java).orEmpty())
                        }
                    awaitClose { listener.remove() }
                }
            }
        }

    private fun authUserIdFlow(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    // NEW: one function for add + edit
    suspend fun saveClothing(clothing: ClothingDto) {
        val toSave = if (clothing.createdAt == 0L)
            clothing.copy(createdAt = System.currentTimeMillis()) else clothing
        collection().document(clothing.id).set(toSave).await()
    }

    // OLD names kept so nothing breaks (we remove these later)
    suspend fun addClothing(clothing: ClothingDto) = saveClothing(clothing)

    suspend fun updateClothing(clothing: ClothingDto) = saveClothing(clothing)

    suspend fun getClothing(): List<ClothingDto> =
        collection().get().await()
            .toObjects(ClothingDto::class.java)
            .sortedByDescending { it.createdAt }

    suspend fun deleteClothing(id: String) {
        collection().document(id).delete().await()
    }
}