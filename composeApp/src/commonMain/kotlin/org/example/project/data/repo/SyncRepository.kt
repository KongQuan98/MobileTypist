package org.example.project.data.repo

import kotlinx.coroutines.flow.StateFlow

interface SyncRepository {
    val isSyncing: StateFlow<Boolean>
    suspend fun syncAll()
    suspend fun syncPending()
}
