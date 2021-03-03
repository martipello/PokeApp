package com.sealstudios.pokemonApp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sealstudios.pokemonApp.ui.PokemonBaseStatsFragment
import com.sealstudios.pokemonApp.ui.PokemonMovesFragment
import com.sealstudios.pokemonApp.ui.PokemonSpeciesFragment

class PokemonDetailViewPager(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList = Fragments.values()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (fragmentList[position]) {
            Fragments.INFO -> PokemonSpeciesFragment()
            Fragments.STATS -> PokemonBaseStatsFragment()
            Fragments.MOVES -> PokemonMovesFragment()
        }
    }

    enum class Fragments {
        INFO,
        STATS,
        MOVES
    }
}

