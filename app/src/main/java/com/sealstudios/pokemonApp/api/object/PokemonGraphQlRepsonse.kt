package com.sealstudios.pokemonApp.api.`object`

data class PokemonGraphQlResponse (
    val data : List<PokemonGraphQlPokemon>
)

data class PokemonGraphQlPokemon (
    val id: Int,
    val name: String,
    val pokemon_species_id: Int,
    val pokemon_v2_pokemontypes: List<PokemonGraphQlPokemonType>,
    val pokemon_v2_pokemonspecy: PokemonGraphQlPokemonSpecies
)

data class PokemonGraphQlPokemonType(
    val type_id: Int,
    val slot: Int,
    val pokemon_v2_type: PokemonGraphQlPokemonTypeName
)

data class PokemonGraphQlPokemonTypeName(
    val name: String
)

data class PokemonGraphQlPokemonSpecies(
    val pokemon_v2_pokemonspeciesnames: List<PokemonGraphQlPokemonSpeciesName>
)

data class PokemonGraphQlPokemonSpeciesName(
    val genus: String
)
