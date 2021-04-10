package com.sealstudios.pokemonApp.ui.util

import com.sealstudios.pokemonApp.database.`object`.Type
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import com.sealstudios.pokemonApp.ui.util.PokemonType as pokemonTypeEnum

class TypeTest {

    @Test
    fun getEnumTypeForPokemonType() {

        val pokemonTypes = listOf(
            Type(id = 0, name = "normal"),
            Type(id = 1, name = "water"),
            Type(id = 2, name = "fire"),
            Type(id = 3, name = "grass"),
            Type(id = 4, name = "electric"),
            Type(id = 5, name = "ice"),
            Type(id = 6, name = "fighting"),
            Type(id = 7, name = "poison"),
            Type(id = 8, name = "ground"),
            Type(id = 9, name = "flying"),
            Type(id = 10, name = "psychic"),
            Type(id = 11, name = "bug"),
            Type(id = 12, name = "rock"),
            Type(id = 13, name = "ghost"),
            Type(id = 14, name = "dark"),
            Type(id = 15, name = "dragon"),
            Type(id = 16, name = "steel"),
            Type(id = 17, name = "fairy"),
            Type(id = 18, name = "unknown"),
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