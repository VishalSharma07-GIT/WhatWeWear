package com.vishalsharma.whatwewear.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vishalsharma.whatwewear.data.remote.model.ClothingDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WardrobeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun addClothing(
        clothing: ClothingDto
    ) {
        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in")

        firestore
            .collection("users")
            .document(userId)
            .collection("clothing")
            .document(clothing.id)
            .set(clothing)
            .await()
    }

    suspend fun updateClothing(
        clothing: ClothingDto
    ) {
        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in")

        firestore
            .collection("users")
            .document(userId)
            .collection("clothing")
            .document(clothing.id)
            .set(clothing)
            .await()
    }

    suspend fun getClothing(): List<ClothingDto> {

        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in")

        return firestore
            .collection("users")
            .document(userId)
            .collection("clothing")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(ClothingDto::class.java)

            }
    }

    suspend fun deleteClothing(clothingId: String){
        val userId= auth.currentUser?.uid ?: throw IllegalStateException("User is not logged in")

        firestore
            .collection("users")
            .document(userId)
            .collection("clothing")
            .document(clothingId)
            .delete()
            .await()
    }
    }
