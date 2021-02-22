package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonMoveMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonMovesJoin

data class PokemonWithMovesAndMetaData(

        @Embedded
    val pokemon: Pokemon,
        @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonMove::class,
        entityColumn = PokemonMove.MOVE_ID,
        associateBy = Junction(
            value = PokemonMovesJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonMove.MOVE_ID
        )
    )
    val moves: List<PokemonMove>,

        @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonMoveMetaData::class,
        entityColumn = PokemonMoveMetaData.META_MOVE_ID,
        associateBy = Junction(
            value = PokemonMoveMetaDataJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonMoveMetaData.META_MOVE_ID
        )
    )
    val pokemonMoveMetaData: List<PokemonMoveMetaData>

)