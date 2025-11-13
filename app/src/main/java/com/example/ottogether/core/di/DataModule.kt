package com.example.ottogether.core.di

import com.example.ottogether.core.data.FakeSubscriptionRepository
import com.example.ottogether.core.data.InMemoryUserRepository
import com.example.ottogether.core.data.SubscriptionRepository
import com.example.ottogether.core.data.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: FakeSubscriptionRepository
    ): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: InMemoryUserRepository
    ): UserRepository
}