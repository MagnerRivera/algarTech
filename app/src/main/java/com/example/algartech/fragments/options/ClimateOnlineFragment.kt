package com.example.algartech.fragments.options

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.algartech.R
import com.example.algartech.animation.AnimationUtils
import com.example.algartech.databinding.FragmentClimateOnlineBinding
import com.example.algartech.retrofit.RetrofitClient
import com.example.algartech.retrofit.WeatherResponse
import com.example.algartech.viewModel.ClimateOnlineViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import kotlin.math.roundToInt

@AndroidEntryPoint
class ClimateOnlineFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private lateinit var binding: FragmentClimateOnlineBinding

    private val viewModel by viewModels<ClimateOnlineViewModel>()

    private var currentWeatherResponse: WeatherResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimateOnlineBinding.inflate(inflater)

        binding.backManagement.setOnClickListener {
            findNavController().popBackStack()
        }

        // Configuración de la barra de búsqueda
        val imageSearch = binding.imageSearch
        val buttonSearchCard = binding.buttonSearchCard
        val editTextSearch = binding.editTextSearch

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

        buttonSearchCard.setOnClickListener {
            if (isNetworkAvailable()) {
                val city = editTextSearch.text.toString().trim()
                if (city.isNotEmpty()) {
                    searchWeatherByCity(city)
                } else {
                    showToast(getString(R.string.enter_valid_city))
                }
            } else {
                showToast(getString(R.string.no_internet_connection))
                binding.temp.visibility = View.GONE
                binding.btnCheck.visibility = View.INVISIBLE
                binding.txtAdd.visibility = View.INVISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    showToast(getString(R.string.go_to_saved_maps))
                }, 2000)
            }
            hideKeyboard()
        }

        binding.btnCheck.setOnClickListener {
            currentWeatherResponse?.let { weatherResponse ->
                viewModel.handleWeatherResponse(weatherResponse)
            }
        }
        viewModel.saveResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showToast("Datos guardados correctamente en la base de datos.")
                binding.linearOptions.visibility = View.INVISIBLE
                binding.txtAdd.visibility = View.INVISIBLE
                binding.editTextSearch.setText("")
            } else {
                showToast("Error: Ciudad ya agregada.")
            }
        }

        return binding.root
    }

    private fun searchWeatherByCity(city: String) {
        val apiKey = "0b8590c38152b6c34fdc510027153f3a"
        val call = RetrofitClient.weatherService.getWeatherByCity(city, apiKey)

        call.enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: retrofit2.Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    handleWeatherResponse(response.body())
                } else {
                    showToast(getString(R.string.enter_valid_city))
                    Log.e("WeatherResponse1", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                showToast(getString(R.string.error_service_consumption))
                Log.e("WeatherResponse2", "Error: ${t.message}")
            }
        })
    }

    private fun handleWeatherResponse(weatherResponse: WeatherResponse?) {
        weatherResponse?.let {
            val lat = it.coord.lat
            val lon = it.coord.lon

            val tvTemp = view?.findViewById<TextView>(R.id.tvTemp)
            val tvWeatherDescription = view?.findViewById<TextView>(R.id.tvDescript)
            val tvWindSpeed = view?.findViewById<TextView>(R.id.tvFast)
            val tvHumidity = view?.findViewById<TextView>(R.id.tvHumidity)

            val temperature = it.main.temp
            val temperatureInCelsius = (temperature - 32) * 5 / 9
            val roundedTemperature = temperatureInCelsius.roundToInt()

            tvTemp?.text = getString(R.string.temperature_format, roundedTemperature)
            tvWeatherDescription?.text =
                getString(R.string.weather_description_format, it.weather[0].description)
            tvWindSpeed?.text = getString(R.string.wind_speed_format, it.wind.speed)
            tvHumidity?.text = getString(R.string.humidity_format, it.main.humidity)

            setWeatherImage(it.weather[0].icon)

            binding.temp.visibility = View.VISIBLE
            binding.linearOptions.visibility = View.VISIBLE
            binding.txtAdd.visibility = View.VISIBLE

            centerMapOnCoordinates(lat, lon)

            currentWeatherResponse = it
        }
    }

    private fun setWeatherImage(icon: String?) {
        val imageTemp = view?.findViewById<ImageView>(R.id.imageTemp)
        when (icon) {
            "01d" -> imageTemp?.setImageResource(R.drawable.clear_sky_d)
            "01n" -> imageTemp?.setImageResource(R.drawable.clear_sky_n)
            "02d" -> imageTemp?.setImageResource(R.drawable.few_clouds_d)
            "02n" -> imageTemp?.setImageResource(R.drawable.few_clouds_n)
            "03d" -> imageTemp?.setImageResource(R.drawable.scattered_cloud_d)
            "03n" -> imageTemp?.setImageResource(R.drawable.scattered_cloud_n)
            "04d" -> imageTemp?.setImageResource(R.drawable.broken_clouds_d)
            "04n" -> imageTemp?.setImageResource(R.drawable.broken_clouds_n)
            "09d" -> imageTemp?.setImageResource(R.drawable.shower_rain_d)
            "09n" -> imageTemp?.setImageResource(R.drawable.shower_rain_n)
            "10d" -> imageTemp?.setImageResource(R.drawable.rain_d)
            "10n" -> imageTemp?.setImageResource(R.drawable.rain_n)
            "11d" -> imageTemp?.setImageResource(R.drawable.thunderstorm_d)
            "11n" -> imageTemp?.setImageResource(R.drawable.thunderstorm_n)
            "13d" -> imageTemp?.setImageResource(R.drawable.snow_d)
            "13n" -> imageTemp?.setImageResource(R.drawable.snow_n)
            "50d" -> imageTemp?.setImageResource(R.drawable.mist_d)
            "50n" -> imageTemp?.setImageResource(R.drawable.mist_n)
            else -> imageTemp?.setImageResource(R.drawable.broken_clouds_d)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun centerMapOnCoordinates(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Configuro la cámara para centrarse en la ubicación por defecto
        val defaultLocation = LatLng(0.0, 0.0)
        val zoomLevel = 2f // Ajusto el nivel de zoom
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, zoomLevel))
    }
}