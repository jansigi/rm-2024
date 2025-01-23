package ch.js.rm2024.repository

import java.util.*

data class Criterion(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var weight: Double,
)

data class Variant(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    val criteria: List<String>
)

data class AnalysisDTO(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val criteria: MutableList<Criterion> = mutableListOf(),
    val variants: MutableList<Variant> = mutableListOf()
)