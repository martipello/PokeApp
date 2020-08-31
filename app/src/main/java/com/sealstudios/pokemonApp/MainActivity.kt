package com.sealstudios.pokemonApp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.api.GetAllPokemonViewModel
import com.sealstudios.pokemonApp.databinding.ActivityMainBinding
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper.Companion.isFirstTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    @Inject
    lateinit var glide: RequestManager

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val getAllPokemonViewModel: GetAllPokemonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
//        binding.root.systemUiVisibility =
//            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        checkIsFirstTime()
    }

    private fun checkIsFirstTime() {
        if (sharedPreferenceHelper.getBool(isFirstTime)) {
            downloadPokemonData()
            sharedPreferenceHelper.setBool(isFirstTime, false)
        }
    }

    private fun downloadPokemonData() {
        getAllPokemonViewModel.getAllPokemon()
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() ||
                super.onSupportNavigateUp()
    }
}