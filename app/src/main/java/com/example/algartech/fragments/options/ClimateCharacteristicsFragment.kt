package com.example.algartech.fragments.options

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.algartech.R
import com.example.algartech.adapter.CitiesAdapter
import com.example.algartech.databinding.FragmentClimateCharacteristicsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class ClimateCharacteristicsFragment : Fragment(), CitiesAdapter.OnCityClickListener {

    private lateinit var binding: FragmentClimateCharacteristicsBinding

    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimateCharacteristicsBinding.inflate(inflater)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragmentChracter) as SupportMapFragment

        // Obtener los argumentos
        val arguments = arguments
        if (arguments != null) {
            val latitude = arguments.getFloat("latitude", 0f)
            val longitude = arguments.getFloat("longitude", 0f)
            val cityName = arguments.getString("cityName", "")

            // Actualizar el mapa con las nuevas coordenadas
            setupMap(latitude.toDouble(), longitude.toDouble())

            binding.cityChracter.text = cityName

            // Puedes hacer otras operaciones relacionadas con la ciudad si es necesario
            Log.d(
                "ClimateCharacteristics",
                "City clicked: $cityName, Lat: $latitude, Lon: $longitude"
            )
        }

        binding.backManagementChracter.setOnClickListener {
            // Regresar al fragmento anterior (climateOfflineFragment)
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun setupMap(latitude: Double, longitude: Double) {
        mapFragment.getMapAsync { googleMap ->
            val location = LatLng(latitude, longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
        }
    }

    override fun onCityClick(latitude: Double, longitude: Double, cityName: String) {
        // Actualizar el mapa con las nuevas coordenadas
        setupMap(latitude, longitude)

        // Puedes hacer otras operaciones relacionadas con la ciudad si es necesario
        Log.d("ClimateCharacteristics", "City clicked: $cityName, Lat: $latitude, Lon: $longitude")
    }
}