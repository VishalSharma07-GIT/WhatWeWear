package com.vishalsharma.whatwewear.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vishalsharma.whatwewear.data.remote.model.ClothingDto
import com.vishalsharma.whatwewear.data.remote.model.WearLogDto
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
    private fun userDocument(): DocumentReference = firestore
        .collection("users")
        .document(auth.currentUser?.uid ?: throw IllegalStateException("User is not logged in"))

    // The days the user wore something, newest first
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeWearLog(): Flow<List<WearLogDto>> =
        authUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                callbackFlow {
                    val listener = firestore
                        .collection("users")
                        .document(uid)
                        .collection("wearLog")
                        .orderBy("date", Query.Direction.DESCENDING)
                        .limit(60)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) { close(error); return@addSnapshotListener }
                            trySend(snapshot?.toObjects(WearLogDto::class.java).orEmpty())
                        }
                    awaitClose { listener.remove() }
                }
            }
        }

    // Writes today's log and adds 1 to every piece worn, all in one batch
    // Writes a day's log and adds 1 to every piece worn, all in one batch
    suspend fun markWorn(
        itemIds: List<String>,
        occasion: String,
        dateKey: String,
        wornAt: Long,
        lastWornById: Map<String, Long>
    ) {
        val user = userDocument()
        val batch = firestore.batch()
        batch.set(
            user.collection("wearLog").document(dateKey),
            WearLogDto(date = dateKey, itemIds = itemIds, occasion = occasion, wornAt = wornAt)
        )
        itemIds.forEach { id ->
            batch.update(
                user.collection("clothing").document(id),
                mapOf(
                    "wearCount" to FieldValue.increment(1),
                    "lastWornAt" to (lastWornById[id] ?: wornAt)
                )
            )
        }
        batch.commit().await()
    }

    // Removes a day's log. updates = piece id to (new wear count, new last-worn time)
    suspend fun unmarkWorn(dateKey: String, updates: Map<String, Pair<Int, Long>>) {
        val user = userDocument()
        val batch = firestore.batch()
        batch.delete(user.collection("wearLog").document(dateKey))
        updates.forEach { (id, values) ->
            batch.update(
                user.collection("clothing").document(id),
                mapOf("wearCount" to values.first, "lastWornAt" to values.second)
            )
        }
        batch.commit().await()
    }
}