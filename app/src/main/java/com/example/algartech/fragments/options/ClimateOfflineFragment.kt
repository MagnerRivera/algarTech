package com.example.algartech.fragments.options

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algartech.adapter.CitiesAdapter
import com.example.algartech.animation.AnimationUtils
import com.example.algartech.databinding.FragmentClimateOfflineBinding
import com.example.algartech.room.ClimateDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ClimateOfflineFragment : Fragment() {

    private lateinit var binding: FragmentClimateOfflineBinding

    @Inject
    lateinit var climateDao: ClimateDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimateOfflineBinding.inflate(inflater)

        // Utiliza una corrutina para obtener todas las ciudades desde la base de datos
        lifecycleScope.launch {
            val allCities = withContext(Dispatchers.IO) {
                climateDao.getAllClimateData()
            }

            val citiesAdapter = CitiesAdapter(allCities)
            citiesAdapter.setOnCityClickListener(object : CitiesAdapter.OnCityClickListener {
                override fun onCityClick(latitude: Double, longitude: Double, cityName: String) {
                    openClimateCharacteristicsFragment(latitude, longitude, cityName)
                }
            })

            binding.rvTablesCitiesOffline.layoutManager = LinearLayoutManager(context)
            binding.rvTablesCitiesOffline.adapter = citiesAdapter

            if (allCities.isEmpty()) {
                binding.rvTablesCitiesOffline.visibility = View.GONE
                binding.emptyTables.visibility = View.VISIBLE
            } else {
                binding.rvTablesCitiesOffline.visibility = View.VISIBLE
                binding.emptyTables.visibility = View.GONE
                binding.rvTablesCitiesOffline.adapter = citiesAdapter
            }

            // Configurar el TextWatcher para el EditText
            binding.editTextSearchCitiesOffline.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // No se utiliza en este caso
                }

                override fun onTextChanged(
                    charSequence: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    // Filtrar ciudades en función del texto ingresado
                    val searchText = charSequence.toString().trim()
                    val filteredCities = allCities.filter { city ->
                        city.name.contains(searchText, ignoreCase = true)
                    }
                    citiesAdapter.updateCities(filteredCities)
                }

                override fun afterTextChanged(editable: Editable?) {
                    // No se utiliza en este caso
                }
            })
        }

        binding.backManagementCitiesOffline.setOnClickListener {
            // Regresar al fragment anterior
            parentFragmentManager.popBackStack()
        }

        // Configuración de la barra de búsqueda
        val imageSearch = binding.imageSearchCitiesOffline
        val editTextSearch = binding.editTextSearchCitiesOffline

        imageSearch.setOnClickListener {
            if (editTextSearch.visibility == View.VISIBLE) {
                // Ocultar la barra de búsqueda
                AnimationUtils.slideViewUp(editTextSearch)
                editTextSearch.visibility = View.INVISIBLE
            } else {
                // Muestrar la barra de búsqueda
                AnimationUtils.slideViewDown(editTextSearch)
                editTextSearch.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    private fun openClimateCharacteristicsFragment(
        latitude: Double,
        longitude: Double,
        cityName: String
    ) {
        val action =
            ClimateOfflineFragmentDirections.actionClimateOfflineFragmentToClimateCharacteristicsFragment(
                latitude.toFloat(),
                longitude.toFloat(),
                cityName
            )
        findNavController().navigate(action)
    }
}