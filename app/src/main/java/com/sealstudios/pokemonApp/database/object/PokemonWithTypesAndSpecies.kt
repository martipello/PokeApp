package com.sealstudios.pokemonApp.database.`object`

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.Relation

data class PokemonWithTypesAndSpecies @JvmOverloads constructor(

    @Ignore
    var matches : Int = 0,

    @Embedded
    val pokemon: Pokemon,
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
)


