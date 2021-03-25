package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.Ability
import com.sealstudios.pokemonApp.database.`object`.AbilityMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.AbilityJoin
import com.sealstudios.pokemonApp.database.`object`.joins.AbilityMetaDataJoin

data class PokemonWithAbilitiesAndMetaData(

        @Embedded
        val pokemon: Pokemon,
        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = Ability::class,
                entityColumn = Ability.ABILITY_ID,
                associateBy = Junction(
                        value = AbilityJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = Ability.ABILITY_ID
                )
        )
        val abilities: List<Ability>,

        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = AbilityMetaData::class,
                entityColumn = AbilityMetaData.ABILITY_META_DATA_ID,
                associateBy = Junction(
                        value = AbilityMetaDataJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = AbilityMetaData.ABILITY_META_DATA_ID
                )
        )
        val abilityMetaData: List<AbilityMetaData>

)