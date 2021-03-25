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
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAIL_ID_LIST)
        val evolutionDetailIdList: List<Int>,
) {
    companion object {

        const val POKEMON_EVOLUTION_CHAIN_ID: String = "pokemon_evolution_chain_id"
        const val BABY_TRIGGER_ITEM_ID: String = "baby_trigger_item_id"
        const val BABY_TRIGGER_ITEM_NAME: String = "baby_trigger_item_name"
        const val POKEMON_EVOLUTION_DETAIL_ID_LIST: String = "evolution_detail_id_list"

        fun mapToEvolutionChain(evolutionChain: EvolutionChain) = EvolutionChain(
                id = evolutionChain.id,
                babyTriggerItemName = evolutionChain.baby_trigger_item?.name ?: "",
                babyTriggerItemId = evolutionChain.baby_trigger_item?.url?.getIdFromUrl() ?: 0,
                evolutionDetailIdList = evolvesToChainLinkIdList(
                        evolutionChain.chain,
                        mutableListOf())
        )

        private fun evolvesToChainLinkIdList(chainLink: ChainLink, idList: MutableList<Int>): List<Int> {
            while (chainLink.evolves_to.isNotEmpty()) {
                idList.add(chainLink.evolves_to.first().species?.url?.getIdFromUrl() ?: -1)
                return evolvesToChainLinkIdList(chainLink.evolves_to.first(), idList)
            }
            return idList
        }

    }
}
