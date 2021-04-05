package com.sealstudios.pokemonApp.api.`object`

data class Ability(
        val id: Int,
        val name: String?,
        val is_main_series: Boolean?,
        val generation: NamedApiResource?,
        val names: List<Name>?,
        val effect_entries: List<VerboseEffect>?,
        val effect_changes: List<AbilityEffectChange>?,
        val flavor_text_entries: List<AbilityFlavorText>?,
        val pokemon: List<AbilityPokemon>?
)

data class AbilityEffectChange(
        val effect_entries: List<Effect>?,
        val version_group: NamedApiResource?
)

data class AbilityFlavorText(
        val flavor_text: String?,
        val language: NamedApiResource?,
        val version_group: NamedApiResource?
)

data class AbilityPokemon(
        val is_hidden: Boolean,
        val slot: Int,
        val pokemon: NamedApiResource?
)

data class Characteristic(
        val id: Int,
        val gene_modulo: Int,
        val possible_values: List<Int>,
        val descriptions: List<Description>
)

data class EggGroup(
        val id: Int,
        val name: String?,
        val names: List<Name>,
        val pokemon_species: List<NamedApiResource>
)

data class Gender(
        val id: Int,
        val name: String?,
        val pokemon_species_details: List<PokemonSpeciesGender>,
        val required_for_evolution: List<NamedApiResource>
)

data class PokemonSpeciesGender(
        val rate: Int,
        val pokemon_species: NamedApiResource?
)

data class GrowthRate(
        val id: Int,
        val name: String?,
        val formula: String?,
        val descriptions: List<Description>,
        val levels: List<GrowthRateExperienceLevel>,
        val pokemonSpecies: List<NamedApiResource>
)

data class GrowthRateExperienceLevel(
        val level: Int,
        val experience: Int
)

data class Nature(
        val id: Int,
        val name: String?,
        val decreased_stat: NamedApiResource?,
        val increased_stat: NamedApiResource?,
        val hates_flavor: NamedApiResource?,
        val likes_flavor: NamedApiResource?,
        val poke_athlon_stat_changes: List<NatureStatChange>,
        val move_battle_style_preferences: List<MoveBattleStylePreference>,
        val names: List<Name>
)

data class NatureStatChange(
        val maxChange: Int,
        val poke_athlon_stat: NamedApiResource?
)

data class MoveBattleStylePreference(
        val lowHpPreference: Int,
        val highHpPreference: Int,
        val moveBattleStyle: NamedApiResource?
)

data class PokeAthlonStat(
        val id: Int,
        val name: String?,
        val names: List<Name>,
        val affectingNatures: NaturePokeAthlonStatAffectSets?
)

data class NaturePokeAthlonStatAffectSets(
        val increase: List<NaturePokeAthlonStatAffect>,
        val decrease: List<NaturePokeAthlonStatAffect>
)

data class NaturePokeAthlonStatAffect(
        val maxChange: Int,
        val nature: NamedApiResource?
)

data class ApiPokemon(
        val id: Int,
        val url: String?,
        val name: String?,
        val baseExperience: Int?,
        val height: Int?,
        val isDefault: Boolean?,
        val order: Int?,
        val weight: Int?,
        val species: NamedApiResource?,
        val abilities: List<PokemonAbility>,
        val forms: List<PokemonForm>,
        val gameIndices: List<VersionGameIndex>,
        val heldItems: List<PokemonHeldItem>,
        val moves: List<PokemonMoveResponse>,
        val stats: List<PokemonStat>,
        val types: List<ApiPokemonType>,
        val sprites: PokemonSprites?,
)

data class PokemonSprites(
        val back_default: String?,
        val back_shiny: String?,
        val front_default: String?,
        val front_shiny: String?,
        val back_female: String?,
        val back_shiny_female: String?,
        val front_female: String?,
        val front_shiny_female: String?

)

data class PokemonAbility(
        val is_hidden: Boolean,
        val slot: Int,
        val ability: NamedApiResource?,
)

data class PokemonHeldItem(
        val item: NamedApiResource?,
        val versionDetails: List<PokemonHeldItemVersion>
)

data class PokemonHeldItemVersion(
        val version: NamedApiResource?,
        val rarity: Int
)

data class PokemonMoveResponse(
        val move: NamedApiResource?,
        val version_group_details: List<PokemonMoveVersion>
)

data class ApiPokemonMove(
        val id: Int,
        val name: String?,
        val accuracy: Int?,
        val pp: Int?,
        val power: Int?,
        val damage_class: NamedApiResource?,
        val effect_chance: Int,
        val priority: Int,
        val short_effect: String?,
        val generation: NamedApiResource?,
        val type: NamedApiResource?,
        val flavor_text_entries: List<FlavorText>?
)

data class EvolutionChain(
        val id: Int,
        val baby_trigger_item: NamedApiResource?,
        val chain: ChainLink
)

data class ChainLink(
        val is_baby: Boolean,
        val species: NamedApiResource?,
        val evolution_details: List<EvolutionDetails>,
        val evolves_to: List<ChainLink>
)

data class EvolutionDetails(
        val item: NamedApiResource?,
        val trigger: NamedApiResource?,
        val gender: Int,
        val held_item: NamedApiResource?,
        val known_move: NamedApiResource?,
        val known_move_type: NamedApiResource?,
        val location: NamedApiResource?,
        val min_level: Int,
        val min_happiness: Int,
        val min_beauty: Int,
        val min_affection: Int?,
        val needs_overworld_rain: Boolean,
        val party_species: NamedApiResource?,
        val party_type: NamedApiResource?,
        val relative_physical_stats: Int,
        val time_of_day: String?,
        val trade_species: NamedApiResource?,
        val turn_upside_down: Boolean
)

