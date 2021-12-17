package com.salva.grainchainchallenge.presentation.di

import android.content.Context
import androidx.room.Room
import com.salva.grainchainchallenge.data.store.RouteDB
import com.salva.grainchainchallenge.utils.Constans.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun provideDB(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, RouteDB::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDAO(db: RouteDB) = db.getRouteDao()
}