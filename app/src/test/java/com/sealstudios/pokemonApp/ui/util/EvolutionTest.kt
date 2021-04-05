package com.sealstudios.pokemonApp.ui.util

import com.google.common.truth.Truth.assertThat
import com.sealstudios.pokemonApp.api.`object`.ChainLink
import com.sealstudios.pokemonApp.api.`object`.EvolutionDetails
import com.sealstudios.pokemonApp.api.`object`.NamedApiResource
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail.Companion.getEvolutionDetailListForEvolutionChain
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail.Companion.pokemonIdsInEvolutionChain
import com.sealstudios.pokemonApp.database.`object`.EvolutionTrigger
import com.sealstudios.pokemonApp.database.`object`.Gender
import com.sealstudios.pokemonApp.database.`object`.TimeOfDay
import com.sealstudios.pokemonApp.util.extensions.getIdFromUrl
import org.junit.Test


class EvolutionTest {

    private val bulbasaurSpecies = NamedApiResource(
            name = "bulbasaur",
            url = "https://pokeapi.co/api/v2/pokemon-species/1/",
            category = "",
            id = 0)

    private val ivysaurSpecies = NamedApiResource(
            name = "ivysaur",
            url = "https://pokeapi.co/api/v2/pokemon-species/2/",
            category = "",
            id = 0)

    private val venusaurSpecies = NamedApiResource(
            name = "venusaur",
            url = "https://pokeapi.co/api/v2/pokemon-species/3/",
            category = "",
            id = 0)


    private val eeveeSpecies = NamedApiResource(
            name = "eevee",
            url = "https://pokeapi.co/api/v2/pokemon-species/133/",
            category = "",
            id = 0)

    private val vaporeonSpecies = NamedApiResource(
            name = "vaporeon",
            url = "https://pokeapi.co/api/v2/pokemon-species/134/",
            category = "",
            id = 0)

    private val jolteonSpecies = NamedApiResource(
            name = "jolteon",
            url = "https://pokeapi.co/api/v2/pokemon-species/135/",
            category = "",
            id = 0)

    private val flareonSpecies = NamedApiResource(
            name = "flareon",
            url = "https://pokeapi.co/api/v2/pokemon-species/136/",
            category = "",
            id = 0)

    private val espeonSpecies = NamedApiResource(
            name = "espeon",
            url = "https://pokeapi.co/api/v2/pokemon-species/196/",
            category = "",
            id = 0)

    private val umbreonSpecies = NamedApiResource(
            name = "umbreon",
            url = "https://pokeapi.co/api/v2/pokemon-species/197/",
            category = "",
            id = 0)

    private val leafeonSpecies = NamedApiResource(
            name = "leafeon",
            url = "https://pokeapi.co/api/v2/pokemon-species/470/",
            category = "",
            id = 0)

    private val glacionSpecies = NamedApiResource(
            name = "glacion",
            url = "https://pokeapi.co/api/v2/pokemon-species/471/",
            category = "",
            id = 0)

    private val sylveonSpecies = NamedApiResource(
            name = "sylveon",
            url = "https://pokeapi.co/api/v2/pokemon-species/700/",
            category = "",
            id = 0)


    private val ivysaurEvolutionDetails = evolutionByLevelUpDetails(16)
    private val venusaurEvolutionDetails = evolutionByLevelUpDetails(32)

    private val venusaurChainLink = ChainLink(is_baby = false, species = venusaurSpecies, evolves_to = listOf(), evolution_details = listOf(venusaurEvolutionDetails))
    private val ivysaurChainLink = ChainLink(is_baby = false, species = ivysaurSpecies, evolves_to = listOf(venusaurChainLink), evolution_details = listOf(ivysaurEvolutionDetails))
    private val bulbasaurChainLink = ChainLink(is_baby = false, species = bulbasaurSpecies, evolves_to = listOf(ivysaurChainLink), evolution_details = listOf())


