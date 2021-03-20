package com.sealstudios.pokemonApp.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DownloadRequestDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setTitle("Permission Request")
        builder.setMessage("To get the best experience we need to download all pokemon meta data. " +
                "This is a large download, proceed?")
        builder.setPositiveButton("OK, download") { dialog, _ ->
//            partialPokemonViewModel.startFetchAllPokemonTypesAndSpeciesWorkManager()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        return builder.create()
    }

    companion object{
        const val TAG = "DownloadRequestDialog"
    }
}