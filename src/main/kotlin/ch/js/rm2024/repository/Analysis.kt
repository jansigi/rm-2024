package ch.js.rm2024.repository

import java.util.*

open class Analysis {
    private var entities = mutableMapOf<UUID, AnalysisDTO>()

    fun getAll(): List<AnalysisDTO> = entities.values.toList()

    fun remove(id: UUID) {
        entities.remove(id)
    }

    fun find(predicate: (AnalysisDTO) -> Boolean): AnalysisDTO? {
        return entities.values.find { predicate(it) }
    }

    fun updateTitle(id: UUID, newTitle: String) {
        remove(id)
        createAnalysis(newTitle)
    }

    fun new(title: String): AnalysisDTO {
        val newEntity = createAnalysis(title)
        return newEntity
    }

    private fun new(newEntity: AnalysisDTO) {
        entities[newEntity.id] = newEntity
    }

    private fun createAnalysis(title: String): AnalysisDTO {
        val analysisDTO = AnalysisDTO(UUID.randomUUID(), title, mutableListOf(), mutableListOf())
        new(analysisDTO)
        return analysisDTO
    }
}