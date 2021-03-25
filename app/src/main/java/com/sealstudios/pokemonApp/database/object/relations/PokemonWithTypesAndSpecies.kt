package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.Species
import com.sealstudios.pokemonApp.database.`object`.Type
import com.sealstudios.pokemonApp.database.`object`.joins.SpeciesJoin
import com.sealstudios.pokemonApp.database.`object`.joins.TypesJoin

data class PokemonWithTypesAndSpecies constructor(

        @Embedded
        val pokemon: Pokemon,
        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = Type::class,
                entityColumn = Type.TYPE_ID,
                associateBy = Junction(
                        value = TypesJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = Type.TYPE_ID
                )
        )
        val types: List<Type>,
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


