package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.ChainLink
import com.sealstudios.pokemonApp.api.`object`.EvolutionDetails
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail.Companion.POKEMON_EVOLUTION_DETAIL_ID
import com.sealstudios.pokemonApp.util.extensions.getIdFromUrl
import org.jetbrains.annotations.NotNull

@Entity(indices = [Index(value = [POKEMON_EVOLUTION_DETAIL_ID])])
data class EvolutionDetail(
        @NotNull
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = EVOLUTION_DETAIL_ID)
        val id: Int = 0,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAIL_ID)
        val evolutionId: Int,
        @ColumnInfo(name = POKEMON_EVOLUTION_NAME)
        val evolutionName: String,
        @ColumnInfo(name = POKEMON_EVOLVES_FROM_NAME)
        val evolvesFromName: String?,
        @ColumnInfo(name = POKEMON_EVOLVES_FROM_ID)
        val evolvesFromId: Int?,
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
        val knownMoveType: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_LOCATION_ID)
        val locationId: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_LOCATION_NAME)
        val locationName: String?,
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
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_PARTY_SPECIES_ID)
        val partySpeciesId: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_PARTY_SPECIES_NAME)
        val partySpeciesName: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_PARTY_TYPE_ID)
        val partyTypeId: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_PARTY_TYPE_NAME)
        val partyTypeName: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_RELATIVE_PHYSICAL_STATS)
        val relativePhysicalStats: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TIME_OF_DAY)
        val timeOfDay: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TRADE_SPECIES_ID)
        val tradeSpeciesId: Int?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TRADE_SPECIES_NAME)
        val tradeSpeciesName: String?,
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_TURN_UPSIDE_DOWN)
        val turnUpsideDown: Boolean,
        @ColumnInfo(name = POKEMON_IS_BABY)
        val isBaby: Boolean,
) {

    companion object {

        const val EVOLUTION_DETAIL_ID: String = "evolution_detail_id"
        const val POKEMON_EVOLUTION_DETAIL_ID: String = "pokemon_evolution_detail_id"
        const val POKEMON_EVOLUTION_NAME: String = "pokemon_evolution_details_name"
        const val POKEMON_EVOLVES_FROM_ID: String = "pokemon_evolves_from_id"
        const val POKEMON_EVOLVES_FROM_NAME: String = "pokemon_evolves_from_name"
        const val POKEMON_EVOLUTION_DETAILS_ITEM_ID: String = "pokemon_evolution_details_item_id"
        const val POKEMON_EVOLUTION_DETAILS_ITEM_NAME: String = "pokemon_evolution_details_item_name"
        const val POKEMON_EVOLUTION_DETAILS_TRIGGER_ID: String = "pokemon_evolution_details_trigger_id"
        const val POKEMON_EVOLUTION_DETAILS_GENDER: String = "pokemon_evolution_details_gender"
        const val POKEMON_EVOLUTION_DETAILS_HELD_ITEM_ID: String = "pokemon_evolution_details_held_item_id"
        const val POKEMON_EVOLUTION_DETAILS_HELD_ITEM_NAME: String = "pokemon_evolution_details_held_item_name"
        const val POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE: String = "pokemon_evolution_details_known_move"
        const val POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE_NAME: String = "pokemon_evolution_details_known_move_name"
        const val POKEMON_EVOLUTION_DETAILS_KNOWN_MOVE_TYPE: String = "pokemon_evolution_details_known_move_type"
        const val POKEMON_EVOLUTION_DETAILS_LOCATION_ID: String = "pokemon_evolution_details_location_id"
        const val POKEMON_EVOLUTION_DETAILS_LOCATION_NAME: String = "pokemon_evolution_details_location_name"
        const val POKEMON_EVOLUTION_DETAILS_MIN_LEVEL: String = "pokemon_evolution_details_min_level"
        const val POKEMON_EVOLUTION_DETAILS_MIN_HAPPINESS: String = "pokemon_evolution_details_min_happiness"
        const val POKEMON_EVOLUTION_DETAILS_MIN_BEAUTY: String = "pokemon_evolution_details_min_beauty"
        const val POKEMON_EVOLUTION_DETAILS_MIN_AFFECTION: String = "pokemon_evolution_details_min_affection"
        const val POKEMON_EVOLUTION_DETAILS_NEEDS_OVER_WORLD_RAIN: String = "pokemon_evolution_details_needs_over_world_rain"
        const val POKEMON_EVOLUTION_DETAILS_PARTY_SPECIES_ID: String = "pokemon_evolution_details_party_species_id"
        const val POKEMON_EVOLUTION_DETAILS_PARTY_SPECIES_NAME: String = "pokemon_evolution_details_party_species_name"
        const val POKEMON_EVOLUTION_DETAILS_PARTY_TYPE_ID: String = "pokemon_evolution_details_party_type_id"
        const val POKEMON_EVOLUTION_DETAILS_PARTY_TYPE_NAME: String = "pokemon_evolution_details_party_type_name"
        const val POKEMON_EVOLUTION_DETAILS_RELATIVE_PHYSICAL_STATS: String = "pokemon_evolution_details_relative_physical_stats"
        const val POKEMON_EVOLUTION_DETAILS_TIME_OF_DAY: String = "pokemon_evolution_details_time_of_day"
        const val POKEMON_EVOLUTION_DETAILS_TRADE_SPECIES_ID: String = "pokemon_evolution_details_trade_species_id"
        const val POKEMON_EVOLUTION_DETAILS_TRADE_SPECIES_NAME: String = "pokemon_evolution_details_trade_species_name"
        const val POKEMON_EVOLUTION_DETAILS_TURN_UPSIDE_DOWN: String = "pokemon_evolution_details_turn_upside_down"
        const val POKEMON_IS_BABY: String = "is_baby"

        fun mapToPokemonEvolutionDetails(evolutionDetails: EvolutionDetails, evolvesFromChainLink: ChainLink, evolvesToChainLink: ChainLink) = EvolutionDetail(
                evolutionId = evolvesToChainLink.species?.url?.getIdFromUrl() ?: -1,
                evolutionName = evolvesToChainLink.species?.name ?: "",
                evolvesFromId = evolvesFromChainLink.species?.url?.getIdFromUrl() ?: -1,
                evolvesFromName = evolvesFromChainLink.species?.name ?: "",
                itemId = evolutionDetails.item?.url?.getIdFromUrl(),
                itemName = evolutionDetails.item?.name,
                triggerId = evolutionDetails.trigger?.url?.getIdFromUrl(),
                gender = evolutionDetails.gender,
                heldItem = evolutionDetails.held_item?.url?.getIdFromUrl(),
                heldItemName = evolutionDetails.held_item?.name,
                knownMove = evolutionDetails.known_move?.url?.getIdFromUrl(),
                knownMoveName = evolutionDetails.known_move?.name,
                knownMoveType = evolutionDetails.known_move_type?.name,
                locationId = evolutionDetails.location?.url?.getIdFromUrl(),
                locationName = evolutionDetails.location?.name,
                minLevel = evolutionDetails.min_level ?: -1,
                minHappiness = evolutionDetails.min_happiness ?: -1,
                minBeauty = evolutionDetails.min_beauty ?: -1,
                minAffection = evolutionDetails.min_affection ?: -1,
                needsOverWorldRain = evolutionDetails.needs_overworld_rain,
                partySpeciesId = evolutionDetails.party_species?.url?.getIdFromUrl(),
                partySpeciesName = evolutionDetails.party_species?.name,
                partyTypeId = evolutionDetails.party_type?.url?.getIdFromUrl(),
                partyTypeName = evolutionDetails.party_type?.name,
                relativePhysicalStats = evolutionDetails.relative_physical_stats ?: -1,
                timeOfDay = evolutionDetails.time_of_day,
                tradeSpeciesId = evolutionDetails.trade_species?.url?.getIdFromUrl(),
                tradeSpeciesName = evolutionDetails.trade_species?.name,
                turnUpsideDown = evolutionDetails.turn_upside_down,
                isBaby = evolvesFromChainLink.is_baby
        )

        fun getEvolutionDetailListForEvolutionChain(chainLink: ChainLink): List<EvolutionDetail> {
            return createEvolutionDetailListForEvolutionChain(chainLink, mutableListOf())
        }

        private fun createEvolutionDetailListForEvolutionChain(chainLink: ChainLink, detailList: MutableList<EvolutionDetail>): List<EvolutionDetail> {
            detailList.addAll(chainLink.evolves_to.map { evolvesToChainLink ->
                evolvesToChainLink.evolution_details.map { evolutionDetails ->
                    mapToPokemonEvolutionDetails(evolutionDetails, chainLink, evolvesToChainLink)
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
                "\nevolutionName=$evolutionName, " +
                "\nevolvesFromName=$evolvesFromName, " +
                "\nevolvesFromId=$evolvesFromId, " +
                "\nitemId=$itemId, " +
                "\nitemName=$itemName, " +
                "\ntriggerId=$triggerId, " +
                "\ngender=$gender, " +
                "\nheldItem=$heldItem, " +
                "\nheldItemName=$heldItemName, " +
                "\nknownMove=$knownMove, " +
                "\nknownMoveName=$knownMoveName, " +
                "\nknownMoveType=$knownMoveType, " +
                "\nlocationId=$locationId, " +
                "\nlocationName=$locationName, " +
                "\nminLevel=$minLevel, " +
                "\nminHappiness=$minHappiness, " +
                "\nminBeauty=$minBeauty, " +
                "\nminAffection=$minAffection, " +
                "\nneedsOverWorldRain=$needsOverWorldRain, " +
                "\npartySpeciesId=$partySpeciesId, " +
                "\npartySpeciesName=$partySpeciesName, " +
                "\npartyTypeId=$partyTypeId, " +
                "\npartyTypeName=$partyTypeName, " +
                "\nrelativePhysicalStats=$relativePhysicalStats, " +
                "\ntimeOfDay=$timeOfDay, " +
                "\ntradeSpeciesId=$tradeSpeciesId, " +
                "\ntradeSpeciesName=$tradeSpeciesName, " +
                "\nturnUpsideDown=$turnUpsideDown, " +
                "\nisBaby=$isBaby" +
                "\n)"
    }


}