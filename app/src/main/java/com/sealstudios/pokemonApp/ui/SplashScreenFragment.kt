package com.sealstudios.pokemonApp.ui

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.get
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.SplashScreenContainerBinding
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitTransitionEnd
import com.sealstudios.pokemonApp.ui.listenerExtensions.startAndWait
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.hypot


class SplashScreenFragment : Fragment() {

    private var _binding: SplashScreenContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashScreenContainerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleEnterAnimation()
        binding.splashScreen.motionRoot.transitionToState(R.id.pokeball_landed)
    }

    private fun handleEnterAnimation() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            binding.splashScreen.motionRoot.run {
                Log.d("Transition", "motionRoot.run")
                awaitTransitionEnd()
                Log.d("Transition", "awaited end")
                createRevealAnimation()
            }
        }
    }

    private suspend fun createRevealAnimation() {
        withContext(Dispatchers.IO){
            val x: Int = binding.root.right / 2
            val y: Int = binding.root.bottom - binding.root.bottom / 9
            val endRadius = hypot(binding.root.width.toDouble(), binding.root.height.toDouble()).toInt()
            createCircleRevealAnimator(x, y, endRadius).run {
                duration = 250
                withContext(Dispatchers.Main){
                    binding.pokeballOpen.visibility = View.VISIBLE
                }
                startAndWait()
                navigateToListFragment()
            }
        }

    }

    private fun createCircleRevealAnimator(x: Int, y: Int, endRadius: Int): Animator {
        return ViewAnimationUtils.createCircularReveal(
            binding.pokeballOpen, x, y,
            0f,
            endRadius.toFloat()
        )
    }

    private fun navigateToListFragment() {
        val canNavigate = with(findNavController()) {
            currentDestination == graph[R.id.splashScreenFragment]
        }
        if (canNavigate) {
            findNavController().navigate(R.id.action_splashScreenFragment_to_PokemonListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

