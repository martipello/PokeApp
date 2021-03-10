package com.sealstudios.pokemonApp.ui.preferences

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.FragmentPreferenceBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetMargin

class PreferenceFragment : Fragment() {

    private var _binding: FragmentPreferenceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        _binding = FragmentPreferenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar()
        view.doOnApplyWindowInsetMargin { myView, windowInsets, marginLayoutParams ->
            marginLayoutParams.topMargin = windowInsets.systemWindowInsetTop
            marginLayoutParams.leftMargin = windowInsets.systemWindowInsetLeft
            marginLayoutParams.rightMargin = windowInsets.systemWindowInsetRight
            marginLayoutParams.bottomMargin = windowInsets.systemWindowInsetBottom
            myView.layoutParams = marginLayoutParams
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setActionBar() {
        binding.toolbar.setupWithNavController(findNavController())
    }

}