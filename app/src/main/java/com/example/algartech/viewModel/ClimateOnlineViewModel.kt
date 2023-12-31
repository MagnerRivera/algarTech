package com.example.algartech.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algartech.retrofit.WeatherResponse
import com.example.algartech.room.ClimateDao
import com.example.algartech.room.ClimateEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ClimateOnlineViewModel @Inject constructor(
    private val climateDao: ClimateDao
) : ViewModel() {

    // LiveData para notificar sobre el resultado del guardado
    private val _saveResult = MutableLiveData<Boolean>()

    val saveResult: LiveData<Boolean> get() = _saveResult

    // Función para manejar la respuesta del clima y guardar en la base de datos
    fun handleWeatherResponse(weatherResponse: WeatherResponse?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                weatherResponse?.let {
                    val cityName = it.name

                    // Verificar si la ciudad ya existe en la base de datos
                    val existingCity = climateDao.getClimateDataByName(cityName)

                    if (existingCity == null) {
                        // La ciudad no existe, entonces podemos agregarla a la base de datos
                        val climateEntity = ClimateEntity(
                            name = cityName,
                            lat = it.coord.lat,
                            lon = it.coord.lon,
                            temperature = calculateTemperatureInCelsius(it.main.temp),
                            description = it.weather[0].description,
                            windSpeed = it.wind.speed,
                            humidity = it.main.humidity,
                            icon = it.weather[0].icon
                        )

                        climateDao.insert(climateEntity)
                        // Notificar que la operación fue exitosa
                        _saveResult.postValue(true)
                    } else {
                        // La ciudad ya existe, notificar que la operación no fue exitosa
                        _saveResult.postValue(false)
                    }
                }
            }
        }
    }

    private fun calculateTemperatureInCelsius(temperatureInFahrenheit: Double): Int {
        return ((temperatureInFahrenheit - 32) * 5 / 9).toInt()
    }
}