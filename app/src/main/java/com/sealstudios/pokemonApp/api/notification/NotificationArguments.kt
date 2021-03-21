package com.sealstudios.pokemonApp.api.notification

data class NotificationArguments(
        val id: Int,
        val title: String,
        val progressText: String,
        val progress: Int,
        val progressMax: Int,
        val indeterminate: Boolean = false
)

fun NotificationArguments.isPriority(): Boolean {
    return (progress != progressMax)
}
