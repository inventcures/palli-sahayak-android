package com.pallisahayak.feature.careteam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pallisahayak.core.data.database.dao.CareTeamMemberDao
import com.pallisahayak.core.data.database.entity.CareTeamMemberEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CareTeamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val careTeamDao: CareTeamMemberDao,
) : ViewModel() {

    private val patientId: String = savedStateHandle["patientId"] ?: ""

    val members = careTeamDao.getMembersByPatient(patientId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addMember(name: String, role: String, phone: String?, primaryContact: Boolean) {
        viewModelScope.launch {
            val member = CareTeamMemberEntity(
                memberId = UUID.randomUUID().toString(),
                patientId = patientId,
                name = name,
                role = role,
                phoneNumber = phone,
                primaryContact = primaryContact,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                syncStatus = "pending",
            )
            careTeamDao.upsert(member)
        }
    }
}
