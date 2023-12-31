package com.example.algartech.utils

sealed class RegisterValidation {
    object Success : RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}

// Modelo de datos que representa el estado de los campos de registro
data class RegisterFieldsState(
    val password: RegisterValidation,
    val username: RegisterValidation,
    val email: RegisterValidation
)