package com.example.algartech.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.algartech.R
import com.example.algartech.room.ClimateEntity

class CitiesAdapter(private var cities: List<ClimateEntity>) :
    RecyclerView.Adapter<CitiesAdapter.CityViewHolder>() {

    private var cityClickListener: OnCityClickListener? = null

    class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.txtCityName)
    }

    fun updateCities(filteredCities: List<ClimateEntity>) {
        val oldSize = cities.size
        cities = filteredCities
        val newSize = filteredCities.size

        // Calcular la cantidad mínima de elementos a notificar
        val itemCount = if (oldSize < newSize) oldSize else newSize

        if (itemCount > 0) {
            // Notificar cambios en el rango de elementos que ha cambiado
            notifyItemRangeChanged(0, itemCount)
        }

        // Si la nueva lista tiene más elementos, notificar inserciones
        if (newSize > oldSize) {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        }
        // Si la nueva lista tiene menos elementos, notificar eliminaciones
        else if (newSize < oldSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        }
    }

    fun setOnCityClickListener(listener: OnCityClickListener) {
        this.cityClickListener = listener
    }

    interface OnCityClickListener {
        fun onCityClick(latitude: Double, longitude: Double, cityName: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.cityName.text = city.name

        holder.itemView.setOnClickListener {
            Log.d("CitiesAdapter", "City clicked: ${city.name}, Lat: ${city.lat}, Lon: ${city.lon}")
            cityClickListener?.onCityClick(city.lat, city.lon, city.name)
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }
}