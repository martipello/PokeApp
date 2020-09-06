package com.sealstudios.pokemonApp

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMotionLayoutListener()

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
        Log.d("SPLASH", "createRevealAnimation")
        val x: Int = binding.root.right / 2
        val y: Int = binding.root.bottom

        val startRadius = 0
        val endRadius = hypot(binding.root.width.toDouble(), binding.root.height.toDouble()).toInt()

        val anim: Animator = ViewAnimationUtils.createCircularReveal(
            binding.pokeballOpen, x, y,
            startRadius.toFloat(),
            endRadius.toFloat()
        )

        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                navigateToDetailFragment()
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })

        binding.pokeballOpen.visibility = View.VISIBLE

        anim.start()
    }

    private fun navigateToDetailFragment() {
        NavHostFragment.findNavController(this@SplashScreenFragment)
            .navigate(R.id.action_splashScreenFragment_to_PokemonListFragment)
    }


}