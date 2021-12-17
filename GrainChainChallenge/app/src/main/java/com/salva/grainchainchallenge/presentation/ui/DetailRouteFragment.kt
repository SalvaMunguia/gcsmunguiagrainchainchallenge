package com.salva.grainchainchallenge.presentation.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

import com.salva.grainchainchallenge.R
import com.salva.grainchainchallenge.data.model.RouteModel
import com.salva.grainchainchallenge.databinding.FragmentDetailRouteBinding
import com.salva.grainchainchallenge.presentation.viewmodel.RouteViewModel
import com.salva.grainchainchallenge.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "route"


@AndroidEntryPoint
class DetailRouteFragment : Fragment() {

    private var route: RouteModel? = null
    private lateinit var binding :FragmentDetailRouteBinding
    private val viewModel by viewModels<RouteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            route = it.getSerializable(ARG_PARAM1) as RouteModel

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailRouteBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtNameDetail.text = route?.nameRoute
        binding.txtDistanceDetail.text = Utils.convertDistanceToFormat(route?.distanceRoute!!) + "km"
        binding.txtTimeDetail.text = route?.timeRoute
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(object : OnMapReadyCallback{
            override fun onMapReady(map: GoogleMap) {

                showRoutePolylines(map)
            }
        })

        binding.btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.deleteRouteDB(route!!)
                requireActivity().onBackPressed()
            }
        }

        binding.btnShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hola te invito a que pruebes esta app, he recorrido ${binding.txtDistanceDetail.text.toString()} en ${binding.txtTimeDetail.text.toString()} de tiempo")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }


    }

    private fun showRoutePolylines(map: GoogleMap){
        var listPoints = Utils.convertStringToListPoints(route!!.listPoints)

        var myLocation = LatLng(listPoints[0].latitude, listPoints[0].longitude)
        map?.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        map?.animateCamera(CameraUpdateFactory.zoomTo(15f))
        listPoints.forEachIndexed { index, latLng ->
            if(index> 1) {
                val preLastLatLng = listPoints[index - 1]
                val lastLatLng = listPoints[index]

                val polylineOptions = PolylineOptions()
                    .color(Color.RED)
                    .width(20f)
                    .jointType(JointType.ROUND)
                    .add(preLastLatLng)
                    .add(lastLatLng)

                map?.addPolyline(polylineOptions)
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(route: RouteModel) =
            DetailRouteFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, route)

                }
            }
    }
}