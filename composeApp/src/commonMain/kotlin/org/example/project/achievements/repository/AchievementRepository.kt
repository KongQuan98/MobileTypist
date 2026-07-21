package org.example.project.achievements.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import org.example.project.achievements.engine.AchievementEngine
import org.example.project.achievements.events.AchievementEvent
import org.example.project.achievements.model.Achievement
import org.example.project.achievements.provider.AchievementDefinitions
import org.example.project.data.storage.StorageManager

class AchievementRepository(
    private val storageManager: StorageManager,
    private val engine: AchievementEngine = AchievementEngine()
) {
    private val _events = MutableSharedFlow<AchievementEvent>()
    val events = _events.asSharedFlow()

    val achievements: Flow<List<Achievement>> =
        combine(
            storageManager.achievementProgressFlow,
            storageManager.resultsFlow
        ) { progressMap, results ->
            val stats = engine.calculateStatistics(results, storageManager.getSocialShares())

            AchievementDefinitions.all.map { definition ->
                val progress = progressMap[definition.id]
                val evalResult = definition.requirement.evaluate(stats)

                Achievement(
                    id = definition.id,
                    title = definition.title,
                    description = definition.description,
                    icon = definition.icon,
                    hidden = definition.hidden,
                    progress = progress?.progress ?: evalResult.current,
                    target = evalResult.target,
                    unlocked = progress?.unlocked ?: evalResult.completed,
                    unlockedAt = progress?.unlockedAt
                )
            }.sortedWith(
                compareByDescending<Achievement> {
                    it.unlocked
                }.thenByDescending {
                    it.unlockedAt
                }.thenByDescending {
                    it.progress.toFloat() / it.target
                }
            )
        }

    suspend fun evaluate() {
        val results = storageManager.resultsFlow.value
        val stats = engine.calculateStatistics(results, storageManager.getSocialShares())
        val currentProgress = storageManager.getAchievementProgress()

        val updated = engine.evaluate(stats, currentProgress)

        if (updated.isNotEmpty()) {
            storageManager.saveAchievementProgress(updated)

            // Emit unlock events
            updated.filter { it.unlocked }.forEach { progress ->
                val definition = AchievementDefinitions.all.find { it.id == progress.achievementId }
                if (definition != null) {
                    val evalResult = definition.requirement.evaluate(stats)
                    _events.emit(
                        AchievementEvent.Unlocked(
                            Achievement(
                                id = definition.id,
                                title = definition.title,
                                description = definition.description,
                                icon = definition.icon,
                                hidden = definition.hidden,
                                progress = progress.progress,
                                target = evalResult.target,
                                unlocked = true,
                                unlockedAt = progress.unlockedAt
                            )
                        )
                    )
                }
            }
        }
    }
}
