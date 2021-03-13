package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.FragmentAboutBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetMargin

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar()
        synchronizeScrollWheelWithScrollView()
        view.doOnApplyWindowInsetMargin { myView, windowInsets, marginLayoutParams ->
            marginLayoutParams.topMargin = windowInsets.systemWindowInsetTop
            marginLayoutParams.leftMargin = windowInsets.systemWindowInsetLeft
            marginLayoutParams.rightMargin = windowInsets.systemWindowInsetRight
            marginLayoutParams.bottomMargin = windowInsets.systemWindowInsetBottom
            myView.layoutParams = marginLayoutParams
        }
    }

    private fun synchronizeScrollWheelWithScrollView() {
        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            binding.motionLayout.progress = scrollY.toFloat()
            val totalHeightOfScrollView = binding.scrollView.getChildAt(0).height - binding.scrollView.height
            val progress = (scrollY.toFloat() / totalHeightOfScrollView.toFloat())
            binding.motionLayout.progress = progress
        })

    }

    @SuppressLint("DefaultLocale")
    private fun setActionBar() {
        binding.toolbar.setupWithNavController(findNavController())
        colorToolbarBackground()
    }

    private fun colorToolbarBackground() {
        ContextCompat.getDrawable(binding.root.context, R.drawable.squareangle)?.let {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(it),
                    ContextCompat.getColor(binding.root.context, R.color.secondaryColor)
            )
            binding.toolbar.background = it
        }
    }


}