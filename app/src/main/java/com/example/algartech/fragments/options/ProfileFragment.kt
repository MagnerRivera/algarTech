package com.example.algartech.fragments.options

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.algartech.activities.LoginRegisterActivity
import com.example.algartech.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    // Creo la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)

        return binding.root
    }

    // Configuro la vista después de que se haya creado
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Botón de cierre de sesión por su ID
        val logoutButton = binding.logout

        // Establece un OnClickListener para el botón de cierre de sesión
        logoutButton.setOnClickListener {
            // Muestro un cuadro de diálogo de confirmación para cerrar la sesión
            showLogoutConfirmationDialog()
        }

    }

    // Método para mostrar el cuadro de diálogo de confirmación para cerrar la sesión
    private fun showLogoutConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Cerrar Sesión")
            setMessage("¿Quieres cerrar la sesión?")
            setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Sí") { dialog, _ ->
                val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

                dialog.dismiss()
            }
        }

        alertDialog.create()
        alertDialog.show()
    }
}
