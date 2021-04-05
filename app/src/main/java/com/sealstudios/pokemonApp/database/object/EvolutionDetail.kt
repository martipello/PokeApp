package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.ChainLink
import com.sealstudios.pokemonApp.api.`object`.EvolutionDetails
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail.Companion.POKEMON_EVOLUTION_DETAILS_ID
import com.sealstudios.pokemonApp.util.extensions.getIdFromUrl
import org.jetbrains.annotations.NotNull

@Entity(indices = [Index(value = [POKEMON_EVOLUTION_DETAILS_ID])])
data class EvolutionDetail(
        @NotNull
        @PrimaryKey
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_ID)
        val id: Int,
        @ColumnInfo(name = POKEMON_EVOLVES_FROM)
        val evolvesFrom: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_ITEM_ID)
        val itemId: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_ITEM_NAME)
        val itemName: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TRIGGER_ID)
        val triggerId: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_GENDER)
        val gender: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_HELD_ITEM_ID)
        val heldItem: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_HELD_ITEM_NAME)
        val heldItemName: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE)
        val knownMove: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE_NAME)
        val knownMoveName: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE_TYPE)
        val knownMoveType: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_LOCATION)
        val location: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_MIN_LEVEL)
        val minLevel: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_MIN_HAPPINESS)
        val minHappiness: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_MIN_BEAUTY)
        val minBeauty: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_MIN_AFFECTION)
        val minAffection: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_NEEDS_OVER_WORLD_RAIN)
        val needsOverWorldRain: Boolean,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_PARTY_SPECIES)
        val partySpecies: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_PARTY_TYPE)
        val partyType: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_RELATIVE_PHYSICAL_STATS)
        val relativePhysicalStats: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TIME_OF_DAY)
        val timeOfDay: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TRADE_SPECIES)
        val tradeSpecies: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TURN_UPSIDE_DOWN)
        val turnUpsideDown: Boolean,
        @ColumnInfo(name = POKEMON_IS_BABY)
        val isBaby: Boolean,
) {


    companion object {

        const val POKEMON_EVOLUTION_DETAILS_ID: String = "pokemon_evolution_details_id"
        const val POKEMON_EVOLVES_FROM: String = "pokemon_evolves_from"
        const val POKEMON_EVOLUTION_DETAILS_ITEM_ID: String = "pokemon_evolution_details_item_id"
        const val POKEMON_EVOLUTION_DETAILS_ITEM_NAME: String = "pokemon_evolution_details_item_name"
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

        fun mapToPokemonEvolutionDetails(evolutionDetails: EvolutionDetails, chainLink: ChainLink, evolvesToChainLink: ChainLink) = EvolutionDetail(
                id = evolvesToChainLink.species?.url?.getIdFromUrl() ?: -1,
                evolvesFrom = chainLink.species?.url?.getIdFromUrl() ?: -1,
                itemId = evolutionDetails.item?.url?.getIdFromUrl(),
                itemName = evolutionDetails.item?.name,
                triggerId = evolutionDetails.trigger?.url?.getIdFromUrl(),
                gender = evolutionDetails.gender,
                heldItem = evolutionDetails.held_item?.url?.getIdFromUrl(),
                heldItemName = evolutionDetails.held_item?.name,
                knownMove = evolutionDetails.known_move?.url?.getIdFromUrl(),
                knownMoveName = evolutionDetails.known_move?.name,
                knownMoveType = evolutionDetails.known_move_type?.url?.getIdFromUrl(),
                location = evolutionDetails.location?.url?.getIdFromUrl(),
                minLevel = evolutionDetails.min_level,
                minHappiness = evolutionDetails.min_happiness,
                minBeauty = evolutionDetails.min_beauty,
                minAffection = evolutionDetails.min_affection,
                needsOverWorldRain = evolutionDetails.needs_overworld_rain,
                partySpecies = evolutionDetails.party_species?.url?.getIdFromUrl(),
                partyType = evolutionDetails.party_type?.url?.getIdFromUrl(),
                relativePhysicalStats = evolutionDetails.relative_physical_stats,
                timeOfDay = evolutionDetails.time_of_day,
                tradeSpecies = evolutionDetails.trade_species?.url?.getIdFromUrl(),
                turnUpsideDown = evolutionDetails.turn_upside_down,
                isBaby = chainLink.is_baby
        )

        fun getEvolutionDetailListForEvolutionChain(chainLink: ChainLink): List<EvolutionDetail> {
            return createEvolutionDetailListForEvolutionChain(chainLink, mutableListOf())
        }

        private fun createEvolutionDetailListForEvolutionChain(chainLink: ChainLink, detailList: MutableList<EvolutionDetail>): List<EvolutionDetail> {
            detailList.addAll(chainLink.evolves_to.map { thisChainLink ->
                thisChainLink.evolution_details.map { evolutionDetails ->
                    mapToPokemonEvolutionDetails(evolutionDetails, chainLink, thisChainLink)
                }
            }.flatten())

            while (chainLink.evolves_to.isNotEmpty()) {
                return chainLink.evolves_to.map {
                    return createEvolutionDetailListForEvolutionChain(it, detailList)
                }
            }
            return detailList.toSet().toList()
        }

        fun pokemonIdsInEvolutionChain(chainLink: ChainLink, idList: MutableList<Int>): List<Int> {
            idList.add(chainLink.species?.url?.getIdFromUrl() ?: -1)
            idList.addAll(chainLink.evolves_to.map { it.species?.url?.getIdFromUrl() ?: -1 })
            while (chainLink.evolves_to.isNotEmpty()) {
                return chainLink.evolves_to.map {
                    return pokemonIdsInEvolutionChain(it, idList)
                }
            }
            return idList.toSet().toList()
        }

    }

    override fun toString(): String {
        return "EvolutionDetail(" +
                "\nid=$id, " +
                "\nevolvesFrom=$evolvesFrom, " +
                "\nitemId=$itemId, " +
                "\nitemName=$itemName, " +
                "\ntriggerId=$triggerId, " +
                "\ngender=$gender, " +
                "\nheldItem=$heldItem, " +
                "\nheldItemName=$heldItemName, " +
                "\nknownMove=$knownMove, " +
                "\nknownMoveName=$knownMoveName, " +
                "\nknownMoveType=$knownMoveType, " +
                "\nlocation=$location, " +
                "\nminLevel=$minLevel, " +
                "\nminHappiness=$minHappiness, " +
                "\nminBeauty=$minBeauty, " +
                "\nminAffection=$minAffection, " +
                "\nneedsOverWorldRain=$needsOverWorldRain, " +
                "\npartySpecies=$partySpecies, " +
                "\npartyType=$partyType, " +
                "\nrelativePhysicalStats=$relativePhysicalStats, " +
                "\ntimeOfDay=$timeOfDay, " +
                "\ntradeSpecies=$tradeSpecies, " +
                "\nturnUpsideDown=$turnUpsideDown, " +
                "\nisBaby=$isBaby" +
                "\n)"
    }
}