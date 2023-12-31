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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.algartech.R
import com.example.algartech.databinding.FragmentRegisterBinding
import com.example.algartech.room.User
import com.example.algartech.utils.*
import com.example.algartech.viewModel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "RegisterFragment"

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

        setupClickListeners()
        setupTextWatchers()
        observeRegistrationState()
        observeValidation()

    }

    private fun setupClickListeners() {
        binding.tvDoYouHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registrerFragment_to_loginFragment)
        }

        binding.buttonRegisterRegister.setOnClickListener {
            performRegistration()
        }
    }

    private fun setupTextWatchers() {
        binding.edNameRegister.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                capitalizeName()
            }
        }
    }

    private fun capitalizeName() {
        val name = binding.edNameRegister.text.toString().trim()
        val capitalized = name.split(" ").joinToString(" ") { it ->
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        }
        binding.edNameRegister.setText(capitalized)
    }

    private fun performRegistration() {
        // Validaciones de campos antes de realizar el registro
        val nameRegex = binding.edNameRegister.text.toString().trim()
        val emailRegex = binding.edEmailRegister.text.toString().trim()
        val passwordRegex = binding.edPasswordRegister.text.toString().trim()

        // Validación del formato de correo electrónico
        if (!isValidEmail(emailRegex)) {
            binding.edEmailRegister.apply {
                requestFocus()
                error = "Formato de correo electrónico incorrecto"
            }
            return
        }

        // Validaciones adicionales y creación del objeto de usuario
        val nameValidation = validateName(nameRegex)
        if (nameValidation is RegisterValidation.Failed) {
            binding.edNameRegister.apply {
                requestFocus()
                error = nameValidation.message
            }
            return
        }

        val passwordValidation = validatePassword(passwordRegex)
        if (passwordValidation is RegisterValidation.Failed) {
            binding.edPasswordRegister.apply {
                requestFocus()
                error = passwordValidation.message
            }
            return
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

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                email.contains("@") &&
                (email.endsWith(".com") || email.endsWith(".co"))
    }

    private fun observeRegistrationState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
        }
    }

    private fun observeValidation() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    if (validation.email is RegisterValidation.Failed) {
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }

                    if (validation.password is RegisterValidation.Failed) {
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