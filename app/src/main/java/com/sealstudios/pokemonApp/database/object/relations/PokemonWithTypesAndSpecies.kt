package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonSpeciesJoin
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonTypesJoin

data class PokemonWithTypesAndSpecies constructor(

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


