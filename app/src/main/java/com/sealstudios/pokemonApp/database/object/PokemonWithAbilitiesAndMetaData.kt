package com.sealstudios.pokemonApp.database.`object`

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility

data class PokemonWithAbilitiesAndMetaData(

    @Embedded
    val pokemon: Pokemon,
    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonAbility::class,
        entityColumn = PokemonAbility.ABILITY_ID,
        associateBy = Junction(
            value = PokemonAbilityJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonAbility.ABILITY_ID
        )
    )
    val abilities: List<PokemonAbility>,

    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonAbilityMetaData::class,
        entityColumn = PokemonAbilityMetaData.ABILITY_META_DATA_ID,
        associateBy = Junction(
            value = PokemonAbilityMetaDataJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonAbilityMetaData.ABILITY_META_DATA_ID
        )
    )
    val pokemonAbilityMetaData: List<PokemonAbilityMetaData>

)