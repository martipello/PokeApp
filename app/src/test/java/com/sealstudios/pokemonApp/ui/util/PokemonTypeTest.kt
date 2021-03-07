package com.sealstudios.pokemonApp.ui.util

import com.sealstudios.pokemonApp.database.`object`.PokemonType
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import com.sealstudios.pokemonApp.ui.util.PokemonType as pokemonTypeEnum

class PokemonTypeTest {

    @Test
    fun getEnumTypeForPokemonType() {

        val pokemonTypes = listOf(
            PokemonType(id = 0, name = "normal"),
            PokemonType(id = 1, name = "water"),
            PokemonType(id = 2, name = "fire"),
            PokemonType(id = 3, name = "grass"),
            PokemonType(id = 4, name = "electric"),
            PokemonType(id = 5, name = "ice"),
            PokemonType(id = 6, name = "fighting"),
            PokemonType(id = 7, name = "poison"),
            PokemonType(id = 8, name = "ground"),
            PokemonType(id = 9, name = "flying"),
            PokemonType(id = 10, name = "psychic"),
            PokemonType(id = 11, name = "bug"),
            PokemonType(id = 12, name = "rock"),
            PokemonType(id = 13, name = "ghost"),
            PokemonType(id = 14, name = "dark"),
            PokemonType(id = 15, name = "dragon"),
            PokemonType(id = 16, name = "steel"),
            PokemonType(id = 17, name = "fairy"),
            PokemonType(id = 18, name = "unknown"),
        )

        val pokemonTypeEnums = pokemonTypeEnum.getAllPokemonTypes()

        for (index in pokemonTypes.indices) {
            assertThat(
                pokemonTypeEnums[index],
                equalTo(pokemonTypeEnum.getPokemonEnumTypeForPokemonType(type = pokemonTypes[index].name))
            )
        }
    }
}