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
            PokemonType(id = 0, name = "normal", slot = 1),
            PokemonType(id = 1, name = "water", slot = 1),
            PokemonType(id = 2, name = "fire", slot = 1),
            PokemonType(id = 3, name = "grass", slot = 1),
            PokemonType(id = 4, name = "electric", slot = 1),
            PokemonType(id = 5, name = "ice", slot = 1),
            PokemonType(id = 6, name = "fighting", slot = 1),
            PokemonType(id = 7, name = "poison", slot = 1),
            PokemonType(id = 8, name = "ground", slot = 1),
            PokemonType(id = 9, name = "flying", slot = 1),
            PokemonType(id = 10, name = "psychic", slot = 1),
            PokemonType(id = 11, name = "bug", slot = 1),
            PokemonType(id = 12, name = "rock", slot = 1),
            PokemonType(id = 13, name = "ghost", slot = 1),
            PokemonType(id = 14, name = "dark", slot = 1),
            PokemonType(id = 15, name = "dragon", slot = 1),
            PokemonType(id = 16, name = "steel", slot = 1),
            PokemonType(id = 17, name = "fairy", slot = 1),
            PokemonType(id = 18, name = "unknown", slot = 1),
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