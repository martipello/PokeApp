package com.sealstudios.pokemonApp.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.ui.viewModel.PartialPokemonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadRequestDialog : DialogFragment() {

    private val partialPokemonViewModel: PartialPokemonViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setTitle(getString(R.string.permission_request_title))
        builder.setMessage(getString(R.string.permission_request_body))
        builder.setPositiveButton(getString(R.string.permission_request_positive_button_text)) { dialog, _ ->
            activity?.let { partialPokemonViewModel.startFetchAllPokemonTypesAndSpeciesWorkManager(it) }
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        return builder.create()
    }

    companion object{
        const val TAG = "DownloadRequestDialog"
    }
}