data class MoveLearnMethod(
        val id: Int,
        val name: String?,
        val accuracy: Int,
        val pp: Int,
        val power: Int,
        val priority: Int,
        val shortEffect: String?,
        val generation: List<NamedApiResource>,
        val category: List<NamedApiResource>,
        val type: List<NamedApiResource>,
        val flavorTextEntries: List<FlavorText>
)

data class PokemonMoveVersion(
        val move_learn_method: NamedApiResource?,
        val version_group: NamedApiResource?,
        val level_learned_at: Int
)

data class PokemonStat(
        val stat: NamedApiResource?,
        val effort: Int,
        val base_stat: Int
)

data class ApiPokemonType(
        val slot: Int,
        val type: NamedApiResource?
)

data class LocationAreaEncounter(
        val locationArea: NamedApiResource?,
        val versionDetails: List<VersionEncounterDetail>
)

data class PokemonColor(
        val id: Int,
        val name: String?,
        val names: List<Name>,
        val pokemonSpecies: List<NamedApiResource>
)

data class PokemonForm(
        val id: Int,
        val name: String?,
        val order: Int,
        val form_order: Int,
        val is_default: Boolean,
        val is_battle_only: Boolean,
        val is_mega: Boolean,
        val form_name: String?,
        val pokemon: NamedApiResource?,
        val version_group: NamedApiResource?,
        val sprites: PokemonFormSprites,
        val form_names: List<Name>,
        val names: List<Name>
)

data class PokemonFormSprites(
        val backDefault: String?,
        val backShiny: String?,
        val frontDefault: String?,
        val frontShiny: String?
)

data class PokemonHabitat(
        val id: Int,
        val name: String?,
        val names: List<Name>,
        val pokemonSpecies: List<NamedApiResource>
)

data class PokemonShape(
        val id: Int,
        val name: String?,
        val awesomeNames: List<AwesomeName>,
        val names: List<Name>,
        val pokemonSpecies: List<NamedApiResource?>
)

data class AwesomeName(
        val awesomeName: String?,
        val language: NamedApiResource?
)

data class ApiPokemonSpecies(
        val id: Int,
        val name: String?,
        val order: Int,
        val gender_rate: Int,
        val capture_rate: Int,
        val base_happiness: Int,
        val is_baby: Boolean,
        val hatch_counter: Int,
        val has_gender_differences: Boolean,
        val forms_switchable: Boolean,
        val growth_rate: NamedApiResource?,
        val pokedex_numbers: List<PokemonSpeciesDexEntry>,
        val egg_groups: List<NamedApiResource>,
        val color: NamedApiResource?,
        val shape: NamedApiResource?,
        val evolves_from_species: NamedApiResource?,
        val evolution_chain: ApiResource,
        val habitat: NamedApiResource?,
        val generation: NamedApiResource?,
        val names: List<Name>,
        val pal_park_encounters: List<PalParkEncounterArea>,
        val form_descriptions: List<Description>,
        val genera: List<Genus>,
        val varieties: List<PokemonSpeciesVariety>,
        val flavor_text_entries: ArrayList<PokemonSpeciesFlavorText>
)

data class PokemonSpeciesFlavorText(
        val flavor_text: String?,
        val language: NamedApiResource?,
        val version: NamedApiResource?
)

data class Genus(
        val genus: String?,
        val language: NamedApiResource?
)

data class PokemonSpeciesDexEntry(
        val entryNumber: Int,
        val pokedex: NamedApiResource?
)

data class PalParkEncounterArea(
        val baseScore: Int,
        val rate: Int,
        val area: NamedApiResource?
)

data class PokemonSpeciesVariety(
        val isDefault: Boolean,
        val pokemon: NamedApiResource?
)

data class Stat(
        val id: Int,
        val name: String?,
        val gameIndex: Int,
        val isBattleOnly: Boolean,
        val affectingMoves: MoveStatAffectSets?,
        val affectingNatures: NatureStatAffectSets?,
        val characteristics: List<ApiResource>,
        val moveDamageClass: NamedApiResource?,
        val names: List<Name>
)

data class MoveStatAffectSets(
        val increase: List<MoveStatAffect>,
        val decrease: List<MoveStatAffect>
)

data class MoveStatAffect(
        val change: Int,
        val move: NamedApiResource?
)

data class NatureStatAffectSets(
        val increase: List<NamedApiResource>,
        val decrease: List<NamedApiResource>
)

data class Type(
        val id: Int,
        val name: String?,
        val damage_relations: TypeRelations?,
        val game_indices: List<GenerationGameIndex>,
        val generation: NamedApiResource?,
        val move_damage_class: NamedApiResource?,
        val names: List<Name>,
)

data class TypePokemon(
        val slot: Int,
        val pokemon: NamedApiResource?
)

data class TypeRelations(
        val no_damage_to: List<NamedApiResource>,
        val half_damage_to: List<NamedApiResource>,
        val double_damage_to: List<NamedApiResource>,
        val no_damage_from: List<NamedApiResource>,
        val half_damage_from: List<NamedApiResource>,
        val double_damage_from: List<NamedApiResource>
)
