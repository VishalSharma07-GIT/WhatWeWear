
package com.vishalsharma.whatwewear.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vishalsharma.whatwewear.data.remote.model.OutfitDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OutfitRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun outfitsCollection() =
        firestore
            .collection("users")
            .document(
                auth.currentUser?.uid
                    ?: throw IllegalStateException(
                        "User is not logged in"
                    )
            )
            .collection("outfits")

    suspend fun saveOutfit(outfit: OutfitDto) {
        val outfitWithTimestamp = outfit.copy(
            createdAt = System.currentTimeMillis()
        )

        outfitsCollection()
            .document(outfit.id)
            .set(outfitWithTimestamp)
            .await()
    }

    suspend fun getOutfits(): List<OutfitDto> {
        return outfitsCollection()
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(OutfitDto::class.java)
            }
            .sortedBy { it.plannedDate }
    }

    suspend fun deleteOutfit(outfitId: String) {
        outfitsCollection()
            .document(outfitId)
            .delete()
            .await()
    }

    suspend fun markOutfitAsWorn(
        outfitId: String,
        isWorn: Boolean
    ) {
        outfitsCollection()
            .document(outfitId)
            .update("isWorn", isWorn)
            .await()
    }

    suspend fun updateOutfit(outfit: OutfitDto){
        outfitsCollection()
            .document(outfit.id)
            .set(outfit)
            .await()
    }
}
