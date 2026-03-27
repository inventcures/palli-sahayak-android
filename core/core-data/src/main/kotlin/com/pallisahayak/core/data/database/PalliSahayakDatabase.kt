package com.pallisahayak.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pallisahayak.core.data.database.dao.*
import com.pallisahayak.core.data.database.entity.*

@Database(
    entities = [
        UserEntity::class,
        PatientEntity::class,
        ObservationEntity::class,
        MedicationReminderEntity::class,
        CareTeamMemberEntity::class,
        QueryCacheEntity::class,
        InteractionLogEntity::class,
        VignetteResponseEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class PalliSahayakDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun patientDao(): PatientDao
    abstract fun observationDao(): ObservationDao
    abstract fun medicationReminderDao(): MedicationReminderDao
    abstract fun careTeamMemberDao(): CareTeamMemberDao
    abstract fun queryCacheDao(): QueryCacheDao
    abstract fun interactionLogDao(): InteractionLogDao
    abstract fun vignetteResponseDao(): VignetteResponseDao
}
