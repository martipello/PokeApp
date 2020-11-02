package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.Description
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.getPokemonIdFromUrl
import org.jetbrains.annotations.NotNull

@Entity
data class PokemonSpecies(

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = SPECIES_ID)
    var id: Int,

    @ColumnInfo(name = GENDER_RATE)
    var gender_rate: Int,

    @ColumnInfo(name = CAPTURE_RATE)
    var capture_rate: Int,

    @ColumnInfo(name = BASE_HAPPINESS)
    var base_happiness: Int,

    @ColumnInfo(name = HATCH_COUNTER)
    var hatch_counter: Int,

    @ColumnInfo(name = EVOLUTION_CHAIN_ID)
    var evolution_chain_id: Int,

    @ColumnInfo(name = IS_BABY)
    var isBaby: Boolean,

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

        const val IS_BABY: String = "is_baby"
        const val EVOLUTION_CHAIN_ID: String = "evolution_chain_id"
        const val HATCH_COUNTER: String = "hatch_counter"
        const val CAPTURE_RATE: String = "capture_rate"
        const val BASE_HAPPINESS: String = "base_happiness"
        const val GENDER_RATE: String = "gender_rate"

        fun mapRemotePokemonSpeciesToDatabasePokemonSpecies(
            apiPokemonSpecies: com.sealstudios.pokemonApp.api.`object`.PokemonSpecies
        ): PokemonSpecies {
            val pokedexEntry = getPokedexEntry(apiPokemonSpecies)
            return PokemonSpecies(
                id = apiPokemonSpecies.id,
                species = getPokemonSpeciesNameFromGenus(apiPokemonSpecies),
                pokedex = apiPokemonSpecies.pokedex_numbers.map { it.pokedex.name }.first(),
                generation = apiPokemonSpecies.generation.name,
                pokedexEntry = pokedexEntry,
                habitat = apiPokemonSpecies.habitat?.name ?: "Unknown",
                shape = apiPokemonSpecies.shape.name,
                formDescription = getFormDescription(apiFormDescription = apiPokemonSpecies.form_descriptions),
                base_happiness = apiPokemonSpecies.base_happiness,
                capture_rate = apiPokemonSpecies.capture_rate,
                isBaby = apiPokemonSpecies.is_baby,
                gender_rate = apiPokemonSpecies.gender_rate,
                hatch_counter = apiPokemonSpecies.hatch_counter,
                evolution_chain_id = getPokemonIdFromUrl(apiPokemonSpecies.evolution_chain.url),
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