package com.sealstudios.pokemonApp.database.`object`

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.objectInterface.PokemonAdapterListItem

data class PokemonWithTypesAndSpeciesForList constructor(

    @Embedded
    val pokemon: PokemonForList,
    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonType::class,
        entityColumn = PokemonType.TYPE_ID,
        associateBy = Junction(
            value = PokemonTypesJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonType.TYPE_ID
        )
    )
    val types: List<PokemonType>,
    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonSpecies::class,
        entityColumn = PokemonSpecies.SPECIES_ID,
        associateBy = Junction(
            value = PokemonSpeciesJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonSpecies.SPECIES_ID
        )
    )
    val species: PokemonSpecies?
) : PokemonAdapterListItem


