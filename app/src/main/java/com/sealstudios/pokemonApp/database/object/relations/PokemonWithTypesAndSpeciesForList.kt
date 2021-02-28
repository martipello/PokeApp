package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonSpeciesJoin
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonTypeMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonTypesJoin
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
        val species: PokemonSpecies?,

        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = PokemonTypeMetaData::class,
                entityColumn = PokemonTypeMetaData.TYPE_META_DATA_ID,
                associateBy = Junction(
                        value = PokemonTypeMetaDataJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = PokemonTypeMetaData.TYPE_META_DATA_ID
                )
        )
        val typeMetaData: PokemonTypeMetaData?
) : PokemonAdapterListItem


