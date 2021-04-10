package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.joins.SpeciesJoin
import com.sealstudios.pokemonApp.database.`object`.joins.TypeMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.joins.TypesJoin
import com.sealstudios.pokemonApp.database.`object`.objectInterface.PokemonAdapterListItem

data class PokemonWithTypesAndSpeciesForList constructor(

        @Embedded
        val pokemon: PokemonForList,
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
        val species: Species?,

        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = TypeMetaData::class,
                entityColumn = TypeMetaData.TYPE_META_DATA_ID,
                associateBy = Junction(
                        value = TypeMetaDataJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = TypeMetaData.TYPE_META_DATA_ID
                )
        )
        val typeMetaData: TypeMetaData?
) : PokemonAdapterListItem


