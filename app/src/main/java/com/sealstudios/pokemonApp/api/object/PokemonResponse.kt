package com.sealstudios.pokemonApp.api.`object`

data class Ability(
    val id: Int,
    val name: String,
    val isMainSeries: Boolean,
    val generation: NamedApiResource,
    val names: List<Name>,
    val effectEntries: List<VerboseEffect>,
    val effectChanges: List<AbilityEffectChange>,
    val flavorTextEntries: List<AbilityFlavorText>,
    val pokemon: List<AbilityPokemon>
)

data class AbilityEffectChange(
    val effectEntries: List<Effect>,
    val versionGroup: NamedApiResource
)

data class AbilityFlavorText(
    val flavorText: String,
    val language: NamedApiResource,
    val versionGroup: NamedApiResource
)

data class AbilityPokemon(
    val isHidden: Boolean,
    val slot: Int,
    val pokemon: NamedApiResource
)

data class Characteristic(
    val id: Int,
    val geneModulo: Int,
    val possibleValues: List<Int>,
    val descriptions: List<Description>
)

data class EggGroup(
    val id: Int,
    val name: String,
    val names: List<Name>,
    val pokemonSpecies: List<NamedApiResource>
)

data class Gender(
    val id: Int,
    val name: String,
    val pokemonSpeciesDetails: List<PokemonSpeciesGender>,
    val requiredForEvolution: List<NamedApiResource>
)

data class PokemonSpeciesGender(
    val rate: Int,
    val pokemonSpecies: NamedApiResource
)

data class GrowthRate(
    val id: Int,
    val name: String,
    val formula: String,
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
    val name: String,
    val decreasedStat: NamedApiResource?,
    val increasedStat: NamedApiResource?,
    val hatesFlavor: NamedApiResource?,
    val likesFlavor: NamedApiResource?,
    val pokeathlonStatChanges: List<NatureStatChange>,
    val moveBattleStylePreferences: List<MoveBattleStylePreference>,
    val names: List<Name>
)

data class NatureStatChange(
    val maxChange: Int,
    val pokeathlonStat: NamedApiResource
)

data class MoveBattleStylePreference(
    val lowHpPreference: Int,
    val highHpPreference: Int,
    val moveBattleStyle: NamedApiResource
)

data class PokeathlonStat(
    val id: Int,
    val name: String,
    val names: List<Name>,
    val affectingNatures: NaturePokeathlonStatAffectSets
)

data class NaturePokeathlonStatAffectSets(
    val increase: List<NaturePokeathlonStatAffect>,
    val decrease: List<NaturePokeathlonStatAffect>
)

data class NaturePokeathlonStatAffect(
    val maxChange: Int,
    val nature: NamedApiResource
)

data class Pokemon(
    val id: Int,
    val url: String,
    val name: String,
    val baseExperience: Int,
    val height: Int,
    val isDefault: Boolean,
    val order: Int,
    val weight: Int,
    val species: NamedApiResource,
    val abilities: List<PokemonAbility>,
    val forms: List<PokemonForm>,
    val gameIndices: List<VersionGameIndex>,
    val heldItems: List<PokemonHeldItem>,
    val moves: List<PokemonMoveResponse>,
    val stats: List<PokemonStat>,
    val types: List<PokemonType>,
    val sprites: PokemonSprites
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
    val id: Int,
    val name: String,
    val isHidden: Boolean,
    val slot: Int,
    val generation: List<NamedApiResource>,
    val effectEntries: List<Effect>
)

data class PokemonHeldItem(
    val item: NamedApiResource,
    val versionDetails: List<PokemonHeldItemVersion>
)

data class PokemonHeldItemVersion(
    val version: NamedApiResource,
    val rarity: Int
)

data class PokemonMoveResponse(
    val move: NamedApiResource,
    val version_group_details: List<PokemonMoveVersion>
)

data class PokemonMove(
    val id: Int,
    val name: String,
    val accuracy: Int,
    val pp: Int,
    val power: Int,
    val damage_class: NamedApiResource,
    val effect_chance: Int,
    val priority: Int,
    val shortEffect: String,
    val generation: List<NamedApiResource>,
    val category: List<NamedApiResource>,
    val type: List<NamedApiResource>,
    val flavorTextEntries: List<FlavorText>
)

data class EvolutionChain(
    val id: Int,
    val baby_trigger_item: String,
    val chain: List<ChainLink>
)

data class ChainLink(
    val isBaby: Boolean,
    val species: NamedApiResource,
    val evolution_details: String,
    val evolves_to: List<ChainLink>
)

