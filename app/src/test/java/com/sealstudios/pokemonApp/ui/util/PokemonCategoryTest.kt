package com.sealstudios.pokemonApp.ui.util

import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.*
import org.junit.Test

class PokemonCategoryTest {
    @Test
    fun getCategoryForDamageClass() {
        val damageClassPhysical = "physical"
        val damageClassSpecial = "special"
        val damageClassStatus = "status"
        val damageClassUnknown = "unknown"

        assertThat(
            PokemonCategory.getCategoryForDamageClass(damageClassPhysical),
            equalTo(PokemonCategory.PHYSICAL)
        )
        assertThat(
            PokemonCategory.getCategoryForDamageClass(damageClassSpecial),
            equalTo(PokemonCategory.SPECIAL)
        )
        assertThat(
            PokemonCategory.getCategoryForDamageClass(damageClassStatus),
            equalTo(PokemonCategory.STATUS)
        )
        assertThat(
            PokemonCategory.getCategoryForDamageClass(damageClassUnknown),
            equalTo(PokemonCategory.UNKNOWN)
        )

    }
}