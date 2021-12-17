package com.salva.grainchainchallenge.presentation.customviews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.salva.grainchainchallenge.data.model.RouteModel
import com.salva.grainchainchallenge.databinding.DialogNameSaveRouteBinding
import com.salva.grainchainchallenge.presentation.viewmodel.RouteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NameSaveRouteDialog : DialogFragment()  {
    private lateinit var binding: DialogNameSaveRouteBinding
    private val viewModel by viewModels<RouteViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogNameSaveRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSave.setOnClickListener {
            route.nameRoute = binding.txtNameRoute.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.saveRouteDB(route)
                dismiss()
            }
        }
    }
    override fun onStart() {
        super.onStart()


        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object{
        const val TAG = "SaveDialog"
        lateinit var route: RouteModel

        fun newInstance(data:RouteModel ): NameSaveRouteDialog{

            route = data
            val fragment = NameSaveRouteDialog()
            return fragment
        }
    }

}