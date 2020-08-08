package com.sealstudios.pokemonApp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.sealstudios.pokemonApp.api.GetAllPokemonViewModel
import com.sealstudios.pokemonApp.api.notification.NotificationHelper
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper.Companion.isFirstTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    @Inject
    lateinit var notificationHelper: NotificationHelper
    private val getAllPokemonViewModel: GetAllPokemonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (sharedPreferenceHelper.getBool(isFirstTime)) {
            downloadPokemonData()
            sharedPreferenceHelper.setBool(isFirstTime, false)
        }
    }

    private fun downloadPokemonData() {
        Log.d("WORKMANAGER", "GET ALL POKEMON MAIN")
        getAllPokemonViewModel.getAllPokemon()
    }

    private fun findNavController(): NavController {
        return findNavController(this, R.id.nav_host_fragment)
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