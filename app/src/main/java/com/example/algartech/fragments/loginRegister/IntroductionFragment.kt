package com.example.algartech.fragments.loginRegister

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.algartech.R
import com.example.algartech.databinding.FragmentIntroductionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding

    private var nightMode: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences

    // Creo la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflo y vinculo la vista usando el View Binding
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    //aqui configuro el tema de la aplicación, en este caso la aplicación siempre es tema claro
    private fun setupNightMode() {
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // Configuro el modo nocturno y manejo el evento de click del botón de inicio
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuro el evento de click para empézar con  la navegación de la aplicación
        binding.buttonStart.setOnClickListener {
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
        //configuración del modo claro en la aplicación
        sharedPreferences = requireContext().getSharedPreferences("MODE", Context.MODE_PRIVATE)
        nightMode = sharedPreferences.getBoolean("nightMode", false)

        setupNightMode()
    }
}