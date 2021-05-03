package com.sealstudios.pokemonApp.database.`object`

import androidx.room.*
import com.sealstudios.pokemonApp.api.`object`.PokemonGraphQlPokemon
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import org.jetbrains.annotations.NotNull


@TypeConverters(RoomIntListConverter::class)
@Entity(indices = [Index(value = [Pokemon.POKEMON_NAME])])
open class PokemonGraphQL constructor(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = POKEMON_ID)
    val id: Int,

    @ColumnInfo(name = POKEMON_NAME)
    val name: String,

    @ColumnInfo(name = POKEMON_SPECIES_ID)
    val speciesId: Int,

    @ColumnInfo(name = POKEMON_SPECIES_NAME)
    val speciesName: String,

    @ColumnInfo(name = POKEMON_TYPES)
    val types: List<String>,

    @ColumnInfo(name = POKEMON_TYPES_SLOTS)
    val typeSlots: List<Int>,

    ) {

    companion object {

        const val POKEMON_ID: String = "pokemon_id"
        const val POKEMON_NAME: String = "pokemon_name"
        const val POKEMON_SPECIES_ID: String = "pokemon_species_id"
        const val POKEMON_SPECIES_NAME: String = "pokemon_species_name"
        const val POKEMON_TYPES: String = "pokemon_types"
        const val POKEMON_TYPES_SLOTS: String = "pokemon_slots"

        fun mapToPokemonGraphQL(apiPokemon: PokemonGraphQlPokemon): PokemonGraphQL {
            return PokemonGraphQL(
                id = apiPokemon.id,
                name = apiPokemon.name,
                speciesName = apiPokemon.pokemon_v2_pokemonspecy.pokemon_v2_pokemonspeciesnames.firstOrNull()?.genus ?: "",
                speciesId = apiPokemon.pokemon_species_id,
                types = apiPokemon.pokemon_v2_pokemontypes.map { it.pokemon_v2_type.name },
                typeSlots = apiPokemon.pokemon_v2_pokemontypes.map { it.slot }
            )
        }

        fun pokemonImage(id: Int) =
            "https://firebasestorage.googleapis.com/v0/b/pokeapp-86eec.appspot.com/o/pokemon_image_$id.png?alt=media"

    }

    override fun toString(): String {
        return "PokemonGraphQL(id=$id, name='$name', speciesId=$speciesId, speciesName='$speciesName', types=$types, typeSlots=$typeSlots)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PokemonGraphQL) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (speciesId != other.speciesId) return false
        if (speciesName != other.speciesName) return false
        if (types != other.types) return false
        if (typeSlots != other.typeSlots) return false

        return true
    }


}
