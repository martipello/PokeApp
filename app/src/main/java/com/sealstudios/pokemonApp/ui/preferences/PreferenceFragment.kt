package com.sealstudios.pokemonApp.ui.preferences

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
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
        binding.toolbar.outlineProvider = null
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

}