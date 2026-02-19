package com.jujodevs.pomodoro.libs.usagestats.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = UsageStatsEventEntity.TABLE_NAME)
internal data class UsageStatsEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val eventType: String,
    val phase: String?,
    val occurredAtMillis: Long,
    val durationMillis: Long?,
    val metadata: String,
) {
    companion object {
        const val TABLE_NAME = "usage_stats_events"
    }
}
