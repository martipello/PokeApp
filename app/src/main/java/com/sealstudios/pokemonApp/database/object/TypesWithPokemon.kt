package com.sealstudios.pokemonApp.database.`object`

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonType.Companion.POKEMON_TYPE_ID
import com.sealstudios.pokemonApp.database.`object`.Pokemon as pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonType as pokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin as pokemonTypesJoin

//data class TypesWithPokemon(
//    @Embedded()
//    val pokemonTypes: pokemonType,
//    @Relation(
//        parentColumn = POKEMON_TYPE_ID,
//        entity = com.sealstudios.pokemonApp.database.`object`.Pokemon::class,
//        entityColumn = POKEMON_ID,
//        associateBy = Junction(
//            value = pokemonTypesJoin::class,
//            parentColumn = POKEMON_TYPE_ID,
//            entityColumn = POKEMON_ID
//        )
//    )
//    val pokemon: List<pokemon>
//)