    private val vaporeonEvolutionDetail = evolutionByItemDetails("water-stone", 84, 3)
    private val jolteonEvolutionDetail = evolutionByItemDetails("electric-stone", 83, 3)
    private val flareonEvolutionDetail = evolutionByItemDetails("fire-stone", 82, 3)
    private val espeonEvolutionDetail = evolutionByTimeOfDayDetails(TimeOfDay.DAY)
    private val umbreonEvolutionDetail = evolutionByTimeOfDayDetails(TimeOfDay.NIGHT)
    private val leafeonEvolutionDetail1 = evolutionByLocation("eterna-forest", 8)
    private val leafeonEvolutionDetail2 = evolutionByLocation("pinwheel-forest", 375)
    private val leafeonEvolutionDetail3 = evolutionByLocation("kalos-route-20", 650)
    private val leafeonEvolutionDetail4 = evolutionByItemDetails("leaf-stone", 85, 3)
    private val glaceonEvolutionDetail1 = evolutionByLocation("sinnoh-route-217", 48)
    private val glaceonEvolutionDetail2 = evolutionByLocation("twist-mountain", 380)
    private val glaceonEvolutionDetail3 = evolutionByLocation("frost-cavern", 640)
    private val glaceonEvolutionDetail4 = evolutionByItemDetails("ice-stone", 885, 3)
    private val sylveonEvolutionDetail = evolutionByKnownMoveDetails("fairy", 18)

    private val vaporeonChainLink = ChainLink(is_baby = false, species = vaporeonSpecies, evolves_to = listOf(), evolution_details = listOf(vaporeonEvolutionDetail))
    private val flareonChainLink = ChainLink(is_baby = false, species = flareonSpecies, evolves_to = listOf(), evolution_details = listOf(flareonEvolutionDetail))
    private val jolteonChainLink = ChainLink(is_baby = false, species = jolteonSpecies, evolves_to = listOf(), evolution_details = listOf(jolteonEvolutionDetail))
    private val espeonChainLink = ChainLink(is_baby = false, species = espeonSpecies, evolves_to = listOf(), evolution_details = listOf(espeonEvolutionDetail))
    private val umbreonChainLink = ChainLink(is_baby = false, species = umbreonSpecies, evolves_to = listOf(), evolution_details = listOf(umbreonEvolutionDetail))
    private val leafeonChainLink = ChainLink(is_baby = false, species = leafeonSpecies, evolves_to = listOf(), evolution_details = listOf(
            leafeonEvolutionDetail1,
            leafeonEvolutionDetail2,
            leafeonEvolutionDetail3,
            leafeonEvolutionDetail4,
    ))
    private val glaceonChainLink = ChainLink(is_baby = false, species = glacionSpecies, evolves_to = listOf(), evolution_details = listOf(
            glaceonEvolutionDetail1,
            glaceonEvolutionDetail2,
            glaceonEvolutionDetail3,
            glaceonEvolutionDetail4,
    ))
    private val sylveonChainLink = ChainLink(is_baby = false, species = sylveonSpecies, evolves_to = listOf(), evolution_details = listOf(sylveonEvolutionDetail))

    private val eeveeChainLink = ChainLink(is_baby = false, species = eeveeSpecies, evolves_to = listOf(
            vaporeonChainLink,
            flareonChainLink,
            jolteonChainLink,
            espeonChainLink,
            umbreonChainLink,
            leafeonChainLink,
            glaceonChainLink,
            sylveonChainLink,
    ), evolution_details = listOf())

    @Test
    fun checkIdsForBulbasaurEvolutions() {
        val pokemonIdsInEvolutionChain = pokemonIdsInEvolutionChain(bulbasaurChainLink, mutableListOf())
        val expectedListIds = listOf(1, 2, 3)
        assertThat(pokemonIdsInEvolutionChain).containsNoDuplicates()
        assertThat(pokemonIdsInEvolutionChain).hasSize(3)
        assertThat(pokemonIdsInEvolutionChain).containsExactlyElementsIn(expectedListIds)
    }