data class MoveLearnMethod(
    val id: Int,
    val name: String,
    val accuracy: Int,
    val pp: Int,
    val power: Int,
    val priority: Int,
    val shortEffect: String,
    val generation: List<NamedApiResource>,
    val category: List<NamedApiResource>,
    val type: List<NamedApiResource>,
    val flavorTextEntries: List<FlavorText>
)

data class PokemonMoveVersion(
    val move_learn_method: NamedApiResource,
    val version_group: NamedApiResource,
    val level_learned_at: Int
)

data class PokemonStat(
    val stat: NamedApiResource,
    val effort: Int,
    val baseStat: Int
)

data class PokemonType(
    val slot: Int,
    val type: NamedApiResource
)

data class LocationAreaEncounter(
    val locationArea: NamedApiResource,
    val versionDetails: List<VersionEncounterDetail>
)

data class PokemonColor(
    val id: Int,
    val name: String,
    val names: List<Name>,
    val pokemonSpecies: List<NamedApiResource>
)

data class PokemonForm(
    val id: Int,
    val name: String,
    val order: Int,
    val form_order: Int,
    val is_default: Boolean,
    val is_battle_only: Boolean,
    val is_mega: Boolean,
    val form_name: String,
    val pokemon: NamedApiResource,
    val version_group: NamedApiResource,
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
    val name: String,
    val names: List<Name>,
    val pokemonSpecies: List<NamedApiResource>
)

data class PokemonShape(
    val id: Int,
    val name: String,
    val awesomeNames: List<AwesomeName>,
    val names: List<Name>,
    val pokemonSpecies: List<NamedApiResource>
)

data class AwesomeName(
    val awesomeName: String,
    val language: NamedApiResource
)

data class PokemonSpecies(
    val id: Int,
    val name: String,
    val order: Int,
    val gender_rate: Int,
    val capture_rate: Int,
    val base_happiness: Int,
    val is_baby: Boolean,
    val hatch_counter: Int,
    val has_gender_differences: Boolean,
    val forms_switchable: Boolean,
    val growth_rate: NamedApiResource,
    val pokedex_numbers: List<PokemonSpeciesDexEntry>,
    val egg_groups: List<NamedApiResource>,
    val color: NamedApiResource,
    val shape: NamedApiResource,
    val evolves_from_species: NamedApiResource?,
    val evolution_chain: ApiResource,
    val habitat: NamedApiResource?,
    val generation: NamedApiResource,
    val names: List<Name>,
    val pal_park_encounters: List<PalParkEncounterArea>,
    val form_descriptions: List<Description>,
    val genera: List<Genus>,
    val varieties: List<PokemonSpeciesVariety>,
    val flavor_text_entries: ArrayList<PokemonSpeciesFlavorText>
)

data class PokemonSpeciesFlavorText(
    val flavor_text: String,
    val language: NamedApiResource,
    val version: NamedApiResource
)

data class Genus(
    val genus: String,
    val language: NamedApiResource
)

data class PokemonSpeciesDexEntry(
    val entryNumber: Int,
    val pokedex: NamedApiResource
)

data class PalParkEncounterArea(
    val baseScore: Int,
    val rate: Int,
    val area: NamedApiResource
)

data class PokemonSpeciesVariety(
    val isDefault: Boolean,
    val pokemon: NamedApiResource
)

data class Stat(
    val id: Int,
    val name: String,
    val gameIndex: Int,
    val isBattleOnly: Boolean,
    val affectingMoves: MoveStatAffectSets,
    val affectingNatures: NatureStatAffectSets,
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
    val move: NamedApiResource
)

data class NatureStatAffectSets(
    val increase: List<NamedApiResource>,
    val decrease: List<NamedApiResource>
)

data class Type(
    val id: Int,
    val name: String,
    val damageRelations: TypeRelations,
    val gameIndices: List<GenerationGameIndex>,
    val generation: NamedApiResource,
    val moveDamageClass: NamedApiResource?,
    val names: List<Name>,
    val pokemon: List<TypePokemon>,
    val moves: List<NamedApiResource>
)

data class TypePokemon(
    val slot: Int,
    val pokemon: NamedApiResource
)

data class TypeRelations(
    val noDamageTo: List<NamedApiResource>,
    val halfDamageTo: List<NamedApiResource>,
    val doubleDamageTo: List<NamedApiResource>,
    val noDamageFrom: List<NamedApiResource>,
    val halfDamageFrom: List<NamedApiResource>,
    val doubleDamageFrom: List<NamedApiResource>
)
