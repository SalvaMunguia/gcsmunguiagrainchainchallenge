package com.salva.grainchainchallenge.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salva.grainchainchallenge.data.model.RouteModel
import com.salva.grainchainchallenge.data.repository.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteViewModel  @Inject constructor(private val routeRepository: RouteRepository): ViewModel() {

    var saveSuccess :MutableLiveData<Boolean> = MutableLiveData()

    suspend fun saveRouteDB(route: RouteModel){

        routeRepository.insertRoute(route)
    }
    suspend fun  deleteRouteDB(route: RouteModel){
        routeRepository.deleteRoute(route)
    }
    var routesInDB = routeRepository.getRoutes()
}