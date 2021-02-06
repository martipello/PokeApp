package com.sealstudios.pokemonApp.ui.util

import android.annotation.SuppressLint
import java.lang.StringBuilder
import java.util.*

enum class PokemonGeneration() {
    GENERATION_I,
    GENERATION_II,
    GENERATION_III,
    GENERATION_IV,
    GENERATION_V,
    GENERATION_VI,
    GENERATION_VII,
    GENERATION_VIII,
    GENERATION_IX,
    GENERATION_X,
    UNKNOWN;

    companion object {

        const val itemType = 1003

        @SuppressLint("DefaultLocale")
        fun getGeneration(generationName: String): PokemonGeneration {
            return try {
                valueOf(generationName.replace('-', '_').toUpperCase())
            } catch (e: Exception) {
                UNKNOWN
            }
        }

        fun formatGenerationName(generation: PokemonGeneration): String {
            val splitGeneration = generation.name.split(regex = Regex("_"), limit = 2)
            return if (splitGeneration.size > 1) {
                val genName = splitGeneration[0].toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                val genNumber = splitGeneration[1].replace('_', ' ').toUpperCase(Locale.ROOT)
                "$genName $genNumber"
            } else {
                "Unknown"
            }
        }

        fun formatGenName(generation: String): String {
            val splitGeneration = generation.split(regex = Regex("-"))
            val generationStringBuilder = StringBuilder()
            for (split in splitGeneration) {
                generationStringBuilder.append(split
                    .toLowerCase(Locale.ROOT).capitalize(Locale.ROOT))
                generationStringBuilder.append(" ")
            }
            return generationStringBuilder.toString()
        }

        fun getVersionForGeneration(generation: PokemonGeneration): String{
            return when (generation) {
                GENERATION_I -> "Red and Blue"
                GENERATION_II -> "Gold and Silver"
                GENERATION_III -> "Ruby, Sapphire and Emerald"
                GENERATION_IV -> "Diamond, Pearl and Platinum"
                GENERATION_V -> "Black and White"
                GENERATION_VI -> "X and Y"
                GENERATION_VII -> "Sun and Moon"
                GENERATION_VIII -> "Sword and Shield"
                else -> "N/A"
            }
        }

    }

}