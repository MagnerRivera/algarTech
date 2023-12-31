package com.example.algartech.viewModel

import com.example.algartech.room.User
import com.example.algartech.room.UserDao
import com.example.algartech.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var userDao: UserDao

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        userDao = mockk()
        viewModel = LoginViewModel(userDao)
    }

    @Test
    fun `login success`() = runBlocking {
        // Arrange
        val username = "prueba con el usuario"
        val password = "prueba con la contraseña"
        val user = User(username = username, password = password, email = "pruebaPruebva@example.com")
        coEvery { userDao.getUserByEmailAndPassword(username, password) } returns user

        // Act
        viewModel.login(username, password)

        // Assert
        val result = viewModel.login.first()
        assertEquals(Resource.Success(user.copy()).data, result.data)
    }

    @Test
    fun `login failure`() = runBlocking {
        // Arrange
        val username = "prueba con el usuario"
        val password = "contraseñaconusuario"
        coEvery { userDao.getUserByEmailAndPassword(username, password) } returns null

        // Act
        viewModel.login(username, password)

        // Assert
        val result = viewModel.login.first()
        assertEquals("Credenciales incorrectas", result.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}