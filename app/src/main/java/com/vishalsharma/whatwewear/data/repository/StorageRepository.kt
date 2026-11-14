package com.vishalsharma.whatwewear.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    private fun ref(clothingId: String): StorageReference {
        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in")
        return storage.reference.child("users/$userId/clothing/$clothingId.jpg")
    }

    suspend fun uploadClothingImage(imageUri: Uri, clothingId: String): String {
        val imageRef = ref(clothingId)
        imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }

    suspend fun deleteClothingImage(clothingId: String) {
        runCatching { ref(clothingId).delete().await() }
    }
}