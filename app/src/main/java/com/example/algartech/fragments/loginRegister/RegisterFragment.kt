package com.example.algartech.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.algartech.R
import com.example.algartech.databinding.FragmentRegisterBinding
import com.example.algartech.room.User
import com.example.algartech.utils.*
import com.example.algartech.viewModel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    // Creo la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    // Configura la lógica del fragmento después de que se haya creado la vista
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navega al fragmento de inicio de sesión al hacer clic en "¿Ya tienes una cuenta?"
        binding.tvDoYouHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registrerFragment_to_loginFragment)
        }

        binding.apply {
            edNameRegister.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val name = edNameRegister.text.toString().trim()
                    val capitalized = name.split(" ").joinToString(" ") { it.capitalize() }
                    edNameRegister.setText(capitalized)
                }
            }

            buttonRegisterRegister.setOnClickListener {
                // Validaciones de campos antes de realizar el registro
                val nameRegex = edNameRegister.text.toString().trim()
                val emailRegex = edEmailRegister.text.toString().trim()

                // Validación del formato de correo electrónico
                if (!Patterns.EMAIL_ADDRESS.matcher(emailRegex)
                        .matches() || !emailRegex.contains("@") || (!emailRegex.endsWith(".com") && !emailRegex.endsWith(
                        ".co"
                    ))
                ) {
                    binding.edEmailRegister.apply {
                        requestFocus()
                        error = "Formato de correo electrónico incorrecto"
                    }
                    return@setOnClickListener
                }

                // Validaciones adicionales y creación del objeto de usuario
                val passwordRegex = edPasswordRegister.text.toString().trim()

                val nameValidation = validateName(nameRegex)
                if (nameValidation is RegisterValidation.Failed) {
                    edNameRegister.apply {
                        requestFocus()
                        error = nameValidation.message
                    }
                    return@setOnClickListener
                }

                val passwordValidation = validatePassword(passwordRegex)
                if (passwordValidation is RegisterValidation.Failed) {
                    binding.edPasswordRegister.apply {
                        requestFocus()
                        error = passwordValidation.message
                    }
                    return@setOnClickListener
                }

                // Creación del objeto de usuario
                val user = User(
                    username = nameRegex,
                    email = emailRegex,
                    password = passwordRegex,
                )

                // Lanzamiento de la operación de registro en un nuevo hilo
                lifecycleScope.launch {
                    viewModel.createAccountWithEmailAndPassword(user, passwordRegex)
                }
            }
        }

        // Observa el estado del registro y toma medidas en consecuencia
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.e("test", it.data.toString())
                        binding.buttonRegisterRegister.revertAnimation()

                        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT)
                            .show()
                        // Retorno al fragmento anterior después del registro exitoso
                        findNavController().popBackStack()
                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }
        // Observo las validaciones de correo electrónico y contraseña
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}