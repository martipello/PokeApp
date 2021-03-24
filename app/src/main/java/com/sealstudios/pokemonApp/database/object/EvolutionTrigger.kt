package com.sealstudios.pokemonApp.database.`object`

enum class EvolutionTrigger(val id: Int) {

    LEVEL_UP(id = 1),
    TRADE(id = 2),
    USE_ITEM(id = 3),
    SHED(id = 4),
    OTHER(id = 5);

}