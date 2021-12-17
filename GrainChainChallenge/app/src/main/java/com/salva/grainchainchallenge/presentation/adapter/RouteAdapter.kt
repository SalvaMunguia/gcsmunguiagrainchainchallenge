package com.salva.grainchainchallenge.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salva.grainchainchallenge.data.model.RouteModel
import com.salva.grainchainchallenge.databinding.ItemRouteBinding
import com.salva.grainchainchallenge.utils.Utils

class RouteAdapter (val data: List<RouteModel>,val listener: (RouteModel) ->Unit) :
    RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteViewHolder {
        val binding = ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) =
        holder.bind(data[position],listener)

    override fun getItemCount() = data.size

    class RouteViewHolder(val binding: ItemRouteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(route: RouteModel, listener: (RouteModel) -> Unit) {
            binding.apply {
                binding.txtNameRoute.text = route.nameRoute
                binding.txtDistanceRoute.text = Utils.convertDistanceToFormat(route.distanceRoute) + "km"

            }

            binding.root.setOnClickListener {
                listener(route)
            }

        }
    }
}