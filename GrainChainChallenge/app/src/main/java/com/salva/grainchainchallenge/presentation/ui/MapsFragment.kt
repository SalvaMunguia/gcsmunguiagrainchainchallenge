package com.salva.grainchainchallenge.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.salva.grainchainchallenge.R
import com.salva.grainchainchallenge.data.model.RouteModel
import com.salva.grainchainchallenge.databinding.FragmentMapsBinding
import com.salva.grainchainchallenge.presentation.adapter.RouteAdapter
import com.salva.grainchainchallenge.presentation.customviews.NameSaveRouteDialog
import com.salva.grainchainchallenge.presentation.viewmodel.RouteViewModel
import com.salva.grainchainchallenge.utils.LocationService
import com.salva.grainchainchallenge.utils.Utils
import com.salva.grainchainchallenge.utils.Utils.distanceRoute
import dagger.hilt.android.AndroidEntryPoint



@Suppress("DEPRECATION")
@AndroidEntryPoint
class MapsFragment : Fragment() {
    private lateinit var binding: FragmentMapsBinding
    var gMap: GoogleMap? = null
    private val PERMISSION_ID: Int = 111
    private lateinit var receiver: LocationBroadcastReceiver
    private var listPoints = ArrayList<LatLng>()
    private var isTracking = false
    private val viewModel by viewModels<RouteViewModel>()
    private lateinit var adapter: RouteAdapter
    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap

        myLocationPermissons()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        try {
            if (adapter != null) {
                adapter.notifyDataSetChanged()
            }
        }catch (e:Exception){}

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        receiver = LocationBroadcastReceiver()
        binding.rvRouteList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.btnSave.setOnClickListener{
            if(!isTracking) {
                myLocationPermissons()
                startLocationService()
                binding.btnSave.setText(resources.getString(R.string.btn_Stop))
                binding.btnSave.setBackgroundColor(Color.RED)
                binding.btnSave.setTextColor(Color.WHITE)
                isTracking = true
                gMap?.clear()
            }else{
//                requireActivity().unregisterReceiver(receiver)
                binding.btnSave.setText("Grabar ruta")
                binding.btnSave.setBackgroundColor(Color.BLUE)
                binding.btnSave.setTextColor(Color.WHITE)
                binding.btnSave.setText(resources.getString(R.string.btn_Save))
                isTracking = false
                LocationService.isStartService.value = false

            }
        }
        loadRouteList()
        observers()
    }

    private fun showMarket(){
        gMap!!.addMarker(
            MarkerOptions()
                .title("Inicio del recorrido")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(listPoints[0]))!!
        gMap!!.addMarker(
            MarkerOptions()
                .title("Fin del recorrido")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(listPoints[listPoints.size - 1]))!!
    }
    private fun dataRoute(timer : Long){

        var distance = 0.0
        distance =  distanceRoute(listPoints)/1000
        var time = Utils.convertSecondsToHMmSs(timer)


        showMarket()
        var routeData = RouteModel("",distance,time,Utils.convertListPointToString(listPoints))
        NameSaveRouteDialog.newInstance(routeData).show(parentFragmentManager,"")

    }



    private fun startLocationService(){
        val filter = IntentFilter("ACT_LOC")
       requireActivity().registerReceiver(receiver, filter)
        val intent = Intent(requireContext(), LocationService::class.java)
        requireActivity().startService(intent)
        LocationService.isStartService.value = true
    }
    @SuppressLint("MissingPermission")
    private fun myLocationPermissons() {

        if (Utils.checkPermissions(requireContext())) {
            if(Utils.isLocationEnabled(requireContext())) {
                gMap?.uiSettings?.isMyLocationButtonEnabled = true
                gMap?.isMyLocationEnabled = true

                // Check if we were successful in obtaining the map.
                if (gMap != null) {
                    gMap?.setOnMyLocationChangeListener(OnMyLocationChangeListener { arg0 ->

                        var myLocation = LatLng(arg0.latitude, arg0.longitude)
                        gMap?.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
                        gMap?.animateCamera(CameraUpdateFactory.zoomTo(20f))

                    })
                }
            }else{
                alertEnableGps()
            }

        }else{
            requestPermissions()
        }
    }

    private fun requestPermissions(){


        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myLocationPermissons()
            }else{
                alertEnablePermissions()
            }
        }
    }

    private fun alertEnablePermissions(){
        AlertDialog.Builder(context)
            .setTitle(requireContext().getString(R.string.title_alert))
            .setMessage(requireContext().getString(R.string.description_alert))
            .setCancelable(false)
            .setPositiveButton(requireContext().getString(R.string.enable_button)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + requireActivity().getPackageName())
                startActivity(intent)
            }
            .show()
    }

    private fun alertEnableGps(){
        AlertDialog.Builder(context)
            .setTitle(requireContext().getString(R.string.title_gps))
            .setMessage(requireContext().getString(R.string.description_gps))
            .setCancelable(false)
            .setPositiveButton(requireContext().getString(R.string.enable_gps)) { _, _ ->
                requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

            }
            .show()

    }

   private fun observers(){
       LocationService.geoLocation.observe(viewLifecycleOwner, Observer {
           if(isTracking) {
               listPoints = it
               addPolyline()
           }
       })
       LocationService.timeRoute.observe(viewLifecycleOwner, Observer {
           dataRoute(it)
       })

       viewModel.saveSuccess.observe(viewLifecycleOwner, Observer {
           loadRouteList()
       })

    }
    private fun loadRouteList(){
        viewModel.routesInDB.observe(viewLifecycleOwner, Observer {

            if(it.isEmpty()){
                binding.rvRouteList.visibility = View.GONE
                binding.txtLabelRoutes.visibility = View.GONE
            }else {
                binding.rvRouteList.visibility = View.VISIBLE
                binding.txtLabelRoutes.visibility = View.VISIBLE
                var adapter = RouteAdapter(it.reversed()) {
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main_container,DetailRouteFragment.newInstance(it)).addToBackStack("detail").commit()

                }

                binding.rvRouteList.adapter = adapter
                adapter.notifyDataSetChanged()
            }

        })
    }


    private fun addPolyline() {
        if(listPoints.isNotEmpty() && listPoints.size > 1) {
            val preLastLatLng = listPoints[listPoints.size - 2]
            val lastLatLng = listPoints[listPoints.size - 1]

            val polylineOptions = PolylineOptions()
                .color(Color.RED)
                .width(20f)
                .jointType(JointType.ROUND)
                .add(preLastLatLng)
                .add(lastLatLng)

            gMap?.addPolyline(polylineOptions)
        }
    }


    class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

        }
    }

}