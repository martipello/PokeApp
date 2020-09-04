package com.sealstudios.pokemonApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.sealstudios.pokemonApp.databinding.SplashScreenLayoutBinding

class SplashScreenFragment : Fragment() {

    private var _binding: SplashScreenLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SplashScreenLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMotionLayoutListener()

    }

    private fun setMotionLayoutListener() {
        binding.motionRoot.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                navigateToDetailFragment()
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }


    private fun navigateToDetailFragment() {
        NavHostFragment.findNavController(this@SplashScreenFragment)
            .navigate(R.id.action_splashScreenFragment_to_PokemonListFragment)
    }


}