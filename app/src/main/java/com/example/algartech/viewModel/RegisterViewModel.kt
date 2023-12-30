package com.example.algartech.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algartech.room.User
import com.example.algartech.room.UserDao
import com.example.algartech.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    private var applicationContext: Context? = null

    private fun showToast(message: String) {
        applicationContext?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            _register.value = Resource.Loading()

            val newUser = User(
                password = password,
                username = user.username,
                email = user.email
            )

            viewModelScope.launch(Dispatchers.IO) {
                userDao.insert(newUser) // Run the insert operation on the IO thread
                _register.value = Resource.Success(newUser)
                showToast("Registro exitoso")
            }
        } else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password), validateName(user.username)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
            _register.value = Resource.Error("Validation failed")
            showToast("Error en el registro")
        }
    }

    // Validation for the user
    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val nameValidation = validateName(user.username)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success && nameValidation is RegisterValidation.Success

        return shouldRegister
    }
}