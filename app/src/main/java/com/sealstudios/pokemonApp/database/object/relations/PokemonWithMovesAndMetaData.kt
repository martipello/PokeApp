package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.Move
import com.sealstudios.pokemonApp.database.`object`.MoveMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.MoveMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.joins.MovesJoin

data class PokemonWithMovesAndMetaData(

        @Embedded
        val pokemon: Pokemon,
        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = Move::class,
                entityColumn = Move.MOVE_ID,
                associateBy = Junction(
                        value = MovesJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = Move.MOVE_ID
                )
        )
        val moves: List<Move>,

        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = MoveMetaData::class,
                entityColumn = MoveMetaData.META_MOVE_ID,
                associateBy = Junction(
                        value = MoveMetaDataJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = MoveMetaData.META_MOVE_ID
                )
        )
        val moveMetaData: List<MoveMetaData>

)