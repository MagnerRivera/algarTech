package com.example.algartech.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.algartech.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController
import com.example.algartech.activities.OptionsActivity
import com.example.algartech.databinding.FragmentLoginBinding
import com.example.algartech.utils.Resource
import com.example.algartech.viewModel.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    // Creo la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    // Configuro la lógica del fragmento después de que se haya creado la vista
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navego al fragmento de registro al hacer click en el texto de "No tengo una cuenta"
        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrerFragment)
        }

        //dialog for login
        binding.apply {
            buttonLoginLogin.setOnClickListener {
                val name = edNameLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(name, password)
            }
        }

        // Observo el estado del inicio de sesión y toma medidas en consecuencia ya sea exitoso o fallido segun credenciales
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonLoginLogin.revertAnimation()
                        // Inicio la actividad de home que contiene las opciones de Registrar o Gestionar la tarjeta si el inicio de sesión es exitoso
                        Intent(requireActivity(), OptionsActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            Toast.makeText(
                                requireContext(),
                                "Inicio de sesión exitoso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is Resource.Error -> {
                        // Muestro un mensaje de error en caso de error durante el inicio de sesión
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLoginLogin.revertAnimation()
                    }

                    else -> Unit

                }
            }
        }
    }
}