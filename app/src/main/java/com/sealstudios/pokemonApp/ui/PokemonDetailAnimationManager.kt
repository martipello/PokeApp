package com.sealstudios.pokemonApp.ui

import android.animation.Animator
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitEnd
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitTransitionEnd
import com.sealstudios.pokemonApp.ui.listenerExtensions.startAndWait
import kotlinx.coroutines.*
import kotlin.coroutines.resume

abstract class PokemonDetailAnimationManager : Fragment() {

    private var _binding: PokemonDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        addBackButtonCallback()
    }

    fun setPokemonDetailFragmentBinding(binding: PokemonDetailFragmentBinding){
        _binding = binding
    }

    suspend fun handleEnterAnimation(): Boolean =
        suspendCancellableCoroutine { continuation ->
            lifecycleScope.launch {
                sharedElementEnterTransition = TransitionInflater.from(context)
                    .inflateTransition(R.transition.shared_element_transition)
                (sharedElementEnterTransition as TransitionSet).run {
                    awaitTransitionEnd {
                        startPostponedEnterTransition()
                    }
                }
                binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToState(
                    R.id.large_image
                )
                createCircleReveal().run {
                    startAndWait()
                    binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder
                        .setCardBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                android.R.color.transparent
                            )
                        )
                    awaitEnd()
                }
                continuation.resume(true)
            }
        }

    private fun createCircleReveal(): Animator {
        val x = binding.splash.right / 2
        val location = IntArray(2)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView
            .getLocationOnScreen(location)
        val y = location[1] + binding.pokemonImageViewHolderLayout
            .pokemonBackgroundCircleView.height / 2
        return binding.splash.circleReveal(null, startAtX = x, startAtY = y)
    }

    private suspend fun handleExitAnimation() = lifecycleScope.launch {
        val x: Int = binding.splash.right / 2
        val location = IntArray(2)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.getLocationOnScreen(
            location
        )
        val y =
            location[1] + binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.height / 2
        binding.splash.circleHide(null, endAtX = x, endAtY = y).run {
            startAndWait()
            delay(150)
            binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToStart()
            popDelayed()
            awaitEnd()
            binding.splash.visibility = View.INVISIBLE
        }
    }

    private fun addBackButtonCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewLifecycleOwner.lifecycleScope.launch {
                handleExitAnimation()
            }
            this.remove()
        }
    }

    private suspend fun popDelayed() {
        withContext(Dispatchers.Main) {
            delay(100)
            findNavController().popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    handleExitAnimation()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}