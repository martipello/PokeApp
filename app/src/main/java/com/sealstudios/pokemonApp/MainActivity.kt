package com.sealstudios.pokemonApp

import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sealstudios.pokemonApp.api.GetAllPokemonViewModel
import com.sealstudios.pokemonApp.databinding.ActivityMainBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetPadding
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper.Companion.isFirstTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val getAllPokemonViewModel: GetAllPokemonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSystemUiVisibility()
        checkIsFirstTime()
    }

    private fun setSystemUiVisibility() {
        binding.root.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun getTheme(): Resources.Theme? {
        val theme: Resources.Theme = super.getTheme()
        theme.applyStyle(R.style.AppTheme, true)
        return theme
    }
}