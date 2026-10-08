package com.vishalsharma.whatwewear.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
){

    suspend fun uploadClothingImage(
        imageUri: Uri,
        clothingId: String
    ): String{

        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in")

        val imageRef= storage
            .reference
            .child("users/\$userId/clothing/\$clothingId.jpg")
        imageRef
            .putFile(imageUri)
            .await()

        return imageRef
            .downloadUrl
            .await()
            .toString()

    }
}