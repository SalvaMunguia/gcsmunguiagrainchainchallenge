package com.salva.grainchainchallenge.data.store

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.*
import com.salva.grainchainchallenge.data.model.RouteModel

@Dao
interface RouteDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertRoute(route: RouteModel)

    @Delete
    suspend fun deleteRoute(route: RouteModel)

    @Query("SELECT * FROM ruta")
    fun getAllRoutes(): LiveData<List<RouteModel>>
}