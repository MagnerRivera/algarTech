package com.example.algartech.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algartech.room.User
import com.example.algartech.room.UserDao
import com.example.algartech.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<User>>()
    val login = _login.asSharedFlow()

    // Función para el login
    fun login(username: String, password: String) {
        viewModelScope.launch { _login.emit(Resource.Loading()) }

        // Logica para e incio de sesion
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByEmailAndPassword(username, password)
            if (user != null) {
                viewModelScope.launch {
                    _login.emit(Resource.Success(user))
                }
            } else {
                viewModelScope.launch {
                    _login.emit(Resource.Error("Credenciales incorrectas"))
                }
            }
        }
    }
}