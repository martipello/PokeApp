package com.sealstudios.pokemonApp.database.`object`

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.Description
import org.jetbrains.annotations.NotNull

@Entity
data class PokemonSpecies(

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = SPECIES_ID)
    var id: Int,

    @ColumnInfo(name = SPECIES_NAME)
    var species: String,

    @ColumnInfo(name = POKEDEX)
    var pokedex: String?,

    @ColumnInfo(name = POKEMON_GENERATION)
    var generation: String?,

    @ColumnInfo(name = POKEDEX_ENTRY)
    var pokedexEntry: String?,

    @ColumnInfo(name = HABITAT)
    var habitat: String?,

    @ColumnInfo(name = SHAPE)
    var shape: String?,

    @ColumnInfo(name = FORM_DESCRIPTION)
    var formDescription: String


) {

    companion object {
        const val SPECIES_ID: String = "species_id"
        const val SPECIES_NAME: String = "species_name"
        const val POKEDEX: String = "pokedex"
        const val HABITAT: String = "habitat"
        const val SHAPE: String = "species_shape"
        const val POKEDEX_ENTRY: String = "pokedex_entry"
        const val POKEMON_GENERATION: String = "pokemon_generation"
        const val FORM_DESCRIPTION: String = "form_description"

        fun mapRemotePokemonSpeciesToDatabasePokemonSpecies(
            apiPokemonSpecies: com.sealstudios.pokemonApp.api.`object`.PokemonSpecies
        ): PokemonSpecies {
            val pokedexEntry = getPokedexEntry(apiPokemonSpecies)
            Log.d("PS", apiPokemonSpecies.form_descriptions.toString())
            return PokemonSpecies(
                id = apiPokemonSpecies.id,
                species = getPokemonSpeciesNameFromGenus(apiPokemonSpecies),
                pokedex = apiPokemonSpecies.pokedex_numbers.map { it.pokedex.name }.first(),
                generation = apiPokemonSpecies.generation.name,
                pokedexEntry = pokedexEntry,
                habitat = apiPokemonSpecies.habitat?.name ?: "Unknown",
                shape = apiPokemonSpecies.shape.name,
                formDescription = getFormDescription(apiFormDescription = apiPokemonSpecies.form_descriptions)
            )
        }

        private fun getFormDescription(apiFormDescription: List<Description>): String {
            var formDescription = "No Form Data."
            for (entry in apiFormDescription) {
                if (entry.language.name == "en") {
                    formDescription = entry.description
                }
            }
            return formDescription
        }

        private fun getPokedexEntry(apiPokemonSpecies: com.sealstudios.pokemonApp.api.`object`.PokemonSpecies): String {
            var pokedexEntry = "No Pokedex Data."
            for (entry in apiPokemonSpecies.flavor_text_entries) {
                if (entry.language.name == "en") {
                    pokedexEntry = entry.flavor_text
                }
            }
            return pokedexEntry
        }

        private fun getPokemonSpeciesNameFromGenus(apiPokemonSpecies: com.sealstudios.pokemonApp.api.`object`.PokemonSpecies): String {
            var pokemonSpecies = "Species Unknown."
            for (entry in apiPokemonSpecies.genera) {
                if (entry.language.name == "en") {
                    pokemonSpecies = entry.genus
                }
            }
            return pokemonSpecies
        }

    }

}