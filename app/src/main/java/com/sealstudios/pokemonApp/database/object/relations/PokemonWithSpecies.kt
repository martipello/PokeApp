package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.Species
import com.sealstudios.pokemonApp.database.`object`.joins.SpeciesJoin

data class PokemonWithSpecies(
        @Embedded
        val pokemon: Pokemon,
        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = Species::class,
                entityColumn = Species.SPECIES_ID,
                associateBy = Junction(
                        value = SpeciesJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = Species.SPECIES_ID
                )
        )
        val species: Species?
)