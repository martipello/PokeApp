package com.sealstudios.pokemonApp.database.`object`.joins

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.MoveMetaData
import com.sealstudios.pokemonApp.database.`object`.MoveMetaData.Companion.META_MOVE_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, META_MOVE_ID])
class MoveMetaDataJoin(
        @NotNull
        @ColumnInfo(name = POKEMON_ID, index = true)
        val pokemon_id: Int,

        @NotNull
        @ColumnInfo(name = META_MOVE_ID, index = true)
        val meta_move_id: Int

) {
    companion object {
        fun createMoveMetaDataJoin(remotePokemonId: Int, pokemonMoveId: Int): MoveMetaDataJoin {
            return MoveMetaDataJoin(
                    remotePokemonId,
                    MoveMetaData.createMetaMoveId(remotePokemonId, pokemonMoveId)
            )
        }
    }
}