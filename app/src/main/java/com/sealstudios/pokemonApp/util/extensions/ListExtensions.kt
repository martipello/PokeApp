package com.sealstudios.pokemonApp.util.extensions

fun List<String>.names() {
    this.toSet().joinToString {
        it.capitalize()
    }
}
