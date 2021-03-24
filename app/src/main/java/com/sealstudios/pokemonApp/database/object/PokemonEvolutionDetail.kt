package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.ChainLink
import com.sealstudios.pokemonApp.api.`object`.EvolutionDetails
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionDetail.Companion.POKEMON_EVOLUTION_DETAILS_ID
import com.sealstudios.pokemonApp.util.extensions.getIdFromUrl
import org.jetbrains.annotations.NotNull

@Entity(indices = [Index(value = [POKEMON_EVOLUTION_DETAILS_ID])])
data class PokemonEvolutionDetail(
        @NotNull
        @PrimaryKey
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_ID)
        val id: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_ITEM_ID)
        val itemId: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TRIGGER_ID)
        val triggerId: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_GENDER)
        val gender: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_HELD_ITEM_ID)
        val heldItem: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_HELD_ITEM_NAME)
        val heldItemName: String,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE)
        val knownMove: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE_NAME)
        val knownMoveName: String,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE_TYPE)
        val knownMoveType: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_LOCATION)
        val location: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_MIN_LEVEL)
        val minLevel: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_MIN_HAPPINESS)
        val minHappiness: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_MIN_BEAUTY)
        val minBeauty: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_MIN_AFFECTION)
        val minAffection: String,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_NEEDS_OVER_WORLD_RAIN)
        val needsOverWorldRain: Boolean,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_PARTY_SPECIES)
        val partySpecies: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_PARTY_TYPE)
        val partyType: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_RELATIVE_PHYSICAL_STATS)
        val relativePhysicalStats: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TIME_OF_DAY)
        val timeOfDay: String,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TRADE_SPECIES)
        val tradeSpecies: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TURN_UPSIDE_DOWN)
        val turnUpsideDown: Boolean,
        @ColumnInfo(name = POKEMON_IS_BABY)
        val isBaby: Boolean,
) {
    companion object {

        const val POKEMON_EVOLUTION_DETAILS_ID: String = "pokemon_evolution_details_id"
        const val POKEMON_EVOLUTION_DETAILS_ITEM_ID: String = "pokemon_evolution_details_item_id"
        const val POKEMON_EVOLUTION_DETAILS_TRIGGER_ID: String = "pokemon_evolution_details_trigger_id"
        const val POKEMON_EVOLUTION_DETAILS_GENDER: String = "pokemon_evolution_details_gender"
        const val POKEMON_EVOLUTION_DETAILS_HELD_ITEM_ID: String = "pokemon_evolution_details_held_item_id"
        const val POKEMON_EVOLUTION_DETAILS_HELD_ITEM_NAME: String = "pokemon_evolution_details_held_item_name"
        const val POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE: String = "pokemon_evolution_details_known_move"
        const val POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE_NAME: String = "pokemon_evolution_details_known_move_name"
        const val POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE_TYPE: String = "pokemon_evolution_details_known_move_type"
        const val POKEMON_EVOLUTION_DETAILS_LOCATION: String = "pokemon_evolution_details_location"
        const val POKEMON_EVOLUTION_DETAILS_MIN_LEVEL: String = "pokemon_evolution_details_min_level"
        const val POKEMON_EVOLUTION_DETAILS_MIN_HAPPINESS: String = "pokemon_evolution_details_min_happiness"
        const val POKEMON_EVOLUTION_DETAILS_MIN_BEAUTY: String = "pokemon_evolution_details_min_beauty"
        const val POKEMON_EVOLUTION_DETAILS_MIN_AFFECTION: String = "pokemon_evolution_details_min_affection"
        const val POKEMON_EVOLUTION_DETAILS_NEEDS_OVER_WORLD_RAIN: String = "pokemon_evolution_details_needs_over_world_rain"
        const val POKEMON_EVOLUTION_DETAILS_PARTY_SPECIES: String = "pokemon_evolution_details_party_species"
        const val POKEMON_EVOLUTION_DETAILS_PARTY_TYPE: String = "pokemon_evolution_details_party_type"
        const val POKEMON_EVOLUTION_DETAILS_RELATIVE_PHYSICAL_STATS: String = "pokemon_evolution_details_relative_physical_stats"
        const val POKEMON_EVOLUTION_DETAILS_TIME_OF_DAY: String = "pokemon_evolution_details_time_of_day"
        const val POKEMON_EVOLUTION_DETAILS_TRADE_SPECIES: String = "pokemon_evolution_details_trade_species"
        const val POKEMON_EVOLUTION_DETAILS_TURN_UPSIDE_DOWN: String = "pokemon_evolution_details_turn_upside_down"
        const val POKEMON_IS_BABY: String = "is_baby"

        fun mapToPokemonEvolutionDetails(evolutionDetails: EvolutionDetails, chainLink: ChainLink) = PokemonEvolutionDetail(
                id = chainLink.species?.url?.getIdFromUrl() ?: -1,
                itemId = evolutionDetails.item?.url?.getIdFromUrl()  ?: -1,
                triggerId = evolutionDetails.trigger?.url?.getIdFromUrl()  ?: -1,
                gender = evolutionDetails.gender,
                heldItem = evolutionDetails.held_item?.url?.getIdFromUrl() ?: -1,
                heldItemName = evolutionDetails.held_item?.name ?: "",
                knownMove = evolutionDetails.known_move?.url?.getIdFromUrl() ?: -1,
                knownMoveName = evolutionDetails.known_move?.name ?: "",
                knownMoveType = evolutionDetails.known_move_type?.url?.getIdFromUrl() ?: -1,
                location = evolutionDetails.location?.url?.getIdFromUrl() ?: -1,
                minLevel = evolutionDetails.min_level,
                minHappiness = evolutionDetails.min_happiness,
                minBeauty = evolutionDetails.min_beauty,
                minAffection = evolutionDetails.min_affection ?: "",
                needsOverWorldRain = evolutionDetails.needs_overworld_rain,
                partySpecies = evolutionDetails.party_species?.url?.getIdFromUrl() ?: -1,
                partyType = evolutionDetails.party_type?.url?.getIdFromUrl() ?: -1,
                relativePhysicalStats = evolutionDetails.relative_physical_stats,
                timeOfDay = evolutionDetails.time_of_day ?: "",
                tradeSpecies = evolutionDetails.trade_species?.url?.getIdFromUrl() ?: -1,
                turnUpsideDown = evolutionDetails.turn_upside_down,
                isBaby = chainLink.is_baby
        )
    }
}