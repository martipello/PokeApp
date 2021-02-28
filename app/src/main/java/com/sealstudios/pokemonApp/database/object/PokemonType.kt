package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.api.`object`.ApiPokemonType
import com.sealstudios.pokemonApp.api.`object`.Type
import com.sealstudios.pokemonApp.util.RoomStringListConverter
import org.jetbrains.annotations.NotNull

@TypeConverters(RoomStringListConverter::class)
@Entity
data class PokemonType(

        @NotNull
        @PrimaryKey
        @ColumnInfo(name = TYPE_ID)
        var id: Int,

        @ColumnInfo(name = TYPE_NAME)
        var name: String,

        @ColumnInfo(name = DOUBLE_DAMAGE_FROM)
        var doubleDamageFrom: List<String>,

        @ColumnInfo(name = DOUBLE_DAMAGE_TO)
        var doubleDamageTo: List<String>,

        @ColumnInfo(name = HALF_DAMAGE_FROM)
        var halfDamageFrom: List<String>,

        @ColumnInfo(name = HALF_DAMAGE_TO)
        var halfDamageTo: List<String>,

        @ColumnInfo(name = NO_DAMAGE_FROM)
        var noDamageFrom: List<String>,

        @ColumnInfo(name = NO_DAMAGE_TO)
        var noDamageTo: List<String>,

        ) {

    constructor(id: Int, name: String) : this(id, name, listOf(), listOf(), listOf(), listOf(), listOf(), listOf())

    companion object {

        const val TYPE_ID: String = "type_id"
        const val TYPE_NAME: String = "type_name"
        const val DOUBLE_DAMAGE_FROM: String = "double_damage_from"
        const val DOUBLE_DAMAGE_TO: String = "double_damage_to"
        const val HALF_DAMAGE_FROM: String = "half_damage_from"
        const val HALF_DAMAGE_TO: String = "half_damage_to"
        const val NO_DAMAGE_FROM: String = "no_damage_from"
        const val NO_DAMAGE_TO: String = "no_damage_to"

        fun getTypesInOrder(types: List<PokemonType>): List<PokemonType> {
            return types.sortedBy { it.name }
        }

        fun mapDbPokemonTypesFromPokemonResponse(apiPokemon: ApiPokemon): List<PokemonType> {
            return apiPokemon.types.map { apiType ->
                mapDbPokemonTypeFromPokemonResponse(apiType)
            }
        }

        fun mapDbPokemonTypeFromPokemonResponse(apiPokemonType: ApiPokemonType): PokemonType {
            return PokemonType(
                    id = Pokemon.getPokemonIdFromUrl(apiPokemonType.type.url),
                    name = apiPokemonType.type.name,
            )
        }
        fun mapDbPokemonTypeFromTypeResponse(type: Type): PokemonType {
            return PokemonType(
                    id = type.id,
                    name = type.name,
                    doubleDamageFrom = type.damage_relations.double_damage_from.map { it.name },
                    doubleDamageTo = type.damage_relations.double_damage_to.map { it.name },
                    halfDamageFrom = type.damage_relations.half_damage_from.map { it.name },
                    halfDamageTo = type.damage_relations.half_damage_to.map { it.name },
                    noDamageFrom = type.damage_relations.no_damage_from.map { it.name },
                    noDamageTo = type.damage_relations.no_damage_to.map { it.name },
            )
        }
    }

}
