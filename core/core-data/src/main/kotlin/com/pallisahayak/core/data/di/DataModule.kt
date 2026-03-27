package com.pallisahayak.core.data.di

import android.content.Context
import androidx.room.Room
import com.pallisahayak.core.data.database.PalliSahayakDatabase
import com.pallisahayak.core.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PalliSahayakDatabase {
        val passphrase = net.sqlcipher.database.SQLiteDatabase.getBytes("palli-sahayak-dev".toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            PalliSahayakDatabase::class.java,
            "palli_sahayak.db",
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideUserDao(db: PalliSahayakDatabase): UserDao = db.userDao()
    @Provides fun providePatientDao(db: PalliSahayakDatabase): PatientDao = db.patientDao()
    @Provides fun provideObservationDao(db: PalliSahayakDatabase): ObservationDao = db.observationDao()
    @Provides fun provideMedicationReminderDao(db: PalliSahayakDatabase): MedicationReminderDao = db.medicationReminderDao()
    @Provides fun provideCareTeamMemberDao(db: PalliSahayakDatabase): CareTeamMemberDao = db.careTeamMemberDao()
    @Provides fun provideQueryCacheDao(db: PalliSahayakDatabase): QueryCacheDao = db.queryCacheDao()
    @Provides fun provideInteractionLogDao(db: PalliSahayakDatabase): InteractionLogDao = db.interactionLogDao()
    @Provides fun provideVignetteResponseDao(db: PalliSahayakDatabase): VignetteResponseDao = db.vignetteResponseDao()
}
