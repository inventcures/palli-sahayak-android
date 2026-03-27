package com.pallisahayak.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pallisahayak.core.data.database.entity.CareTeamMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CareTeamMemberDao {
    @Query("SELECT * FROM care_team_members WHERE patientId = :patientId")
    fun getMembersByPatient(patientId: String): Flow<List<CareTeamMemberEntity>>

    @Query("SELECT * FROM care_team_members WHERE patientId = :patientId AND primaryContact = 1 LIMIT 1")
    suspend fun getPrimaryContact(patientId: String): CareTeamMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(member: CareTeamMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(members: List<CareTeamMemberEntity>)

    @Query("SELECT * FROM care_team_members WHERE syncStatus = 'pending'")
    suspend fun getPendingSync(): List<CareTeamMemberEntity>
}
