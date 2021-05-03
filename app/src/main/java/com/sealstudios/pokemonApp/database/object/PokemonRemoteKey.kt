package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class PokemonRemoteKey(
    @PrimaryKey
    val repoId: Int,
    val nextPageKey: Int?,
    val prevPageKey: Int?
)