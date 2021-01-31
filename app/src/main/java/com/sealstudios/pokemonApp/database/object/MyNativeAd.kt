package com.sealstudios.pokemonApp.database.`object`

import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.sealstudios.pokemonApp.database.`object`.objectInterface.PokemonAdapterListItem

data class MyNativeAd constructor(
    val ad: UnifiedNativeAd
) : PokemonAdapterListItem