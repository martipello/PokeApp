package com.sealstudios.pokemonApp.ui

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.SplashScreenContainerBinding
import kotlin.math.hypot


class SplashScreenFragment : Fragment() {

    private var _binding: SplashScreenContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SplashScreenContainerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    //TODO allowEnterTransitionOverlap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMotionLayoutListener()
        binding.splashScreen.motionRoot.transitionToState(R.id.pokeball_landed)
    }

    private fun setMotionLayoutListener() {
        binding.splashScreen.motionRoot.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                createRevealAnimation()
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    private fun createRevealAnimation() {
        val x: Int = binding.root.right / 2
        val y: Int = binding.root.bottom - binding.root.bottom / 9
        val endRadius = hypot(binding.root.width.toDouble(), binding.root.height.toDouble()).toInt()
        val anim = createCircleRevealAnimator(x, y, endRadius)
        addCircleRevealAnimationListener(anim)
        anim.duration = 250
        binding.pokeballOpen.visibility = View.VISIBLE
        anim.start()
    }

    private fun createCircleRevealAnimator(x: Int, y: Int, endRadius: Int): Animator {
        return ViewAnimationUtils.createCircularReveal(
            binding.pokeballOpen, x, y,
            0f,
            endRadius.toFloat()
        )
    }

    private fun addCircleRevealAnimationListener(anim: Animator) {
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                navigateToListFragment()
            }

            override fun onAnimationEnd(animator: Animator) {
            }
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
    }

    private fun navigateToListFragment() {
        findNavController().navigate(R.id.action_splashScreenFragment_to_PokemonListFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}