    @Test
    fun checkIdsForEeveeEvolutions() {
        val pokemonIdsInEvolutionChain = pokemonIdsInEvolutionChain(eeveeChainLink, mutableListOf())
        val expectedListIds = listOf(133, 134, 135, 136, 196, 197, 470, 471, 700)
        assertThat(pokemonIdsInEvolutionChain).containsNoDuplicates()
        assertThat(pokemonIdsInEvolutionChain).hasSize(9)
        assertThat(pokemonIdsInEvolutionChain).containsExactlyElementsIn(expectedListIds)
    }

    @Test
    fun checkDetailsForEeveeEvolutions() {
        val pokemonDetailsInEvolutionChain = getEvolutionDetailListForEvolutionChain(eeveeChainLink)
        val expectedPokemonDetailsInEvolutionChain = listOf(
                EvolutionDetail.mapToPokemonEvolutionDetails(vaporeonEvolutionDetail, eeveeChainLink, vaporeonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(flareonEvolutionDetail, eeveeChainLink, flareonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(jolteonEvolutionDetail, eeveeChainLink, jolteonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(espeonEvolutionDetail, eeveeChainLink, espeonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(umbreonEvolutionDetail, eeveeChainLink, umbreonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(leafeonEvolutionDetail1, eeveeChainLink, leafeonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(leafeonEvolutionDetail2, eeveeChainLink, leafeonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(leafeonEvolutionDetail3, eeveeChainLink, leafeonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(leafeonEvolutionDetail4, eeveeChainLink, leafeonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(glaceonEvolutionDetail1, eeveeChainLink, glaceonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(glaceonEvolutionDetail2, eeveeChainLink, glaceonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(glaceonEvolutionDetail3, eeveeChainLink, glaceonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(glaceonEvolutionDetail4, eeveeChainLink, glaceonChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(sylveonEvolutionDetail, eeveeChainLink, sylveonChainLink),
        )
        assertThat(pokemonDetailsInEvolutionChain).containsNoDuplicates()
        assertThat(pokemonDetailsInEvolutionChain).hasSize(14)
        assertThat(pokemonDetailsInEvolutionChain).containsExactlyElementsIn(expectedPokemonDetailsInEvolutionChain)
        pokemonDetailsInEvolutionChain.forEach {
            assertThat(it.evolvesFrom).isEqualTo(eeveeChainLink.species?.url?.getIdFromUrl())
        }
    }

    @Test
    fun checkDetailsForBulbasaurEvolutions() {
        val pokemonDetailsInEvolutionChain = getEvolutionDetailListForEvolutionChain(bulbasaurChainLink)
        val expectedPokemonDetailsInEvolutionChain = listOf(
                EvolutionDetail.mapToPokemonEvolutionDetails(ivysaurEvolutionDetails, bulbasaurChainLink, ivysaurChainLink),
                EvolutionDetail.mapToPokemonEvolutionDetails(venusaurEvolutionDetails, ivysaurChainLink, venusaurChainLink),
        )
        assertThat(pokemonDetailsInEvolutionChain).containsNoDuplicates()
        assertThat(pokemonDetailsInEvolutionChain).hasSize(2)
        assertThat(pokemonDetailsInEvolutionChain).containsExactlyElementsIn(expectedPokemonDetailsInEvolutionChain)

        // This proves the data shoes that ivysaur - pokemonDetailsInEvolutionChain[0] evolves from bulbasaur
        assertThat(pokemonDetailsInEvolutionChain[0].evolvesFrom).isEqualTo(bulbasaurChainLink.species?.url?.getIdFromUrl())
        // This proves the data shoes that venusaur - pokemonDetailsInEvolutionChain[1] evolves from ivysaur
        assertThat(pokemonDetailsInEvolutionChain[1].evolvesFrom).isEqualTo(ivysaurChainLink.species?.url?.getIdFromUrl())
    }

    private fun evolutionByItemDetails(itemName: String, itemId: Int?, triggerId: Int): EvolutionDetails {
        return EvolutionDetails(
                item = NamedApiResource(name = itemName, "", 0, url = "https://pokeapi.co/api/v2/item/$itemId/"),
                trigger = NamedApiResource(name = EvolutionTrigger.USE_ITEM.name, "", triggerId, url = "https://pokeapi.co/api/v2/evolution_trigger/$triggerId/"),
                gender = Gender.MALE.id,
                held_item = null,
                known_move = null,
                known_move_type = null,
                location = null,
                min_level = 0,
                min_happiness = 0,
                min_beauty = 0,
                min_affection = 0,
                needs_overworld_rain = false,
                party_species = null,
                party_type = null,
                relative_physical_stats = 0,
                time_of_day = "",
                trade_species = null,
                turn_upside_down = false
        )
    }

    private fun evolutionByLevelUpDetails(minLevel: Int): EvolutionDetails {
        val trigger = EvolutionTrigger.LEVEL_UP
        return EvolutionDetails(
                item = null,
                trigger = NamedApiResource(name = trigger.name, "", trigger.id, url = "https://pokeapi.co/api/v2/evolution_trigger/${trigger.id}/"),
                gender = Gender.MALE.id,
                held_item = null,
                known_move = null,
                known_move_type = null,
                location = null,
                min_level = minLevel,
                min_happiness = 0,
                min_beauty = 0,
                min_affection = 0,
                needs_overworld_rain = false,
                party_species = null,
                party_type = null,
                relative_physical_stats = 0,
                time_of_day = "",
                trade_species = null,
                turn_upside_down = false
        )
    }

    private fun evolutionByTimeOfDayDetails(timeOfDay: TimeOfDay): EvolutionDetails {
        val trigger = EvolutionTrigger.LEVEL_UP
        return EvolutionDetails(
                item = null,
                trigger = NamedApiResource(name = trigger.name, "", trigger.id, url = "https://pokeapi.co/api/v2/evolution_trigger/${trigger.id}/"),
                gender = Gender.MALE.id,
                held_item = null,
                known_move = null,
                known_move_type = null,
                location = null,
                min_level = 0,
                min_happiness = 220,
                min_beauty = 0,
                min_affection = 0,
                needs_overworld_rain = false,
                party_species = null,
                party_type = null,
                relative_physical_stats = 0,
                time_of_day = timeOfDay.name,
                trade_species = null,
                turn_upside_down = false
        )
    }

    private fun evolutionByKnownMoveDetails(knownMoveTypeName: String, knownMoveTypeId: Int): EvolutionDetails {
        val trigger = EvolutionTrigger.LEVEL_UP
        return EvolutionDetails(
                item = null,
                trigger = NamedApiResource(name = trigger.name, "", trigger.id, url = "https://pokeapi.co/api/v2/evolution_trigger/${trigger.id}/"),
                gender = Gender.MALE.id,
                held_item = null,
                known_move = null,
                known_move_type = NamedApiResource(name = knownMoveTypeName, url = "https://pokeapi.co/api/v2/type/$knownMoveTypeId/", category = "", id = 0),
                location = null,
                min_level = 0,
                min_happiness = 0,
                min_beauty = 0,
                min_affection = 2,
                needs_overworld_rain = false,
                party_species = null,
                party_type = null,
                relative_physical_stats = 0,
                time_of_day = "",
                trade_species = null,
                turn_upside_down = false
        )
    }

    private fun evolutionByLocation(locationName: String, locationId: Int): EvolutionDetails {
        val trigger = EvolutionTrigger.LEVEL_UP
        return EvolutionDetails(
                item = null,
                trigger = NamedApiResource(name = trigger.name, "", trigger.id, url = "https://pokeapi.co/api/v2/evolution_trigger/${trigger.id}/"),
                gender = Gender.MALE.id,
                held_item = null,
                known_move = null,
                known_move_type = null,
                location = NamedApiResource(name = locationName, "", locationId, url = "https://pokeapi.co/api/v2/location/$locationId/"),
                min_level = 0,
                min_happiness = 0,
                min_beauty = 0,
                min_affection = 0,
                needs_overworld_rain = false,
                party_species = null,
                party_type = null,
                relative_physical_stats = 0,
                time_of_day = "",
                trade_species = null,
                turn_upside_down = false
        )
    }

}