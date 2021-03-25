package com.sealstudios.pokemonApp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sealstudios.pokemonApp.ui.PokemonInfoFragment
import com.sealstudios.pokemonApp.ui.MovesFragment
import com.sealstudios.pokemonApp.ui.StatsFragment

class DetailViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList = Fragments.values()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (fragmentList[position]) {
            Fragments.INFO -> PokemonInfoFragment()
            Fragments.STATS -> StatsFragment()
            Fragments.MOVES -> MovesFragment()
        }
    }

    enum class Fragments {
        INFO,
        STATS,
        MOVES
    }
}

