package com.salva.grainchainchallenge.data.repository

import com.salva.grainchainchallenge.data.model.RouteModel
import com.salva.grainchainchallenge.data.store.RouteDao
import javax.inject.Inject

class RouteRepository @Inject constructor(val rutaDao: RouteDao)  {
    suspend fun insertRoute(route: RouteModel) = rutaDao.insertRoute(route)

    suspend fun deleteRoute(route: RouteModel) = rutaDao.deleteRoute(route)

    fun getRoutes() = rutaDao.getAllRoutes()
}