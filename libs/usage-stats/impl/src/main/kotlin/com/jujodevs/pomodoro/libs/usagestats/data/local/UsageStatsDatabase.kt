package com.jujodevs.pomodoro.libs.usagestats.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UsageStatsEventEntity::class],
    version = USAGE_STATS_DATABASE_VERSION,
    exportSchema = true,
)
internal abstract class UsageStatsDatabase : RoomDatabase() {
    abstract fun usageStatsDao(): UsageStatsDao
}

private const val USAGE_STATS_DATABASE_VERSION = 1
