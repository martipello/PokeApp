package com.sealstudios.pokemonApp.api.`object`

interface ResourceSummary {
    val id: Int
    val category: String
    val url: String
}

data class ApiResource(
    override val category: String,
    override val id: Int, override val url: String
) : ResourceSummary

data class NamedApiResource(
    val name: String,
    override val category: String,
    override val id: Int, override val url: String
) : ResourceSummary

interface ResourceSummaryList<out T : ResourceSummary> {
    val count: Int
    val next: String?
    val previous: String?
    val results: List<T>
}

data class ApiResourceList(
    override val count: Int,
    override val next: String?,
    override val previous: String?,
    override val results: List<ApiResource>
) : ResourceSummaryList<ApiResource>

data class NamedApiResourceList(
    override val count: Int,
    override val next: String?,
    override val previous: String?,
    override val results: List<NamedApiResource>
) : ResourceSummaryList<NamedApiResource>