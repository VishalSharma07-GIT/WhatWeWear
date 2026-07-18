package com.vishalsharma.whatwewear.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.vishalsharma.whatwewear.auth.google.GoogleAuthManager
import com.vishalsharma.whatwewear.data.auth.AuthManager
import com.vishalsharma.whatwewear.data.remote.FirebaseAuthProvider
import com.vishalsharma.whatwewear.data.repository.AuthRepositoryImpl
import com.vishalsharma.whatwewear.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthProvider(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthProvider {
        return FirebaseAuthProvider(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthProvider: FirebaseAuthProvider
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuthProvider)
    }
    @Provides
    @Singleton
    fun provideAuthManager(
        firebaseAuth: FirebaseAuth
    ): AuthManager {
        return AuthManager(firebaseAuth)
    }

}