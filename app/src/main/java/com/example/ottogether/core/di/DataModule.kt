package com.example.ottogether.core.di

import com.example.ottogether.core.data.FakePartyRepository
import com.example.ottogether.core.data.FakeSubscriptionRepository
import com.example.ottogether.core.data.PartyRepository
import com.example.ottogether.core.data.SubscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindPartyRepository(
        impl: FakePartyRepository
    ): PartyRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: FakeSubscriptionRepository
    ): SubscriptionRepository
}