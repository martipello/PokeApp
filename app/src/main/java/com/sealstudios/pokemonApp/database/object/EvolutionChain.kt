package com.sealstudios.pokemonApp.database.`object`

import androidx.room.*
import com.sealstudios.pokemonApp.api.`object`.ChainLink
import com.sealstudios.pokemonApp.api.`object`.EvolutionChain
import com.sealstudios.pokemonApp.database.`object`.EvolutionChain.Companion.POKEMON_EVOLUTION_CHAIN_ID
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import com.sealstudios.pokemonApp.util.extensions.getIdFromUrl
import org.jetbrains.annotations.NotNull

@TypeConverters(RoomIntListConverter::class)
@Entity(indices = [Index(value = [POKEMON_EVOLUTION_CHAIN_ID])])
data class EvolutionChain(
        @NotNull
        @PrimaryKey
        @ColumnInfo(name = POKEMON_EVOLUTION_CHAIN_ID)
        val id: Int,
        @ColumnInfo(name = BABY_TRIGGER_ITEM_NAME)
        val babyTriggerItemName: String,
        @ColumnInfo(name = BABY_TRIGGER_ITEM_ID)
        val babyTriggerItemId: Int,
        @ColumnInfo(name = POKEMON_IDS_IN_EVOLUTION_CHAIN)
        val pokemonIdsInEvolutionChain: List<Int>,
) {

    companion object {

        const val POKEMON_EVOLUTION_CHAIN_ID: String = "pokemon_evolution_chain_id"
        const val BABY_TRIGGER_ITEM_ID: String = "baby_trigger_item_id"
        const val BABY_TRIGGER_ITEM_NAME: String = "baby_trigger_item_name"
        const val POKEMON_IDS_IN_EVOLUTION_CHAIN: String = "pokemon_ids_in_evolution_chain"

        fun mapToEvolutionChain(evolutionChain: EvolutionChain) = EvolutionChain(
                id = evolutionChain.id,
                babyTriggerItemName = evolutionChain.baby_trigger_item?.name ?: "",
                babyTriggerItemId = evolutionChain.baby_trigger_item?.url?.getIdFromUrl() ?: 0,
                pokemonIdsInEvolutionChain = pokemonIdsInEvolutionChain(
                        evolutionChain.chain,
                        mutableListOf())
        )

        private fun pokemonIdsInEvolutionChain(chainLink: ChainLink, idList: MutableList<Int>): List<Int> {
            idList.add(chainLink.species?.url?.getIdFromUrl() ?: -1)
            chainLink.evolves_to.map { idList.add(it.species?.url?.getIdFromUrl() ?: -1) }
            while (chainLink.evolves_to.isNotEmpty()) {
                return chainLink.evolves_to.map {
                    return pokemonIdsInEvolutionChain(it, idList)
                }
            }
            return idList.toSet().toList()
        }

    }

    override fun toString(): String {
        return "EvolutionChain(\nid=$id, \nbabyTriggerItemName='$babyTriggerItemName', \nbabyTriggerItemId=$babyTriggerItemId, \npokemonIdsInEvolutionChain=$pokemonIdsInEvolutionChain\n)"
    }
}
