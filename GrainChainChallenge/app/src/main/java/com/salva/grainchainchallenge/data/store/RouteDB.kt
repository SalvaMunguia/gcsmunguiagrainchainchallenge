package com.salva.grainchainchallenge.data.store

import androidx.room.Database
import androidx.room.RoomDatabase
import com.salva.grainchainchallenge.data.model.RouteModel

@Database(entities = [RouteModel::class], version = 1)
abstract class RouteDB : RoomDatabase()  {
    abstract fun getRouteDao(): RouteDao
}